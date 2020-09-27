package mainpackage;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.ResultSet;

import it.MirkoGiacchini.GameSocket.Server.GameServer;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import com.MirkoGiacchini.game.MultiplayerProtocol;
import com.MirkoGiacchini.gameserver.CmdProtocol;

public class MainServer extends JFrame
{
  private static final long serialVersionUID = 1L;
  private static final int PORT = 2453;
  private static final String PL_OBJ_RELATIONS = "pl_obj_relations";
  private static final String ITEM_TABLE = "items";
  private static final String GAME_NAME = "Mager";
  
  GameServer server;
  
  JTextArea textArea;
  JTextField field;
  
  boolean onlyTcp = false; //devo usare solo il tcp?
  boolean debug = true; //modalità debug? (stampo i messaggi)
  
  public MainServer()
  {
	super("Server "+GAME_NAME);
	
	textArea = new JTextArea();
	textArea.setEditable(false);
	add(new JScrollPane(textArea));
	
	field = new JTextField();
	field.addActionListener(new ActionListener()
	{
	  @Override
	  public void actionPerformed(ActionEvent evt) 
	  {
	    String cmd = evt.getActionCommand();
	    addMessage(cmd);
	    
	    if(cmd.trim().equalsIgnoreCase(CmdProtocol.ENABLE_ONLY_TCP))
	     onlyTcp = true; else
	    if(cmd.trim().equalsIgnoreCase(CmdProtocol.DISABLE_ONLY_TCP))
	     onlyTcp = false; else
	    if(cmd.trim().equalsIgnoreCase(CmdProtocol.ENABLE_DEBUG))
	     debug = true; else
	    if(cmd.trim().equalsIgnoreCase(CmdProtocol.DISABLE_DEBUG))
		 debug = false; else
	    addMessage("Unknown command");
	    
		field.setText("");
	  }
	});
	
	add(field, BorderLayout.SOUTH);
	
	setBounds(0, 0, 300, 300);
	setVisible(true);
	
	this.addWindowListener(new WindowAdapter()
	{
	  @Override
	  public void windowClosing(WindowEvent evt)
	  {
		close();
	  }
	});
	
	try
	{
      createServer();
	  if(!server.open())
	  {
		JOptionPane.showMessageDialog(this, "Error opening server!", "Error!", JOptionPane.ERROR_MESSAGE);	
		close();  
	  }
	}
	catch(Exception e)
	{
	  JOptionPane.showMessageDialog(this, "Exception: "+e.getMessage(), "Error!", JOptionPane.ERROR_MESSAGE);	
	  close();
	}
  }
  
  private void close()
  {
	if(server != null)  
	 server.disconnect();
	dispose();
	System.exit(0); 
  }
  
  protected void addMessage(String message)
  {
	SwingUtilities.invokeLater(new Runnable() //thread-safe
	{
	  public void run()
	  {	  
		if(debug)  
	     textArea.append(message+"\n");    
	  }
    });    
  }
  
  protected void createServer() throws Exception
  {
	server = new GameServer(PORT, 100, GAME_NAME, "users", "username", "password")
	{
	  @Override
	  public void onMessageTcpReceived(Object obj, int id) //ricevuto tcp non standard
	  {
		String str = (String)obj;
		if(str.startsWith(MultiplayerProtocol.RESET_ITEMS) || str.startsWith(MultiplayerProtocol.START_GAME_TIME))
		 super.sendObjectTcp(obj, Integer.parseInt(str.split(" ")[1]));
		else
		 super.broadcastTcpOnRoom(obj, id);
	  }
		  
	  @Override
	  public void onMessageUdpReceived(byte data[], int id) //ricevuto udp non standard
	  {
		if(!onlyTcp)  
		 super.broadcastUdpOnRoom(data, id); //faccio il broadcast alla stanza  
		else
		 super.broadcastTcpOnRoom(new String(data).trim(), id);
	  }
	  
	  @Override
	  protected synchronized void changeRoom(int id, int oldRoom, int newRoom) throws Exception //un client ha cambiato stanza...
	  {
		super.changeRoom(id, oldRoom, newRoom);  
		if(oldRoom != DEFAULT_ROOM) //devo dire ai client della stanza che 'id' è uscito
		 synchronized(rooms)
		 {
		   sendObjectTcpToRoom(MultiplayerProtocol.REMOVE_PLAYER_FROM_GAME+" "+id, oldRoom);	
		 }
	  }
	  
	  @Override
	  public void onClientConnected(int id)
	  {
	    super.onClientConnected(id);
	    addMessage("Client "+id+" connesso");	
	  }
		
	  @Override
	  public void onServerOpened()
	  {
	    super.onServerOpened();
	    addMessage("Server aperto");	
	  }
		
	  @Override
	  public void onClientDisconnected(int id, int room)
	  {
	    super.onClientDisconnected(id, room);
	    addMessage("Client "+id+" disconesso, ultima stanza: "+room);
	  }
		
	  @Override
	  public void roomUpdated(int room)
	  {
	    super.roomUpdated(room);
	    addMessage(this.rooms.get(room).a.size()+" utenti connessi su stanza "+room);
	  }
		
	  @Override
	  public void roomRemoved(int room)
	  {
	    super.roomRemoved(room);
	    addMessage("stanza "+room+" rimossa");
	  }
		
	  @Override
	  public void onSpecialCommandReceived(Object obj, int id)
	  {
	    super.onMessageTcpReceived(obj, id);
	    if(obj instanceof String)
	    {
	  	  String str = (String)obj;
		  addMessage("Tcp da "+id+":"+str);
		}
	  }
		
	  @Override
	  public void onDeletingAccount(String username)
	  {
		/*try
		{
		  executeUpdate("DELETE FROM "+PL_OBJ_RELATIONS+" WHERE player = '"+username+"'");	
		}catch(Exception e){}*/
	  }
	  
	  @Override
	  public boolean initDB() //metodo incaricato di creare le tabelle del db
	  {
		super.initDB();  
		try
		{
		  executeUpdate("CREATE TABLE "+ITEM_TABLE+" (id int PRIMARY KEY, description varchar(30))");  //tabella item
		  
		  insertItems();
		  
		  executeUpdate("CREATE TABLE "+PL_OBJ_RELATIONS+" (id int AUTO_INCREMENT PRIMARY KEY, player varchar(30) collate utf8_bin NOT NULL, obj int NOT NULL," //relazione player-items
		  		        + "foreign key(player) references "+table+" ("+unc+") on delete cascade, foreign key(obj) references "+ITEM_TABLE+"(id) )"); 
		  executeUpdate("create index i"+PL_OBJ_RELATIONS+" on "+PL_OBJ_RELATIONS+"(player)");
		}catch(Exception e){ }
		return true;
      }
	  
	  @Override
	  public String getRecord(String username, String psw)
	  {
		String record = super.getRecord(username, psw);
		if(record != "")
		{
		  try
		  {
	        ResultSet rset = db.executeQuery("SELECT obj FROM "+PL_OBJ_RELATIONS+" WHERE player='"+username+"'");
	        
	        while(rset.next())
	         record += rset.getString(1)+" ";	
		  }catch(Exception e){}
		}
		return record;
	  }
	  
	  @Override
	  public String[] createUpdateQuery(String q)
	  {
		String data[] = q.split(" "); //record standard: nome, lv, exp, pen, serie di item
		String ris[] = new String[data.length-3];
		ris[0] = "UPDATE "+table+" SET level='"+data[1]+"', exp='"+data[2]+"', pen='"+data[3]+"' WHERE "+unc+"='"+data[0]+"'";
		for(int i=4; i<data.length; i++)
		 ris[i - 3] = "INSERT INTO "+PL_OBJ_RELATIONS+" VALUES (NULL, '"+data[0]+"', '"+data[i]+"')";
		return ris;
	  }
	  
	  @Override
	  public void onExceptionCaught(Exception e)
	  {
		//addMessage("Exception: "+e.getMessage());  
	    //e.printStackTrace();
	  }
	  
	  private void insertItems()
	  {
		try
		{
		  executeUpdate("insert into "+ITEM_TABLE+" values(0, 'hp')");	 	
		  executeUpdate("insert into "+ITEM_TABLE+" values(1, 'crit')");	 
		  executeUpdate("insert into "+ITEM_TABLE+" values(2, 'perf')");	 
		  executeUpdate("insert into "+ITEM_TABLE+" values(3, 'boss')");	 
		  executeUpdate("insert into "+ITEM_TABLE+" values(4, 'dark pow')");	 
		  executeUpdate("insert into "+ITEM_TABLE+" values(5, 'dark res')");	 
		  executeUpdate("insert into "+ITEM_TABLE+" values(6, 'light pow')");	 
		  executeUpdate("insert into "+ITEM_TABLE+" values(7, 'light res')");	 
		  executeUpdate("insert into "+ITEM_TABLE+" values(8, 'fire pow')");	 
		  executeUpdate("insert into "+ITEM_TABLE+" values(9, 'fire res')");	 
		  executeUpdate("insert into "+ITEM_TABLE+" values(10, 'ice pow')");	 
		  executeUpdate("insert into "+ITEM_TABLE+" values(11, 'ice def')");	 
		  executeUpdate("insert into "+ITEM_TABLE+" values(12, 'lightning pow')");	 
		  executeUpdate("insert into "+ITEM_TABLE+" values(13, 'lightning def')");	 
		  executeUpdate("insert into "+ITEM_TABLE+" values(14, 'chaos pow')");	 
		  executeUpdate("insert into "+ITEM_TABLE+" values(15, 'chaos def')");	 
		}catch(Exception e){ }
	  }
	};	  
  }
}
