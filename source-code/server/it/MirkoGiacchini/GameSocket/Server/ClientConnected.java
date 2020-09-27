package it.MirkoGiacchini.GameSocket.Server;

import it.MirkoGiacchini.GameSocket.Client.Client;
import it.MirkoGiacchini.GameSocket.Client.DBClient;
import it.MirkoGiacchini.GameSocket.Client.GameClient;
import it.MirkoGiacchini.crypto.AES;
import it.MirkoGiacchini.crypto.Cipher;
import it.MirkoGiacchini.pairing.Triple;
import it.MirkoGiacchini.util.Util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashSet;
import java.util.Map.Entry;

/**
 * client connesso al server
 * @author Mirko
 *
 */
public class ClientConnected 
{
  /** stream per ricevere messaggi TCP dal client */
  protected ObjectInputStream inputStream;
  
  /** stream per inviare messaggi TCP al client */
  protected ObjectOutputStream outputStream;
  
  /** connessione con server */
  protected Socket connection;
  
  /** id client */
  protected int id;
	
  /** client ancora connesso? */
  protected boolean connected;
  
  /** server a cui il client è connesso */
  protected Server server;
  
  /**stanza del client */
  public int room;
  
  /** ip del client */
  protected InetAddress ip;
  
  /** porta sulla quale è connesso il client */
  protected int port;
  
  /** master della stanza?*/
  protected boolean master;
  
  /**usato solo in caso di DBServer... è l'username del client connesso*/
  String username;
  
  /**ultima volta che il client ha rinnovato il timer*/
  long lastRenew;
  
  /**chiave negoziata da algoritmo diffie-hellman*/
  int diffieKey;
  int tmpX;
  /**cifrario per criptare usando la chiave diffie-hellman*/
  Cipher msgCiph;
  
  /** 
   * crea client connesso
   * @param connection connessione con server
   * @param id id del client
   * @param server
   * @throws IOException 
   */
  public ClientConnected(Socket connection, int id, Server server, Cipher c) throws Exception
  {
	msgCiph = c;
	this.connection = connection;
	this.server = server;
	this.id = id;
	outputStream = new ObjectOutputStream(connection.getOutputStream());
	outputStream.flush();
	inputStream = new ObjectInputStream(connection.getInputStream());
	connected = true;
	room = Server.DEFAULT_ROOM;
	ip = null;
	port = -1;
	username = "-";
	master = false;
	lastRenew = System.currentTimeMillis();

	int n = Util.getRandomNumber(2, 10);
	int x = Util.getRandomNumber(1, 17);
	int p = Util.getRandomPrime();
	int A = Util.fastModPow(n, x, p); //(int)(((long)Math.pow((long)n, (long)x))%(long)p); //(n^x)%p
	sendObjectTcp((Object)(Client.BEGIN_INIT_DIFFIE+" "+n+" "+p+" "+A), false); //inizio scambio chiavi
	tmpX = x;
	diffieKey = 0;
	
	getMessagesTcp.start();
	checkTimer.start();
  }
  
  public ClientConnected(Socket connection, int id, Server server) throws Exception
  {
	this(connection, id, server, new AES());    
	//this(connection, id, server, new Xor(0));  
  }
  
  /** thread che riceve messaggi TCP dal client */
  protected Thread getMessagesTcp = new Thread()
  {
	@Override
	public void run()
	{
	  Object obj;
	  
	  while(connected && server.isOpen) //finchè il client è connesso
	  {
		try
		{
		  synchronized(inputStream)
		  {
		    obj = inputStream.readObject(); //ricevo oggetto 
		    obj = msgCiph.decrypt((String)obj);
		    //obj = Util.xorStr((String)obj, diffieKey);
		    
		    if(connected && server.isOpen)
		     if(!checkSpecialCommands(obj)) //se non è un comando speciale
		      server.onMessageTcpReceived(obj, id); //se ne occupa il server
		     else
		      server.onSpecialCommandReceived(obj, id);
		  }
		}catch(Exception e){ server.onExceptionCaught(e); }
	  }
	}
  };
  
  
  public void sendObjectTcp(Object obj) throws Exception
  {
	sendObjectTcp(obj, true);  
  }
  
  /**
   * invia un oggetto al client con protocollo TCP
   * @param obj oggetto da inviare
   * @param crypto usare diffie?
   */
  public void sendObjectTcp(Object obj, boolean crypto) throws Exception
  {
	if(crypto) 
	 obj = msgCiph.encrypt((String)obj);
	 //obj = Util.xorStr((String)obj, diffieKey);
	
	synchronized(outputStream)
	{
  	  outputStream.writeObject(obj);
	  outputStream.flush();
	}
  }
  
  /**
   * controlla comandi speciali
   * @return true se si trattava di un comando speciale, false altrimenti
   */
  protected synchronized boolean checkSpecialCommands(Object obj) throws Exception
  {
	if(obj instanceof String)
	{
	  final String s = (String)obj;
	 
	  if(s.startsWith(Client.END_INIT_DIFFIE))
	  {
		String data[] = s.split(" ");
		int B = Integer.parseInt(data[1]);
		int p = Integer.parseInt(data[2]);
		int x = tmpX;
		diffieKey = Util.fastModPow(B, x, p); //(int)(((long)Math.pow((long)B, (long)x))%(long)p); //(B^x)%p -> chiavi scambiate
		msgCiph.setKey(diffieKey);
		this.sendObjectTcp(Client.DIFFIE_COMPLETED);
		return true;
	  }
	  
	  if(s.equals(Client.DISCONNECT_COMMAND)) //se è un comando di disconnessione
	  { 
		//il client viene disconnesso 
		disconnect(); //disconnetto
		server.onClientDisconnected(id, room); //chiamo il metodo del server per ulteriori istruzioni
		return true;  //restituisco true -> è un comando speciale
	  }
	  
	  if(s.equals(Server.RENEW_TIME)) //client ha rinnovato il timer
	  {
	    lastRenew = System.currentTimeMillis();  
	    return true;
	  }
	  
	  if(s.startsWith(Client.CHANGE_ROOM_COMMAND)) //se è un comando per cambiare stanza
	  {
		String data[] = s.split(" "); //comando di cambio stanza = "#room id_nuova_stanza" oppure "#room id_nuova_stanza massima_dim_stanza descrizione"
		int newRoom = Integer.parseInt(data[1]); //prendo nuovo id della stanza
		
		if(newRoom == Server.AUTO_ROOM)
		 newRoom = server.getNewRoomId();
		
		if(server.canChangeRoom(id, newRoom)) //cambio stanza
		{
		  server.changeRoom(id, room, newRoom); //cambio stanza nel server
		  room = newRoom; //aggiorno valore
		  if(data.length > 2)
		  {
		    server.rooms.get(room).b = Integer.parseInt(data[2]); //setto dimensione massima stanza
		    server.rooms.get(room).c = data[3]; //setto descrizione stanza ("" se non è stata inviata)
		  }
		  this.sendObjectTcp(Server.CHANGE_ROOM+" "+newRoom+" "+server.rooms.get(room).b+" "+server.rooms.get(room).c); //dico al client di cambiare stanza
		}
		else
		 this.sendObjectTcp(Server.NO_CHANGE_ROOM); //impossibile cambiare stanza
		return true;  
	  }
	  
	  if(s.equals(Client.GET_ID_COMMAND)) //se è il comando che richiede l'id
	  {
		sendObjectTcp(Client.ID_COMMAND+" "+id);
		return true;  
	  }
	  
	  if(server instanceof DBServer) //richiesta database
	  {
	    if(s.startsWith(DBClient.REQ_CONNECTION)) //richiesta connessione a db
	    { 
		  String data[] = s.split(" "); //#comando username password
		  final String username = data[1]; //username
		  final String psw = data[2]; //psw criptata	
		  
		  new Thread() //non blocco il server... se la query è lenta ne risente l'intero gioco
		  { 
			@Override
			public void run()
			{
			  try
			  {
				String record = "";
				
				synchronized(((DBServer)server).db)
				{
		          record = ((DBServer)server).getRecord(username, psw);
				}
				
		        if(!record.equals("")) //connessione avvenuta
		        { 
		          synchronized(((DBServer)server).usersConnected)
		          {
		            if(((DBServer)server).usersConnected.containsKey(username)) //utente già connesso
		            {
		              ClientConnected.this.sendObjectTcp(DBServer.CONN_FAIL); //non posso connettere, ma faccio sloggare chi è connesso
		              if(((DBServer)server).usersConnected.get(username) != -1)
		              {
		            	((DBServer)server).sendObjectTcp(DBServer.ACC_LOGOUT, ((DBServer)server).usersConnected.get(username));  
		            	((DBServer)server).usersConnected.put(username, -1);  
		              }
		            }
		            else
		            {
		              ClientConnected.this.sendObjectTcp(DBServer.CONN_OK+"^"+record);
		              ((DBServer)server).usersConnected.put(username, id);
		              ClientConnected.this.username = username;
		            }
		          }
		        }
		        else //connessione non avvenuta
		         ClientConnected.this.sendObjectTcp(DBServer.CONN_FAIL);
			  }catch(Exception e){}
			}
		  }.start();
		  
		  return true;
	    }
	    
	    if(s.startsWith(DBClient.REQ_REGISTRATION)) //richiesta registrazione
	    {
		  String data[] = s.split(" "); //#comando username password
		  final String username = data[1]; //username
		  final String psw = data[2]; //psw criptata 
		  
	      new Thread() //non viene bloccato il server
	      {
	    	public void run()
	    	{
	          try
	          {  
	    	    synchronized(((DBServer)server).db)
	    	    {
	              ((DBServer)server).db.executeUpdate(((DBServer)server).createInsertQuery(username, psw));
	    	    }
	            sendObjectTcp(DBServer.REG_OK);
	          }
	          catch(Exception e) 
	          {
	        	try { sendObjectTcp(DBServer.REG_FAIL); }catch(Exception e2) {}
	          }
	    	}
	      }.start();
	      return true;
	    }
	    
	    if(s.startsWith(DBClient.DEL_ACCOUNT)) //eliminazione account
	    {
		  String data[] = s.split(" "); //#comando username password
		  final String username = data[1]; //username
		  final String psw = data[2]; //psw criptata	
		  
	      new Thread() //non viene bloccato il server 
	      {
	    	@Override
	    	public void run()
	    	{
	          try
	          {
	        	synchronized(((DBServer)server).db)
	        	{
	    	      if(((DBServer)server).db.executeUpdate("DELETE FROM "+((DBServer)server).table+" WHERE "+((DBServer)server).unc+"='"+username+"' AND "+((DBServer)server).pwc+"='"+psw+"'") > 0)
	    	      {
	    		    ((DBServer)server).onDeletingAccount(username);  
		            sendObjectTcp(DBServer.DEL_OK);   
	    	      }
	      	      else
	    	       sendObjectTcp(DBServer.DEL_FAIL);
	        	}
	          }
	          catch(Exception e) 
	          { 
	        	try { sendObjectTcp(DBServer.DEL_FAIL); }catch(Exception e2) {}
	          }
	        }
	      }.start();
	      return true;
	    }
	    
	    if(s.startsWith(DBClient.DB_UPDATE)) //aggiorna record
	    {
	      final String record = s.split("\\^")[1];
	      new Thread()
	      {
	    	@Override
	    	public void run()
	    	{
	    	  synchronized(((DBServer)server).db)
	    	  {
	    		try
	    		{
	    		  String queries[] = ((DBServer)server).createUpdateQuery(record);
	    		  for(String s : queries)
	    		   ((DBServer)server).db.executeUpdate(s);  
	    		}catch(Exception e){ }
	    	  }
	    	}
	      }.start();
	      return true; 	
	    }
	    
	    if(s.equals(DBClient.DB_LOGOUT)) //logout
	    {
	      new Thread()
	      {
	    	@Override
	    	public void run()
	    	{
	          logout();
	    	}
	      }.start();
	      return true;	
	    }
	  }
	  
	  if(server instanceof GameServer) //richieste gioco
	  {
		if(s.equals(GameClient.REQ_R)) //il client richiede le stanze
		{
		  new Thread()
		  {
			@Override
			public void run()
			{
			  synchronized(server.rooms)
			  {
				for(Entry<Integer, Triple<HashSet<Integer>, Integer, String>>e : server.rooms.entrySet())
				 try{ ClientConnected.this.sendObjectTcp(GameServer.ROOM_INFO+" "+((GameServer)server).getRoomString(e.getKey())); }catch(Exception e2){}
			  }
			}
		  }.start();
		  return true;
		}
	  }
	}
	return false;
  }
  
  /**thread che controlla che il timer non sia scaduto*/
  protected Thread checkTimer = new Thread()
  {
	@Override
	public void run()
	{
      while(true)
      {
	    if(System.currentTimeMillis() - lastRenew >= Server.MAX_TIME * 1000) //scaduto tempo di rinnovo
	    {
	      try
	      {
	        ClientConnected.this.sendObjectTcp(Server.TIME_EXPIRED); //dico che il tempo è finito
	        disconnect(); //disconnetto
	        server.onClientDisconnected(id, room);
	      }catch(Exception e){}
	    }
	    try { sleep(Server.MAX_TIME / 2 * 1000); }catch(Exception e){}
      }
	}
  };
  
  /** setta il client a master/non master*/
  protected void setMaster(boolean b) throws Exception
  {
	master = b;
	if(master)
	 this.sendObjectTcp(Server.MASTER);
	else
	 this.sendObjectTcp(Server.NOT_MASTER);
  }
  
  /**esegue logout dall'account, in caso di server DB*/
  protected void logout()
  {
	if(server instanceof DBServer && username != "-")  
	 synchronized(((DBServer)server).usersConnected)
	 {
	   ((DBServer)server).usersConnected.remove(username);  
	 }
	try { server.changeRoom(id, room, -1); }catch(Exception e){}
	username = "-";
  }
  
  public int getDiffieKey()
  {
	return diffieKey;  
  }
  
  /**
   * disconnette il client
   * @throws Exception
   */
  public void disconnect() throws Exception
  {
	connected = false;
	logout();
	try { getMessagesTcp.interrupt(); }catch(Exception e) { server.onExceptionCaught(e); }
	try { checkTimer.interrupt(); }catch(Exception e) { server.onExceptionCaught(e); }
	inputStream.close();
	outputStream.close();
	connection.close();
  }
}
