package it.MirkoGiacchini.GameSocket.Client;

import it.MirkoGiacchini.GameSocket.Server.Server;
import it.MirkoGiacchini.crypto.AES;
import it.MirkoGiacchini.crypto.Cipher;
import it.MirkoGiacchini.util.Util;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

/**
 * client usato nei videogiochi
 * @author Mirko
 *
 */
public class Client 
{
  //i comandi vengono inviati e ricevuti tramite protocollo TCP
	
  /** comando inviato dal client per disconnettersi */
  public static final String DISCONNECT_COMMAND = "#quit";
  
  /** comando inviato dal server per comunicare l'id del client: #id id_client*/
  public static final String ID_COMMAND = "#id";
  
  /** comando inviato dal client per cambiare stanza -> #room nuovo_id_room*/
  public static final String CHANGE_ROOM_COMMAND = "#room";
  
  /** comando inviato dal client per ricevere l'id dal server*/
  public static final String GET_ID_COMMAND = "#getid";
  
  /** stringa inviata dal client al server per permettergli di registrare l'ip e la porta udp del client. NOTA: l'invio di questo primo messaggio potrebbe non arrivare al server,
   * il server registrerà comunque il client al primo messaggio ricevuto */
  public static final String FIRST_UDP_STR = "#initudp";
  
  /**messaggio per iniziare lo scambio di chiavi (inviato dal server): #diffiein random_number random_prime_number public_key1*/
  public static final String BEGIN_INIT_DIFFIE = "#diffiein";
  
  /**messaggio per completare lo scambio di chiavi (inviato dal client): #diffieout public_key2 random_prime_number(lo stesso inviato dal server)*/
  public static final String END_INIT_DIFFIE = "#diffieout";
  
  /**scambio di chiavi diffie-hellman completato*/
  public static final String DIFFIE_COMPLETED = "#diffieend";
  
  /** porta sulla quale connettersi -> stessa del server */
  protected int port;
  
  /** ip del server a cui connettersi */
  protected InetAddress ip;
  
  /** ip come stringa*/
  protected String ipStr;
  
  /** connessione con server TCP */
  protected Socket connection;
  
  /** stream per inviare messaggi TCP al server */
  protected ObjectOutputStream outputStream;
  
  /** stream per ricevere messaggi TCP dal server */
  protected ObjectInputStream inputStream;
  
  /** socket per interazioni con server UDP */
  protected DatagramSocket socketUdp;
  
  /** id del client */
  protected int id;
  
  /** stanza sulla quale è connesso il client -> se la stanza è diversa da Server.DEFAULT_ROOM si può usare l'udp*/
  protected int room;
  
  /** client connesso? */
  protected boolean connected;
  
  /** master della stanza? */
  protected boolean master;
  
  /** stringa contenente informazioni sulla disconnessione del client */
  protected String infoStrDisconnection = "Disconnected";
  
  /**ultima volta che ho rinnovato il timer?*/
  long lastRenew;
  
  /**chiave negoziata dall'algoritmo diffie-hellman*/
  int diffieKey;
  
  /**cifrario per criptare i pacchetti, con chiave diffie-hellman*/
  Cipher msgCiph;
  
  public Client(int port, String ip, Cipher c)
  {
	msgCiph = c;  
	setPortIp(port, ip);
	connected = false;
	master = false;
	lastRenew = -1;
	diffieKey = 0;
  }
  
  /** 
   * crea client sulla porta 'port' e ip 'ip' 
   * @throws Exception se l'ip non è costruito correttamente
   */
  public Client(int port, String ip)
  {
	this(port, ip, new AES());    
	//this(port, ip, new Xor(0));   
  }
  
  /**
   * si connette al server
   * @return true se la connessione avviene, false altrimenti
   */
  public boolean connect()
  {
	if(!connected)
	{
	  id = -1; //id solo positivi -> -1 -> non ha un'id
	  try
	  {
		ip = InetAddress.getByName(ipStr);
		socketUdp = new DatagramSocket();  
	    connection = new Socket(ip, port); //si connette al server con porta e ip specificati	
	    outputStream = new ObjectOutputStream(connection.getOutputStream()); //crea stream output
	    outputStream.flush();
	    inputStream = new ObjectInputStream(connection.getInputStream()); //crea stream input
	 
	    connected = true;
	    onConnected();
	    
	    room = Server.DEFAULT_ROOM;
	 
	    getMessagesTcp.start(); //inizio a ricevere messaggi tcp
	    getMessagesUdp.start(); //inizio a ricevere messaggi udp
	    renewTimer.start(); //inizio a rinnovare il timer quando necessario
	  }catch(Exception e){ onExceptionCaught(e); return false; }
	}
	
	return true;  
  }
  
  protected void requestId()
  {
	sendObjectTcp(GET_ID_COMMAND); //chiede al server di mandargli l'id  
  }
  
  /**
   * thread che riceve messaggi tcp dal server
   */
  protected Thread getMessagesTcp = new Thread()
  {
	@Override
	public void run()
	{
	  Object obj = null;
	  
	  while(connected) //finchè sono connesso ricevo messaggi
	  {
		try
		{
		  synchronized(inputStream)
		  {
		    obj = inputStream.readObject(); //leggo oggetto
		    obj = msgCiph.decrypt((String)obj);
		
		    if(connected) 
		     if(!checkSpecialCommands(obj)) //se non è un comando speciale
			  onObjectTcpReceived(obj); //delego le operazioni al metodo
		  }
		}catch(Exception e){ onExceptionCaught(e); }
	  }
	}
  };
  
  /**
   * thread che riceve messaggi udp dal server
   */
  protected Thread getMessagesUdp = new Thread()
  {
	@Override
	public void run()
	{
	  DatagramPacket packet;
	  
	  while(connected) //finchè sono connesso
	  { 
		try
		{
		  synchronized(socketUdp)
		  {
		    byte data[] = new byte[1024];
		    packet = new DatagramPacket(data, data.length);
		    
		    socketUdp.receive(packet); //ricevo pacchetti
		    //String str = Util.xorStr(new String(packet.getData(), 0, packet.getLength(), "UTF-8").trim(), diffieKey);
		    String str = msgCiph.decrypt(new String(packet.getData(), 0, packet.getLength(), "UTF-8").trim());
		    onDataUdpReceived(str.getBytes()); //lascio la gestione dei dati al metodo
		  }
		}catch(Exception e) { onExceptionCaught(e); }
	  }
	}
  };
  
  protected Thread renewTimer = new Thread()
  {
	@Override
	public void run()
	{
      while(true)
      {
    	try
    	{
          sleep(Server.MAX_TIME / 2 * 1000);		
          Client.this.sendObjectTcp(Server.RENEW_TIME);
          lastRenew = System.currentTimeMillis();
    	}catch(Exception e){}
      }
	}
  };
  
  /**
   * controlla se l'oggetto tcp ricevuto è un comando speciale
   * @param obj oggetto ricevuto
   * @return true se è un comando speciale, false altrimenti
   */
  protected boolean checkSpecialCommands(Object obj)
  {
	if(obj instanceof String) //i comandi speciali sono stringhe
	{
	  String s = (String)obj;
	  //System.out.println(s);
	  
	  if(s.startsWith(Client.BEGIN_INIT_DIFFIE)) //server mi ha mandato dati per l'algoritmo diffie-hellman
	  {
		String data[] = s.split(" ");  
		int n = Integer.parseInt(data[1]);
		int p = Integer.parseInt(data[2]);
		int A = Integer.parseInt(data[3]);
		int y = Util.getRandomNumber(1, 17);
		int B = Util.fastModPow(n, y, p); //(n^y)%p
		this.sendObjectTcp(Client.END_INIT_DIFFIE+" "+B+" "+p, false);
		diffieKey = Util.fastModPow(A, y, p);  //(A^y)%p
		msgCiph.setKey(diffieKey);
		return true;
	  }
	  
	  if(s.equals(Client.DIFFIE_COMPLETED)) //diffie completato
	  {
		requestId();  
		return true;
	  }
	  
	  if(s.startsWith(ID_COMMAND)) //se il server ha mandato l'id del client
	  { //comando id -> #id id_client
		String data[] = s.split(" ");
		id = Integer.parseInt(data[1]);
		onIdReceived();
		return true;  
	  }
	  
	  if(s.equals(Server.MASTER)) //sono master
	  {
	    master = true; 
	    return true;
	  }
	  
	  if(s.equals(Server.NOT_MASTER)) //non sono master 
	  {
		master = false;
		return true;
	  }
	  
	  if(s.startsWith(Server.CHANGE_ROOM)) //devo cambiare stanza
	  {
		room = Integer.parseInt(s.split(" ")[1]); //#cr room maxDim descr
		this.onRoomChanged(s);
		return true;
	  }
	  
	  if(s.equals(Server.NO_CHANGE_ROOM)) //non possibile cambiare stanza
	  {
		this.onRoomChangeNegated();
		return true;
	  }
	  
	  if(s.equals(Server.CLOSE_COMMAND)) //se il server si è disconnesso
	  {
		infoStrDisconnection = "Server has been closed!";
		disconnect();
		return true;  
	  }
	  
	  if(s.equals(Server.TIME_EXPIRED)) //tempo scaduto
	  {
		infoStrDisconnection = "Server doesn't respond!";
		disconnect();
		return true;    
	  }
	}
	return false;  
  }
  
  /**
   * metodo per disconnettersi
   */
  public void disconnect()
  {
	if(connected)
	{
	  onDisconnecting();
	  connected = false;
	  try { sendObjectTcp(DISCONNECT_COMMAND); }catch(Exception e) { onExceptionCaught(e); }
	  try { getMessagesTcp.interrupt(); }catch(Exception e){ onExceptionCaught(e); }
	  try { getMessagesUdp.interrupt(); }catch(Exception e) { onExceptionCaught(e); }
	  try { renewTimer.interrupt(); }catch(Exception e) { onExceptionCaught(e); }
  	  try { connection.close(); }catch(Exception e){ onExceptionCaught(e); }
	  try { outputStream.close(); }catch(Exception e){ onExceptionCaught(e); }
	  try { inputStream.close(); }catch(Exception e){ onExceptionCaught(e); }
	}
	onDisconnected();
  }
  
  /**
   * metodo per cambiare stanza
   * @param newRoom nuova stanza, Server.DEFAULT_ROOM è una stanza dove si possono ricevere solamente i messaggi globali (es: disconnessione server), -2 per far scegliere al server la stanza
   */
  public void changeRoom(int newRoom)
  {
	sendObjectTcp(CHANGE_ROOM_COMMAND+" "+newRoom); 
  }
  
  public void changeRoom(String s)
  {
	sendObjectTcp(CHANGE_ROOM_COMMAND+" "+s);  
  }
  
  public boolean sendObjectTcp(Object obj)
  {
    return sendObjectTcp(obj, true);
  }
  
  /**
   * invia un oggetto con protocollo tcp al server
   * @param obj oggetto da inviare
   * @param crypto usare diffie-hellman?
   * @return true se l'oggetto è stato inviato, false altrimenti
   */
  public boolean sendObjectTcp(Object obj, boolean crypto)
  {
	if(crypto)
	 obj = msgCiph.encrypt((String)obj);
	 //obj = Util.xorStr((String)obj, diffieKey);

	try
	{
	  synchronized(outputStream)
	  {  
 	    outputStream.writeObject(obj);
	    outputStream.flush();
	  }
	}catch(Exception e){ onExceptionCaught(e); return false; }
	return true;
  }
  
  /**
   * invia dati con protocollo udp
   * @param data dati da inviare
   * @return true se il pacchetto è stato inviato, false altrimenti
   */
  public boolean sendDataUdp(byte data[])
  {
	try
	{		  
	  String tmp = getId()+"#"+msgCiph.encrypt(new String(data).trim()); //Util.xorStr(new String(data).trim(), diffieKey);
	  data = tmp.getBytes("UTF-8"); //cripto	
	  
	  DatagramPacket packet = new DatagramPacket(data, data.length, ip, port);
	  socketUdp.send(packet);
	}catch(Exception e) { onExceptionCaught(e); return false; }
	return true;
  }
  
  /**
   * setta porta e ip
   */
  public void setPortIp(int port, String ip) 
  {
    this.port = port;
    this.ipStr = ip;
  }
  
  /**
   * @return id client
   */
  public int getId()
  {
	return id;  
  }
  
  /**
   * @return ip client come stringa
   */
  public String getIp()
  {
	return ipStr;  
  }
  
  /**
   * @return stanza del client
   */
  public int getRoom()
  {
	return room;  
  }
  
  /** sono master della mia stanza? */
  public boolean isMaster()
  {
	return master;  
  }
  
  /** restituisce stringa informativa sulla disconnessione*/
  public String getDisconnectionStrInfo()
  {
	return infoStrDisconnection;  
  }
  
  public void setDisconnectionStrInfo(String str)
  {
	infoStrDisconnection = str;  
  }
  
  /** connesso ? */
  public boolean isConnected()
  {
	return connected;  
  }
  
  /**
   * metodo chiamato quando il client si connette
   */
  protected void onConnected()
  {
  }
  
  /**
   * metodo chiamato quando si riceve un oggetto tcp
   * @param obj oggetto tcp ricevuto
   */
  protected void onObjectTcpReceived(Object obj)
  {  
  }
  
  /**
   * metodo chiamato quando si ricevono dati udp
   * @param data dati ricevuto dal server
   */
  protected void onDataUdpReceived(byte data[])
  {
  }
  
  /**
   * metodo chiamato quando il client è stato disconnesso (gli stream sono chiusi)
   */
  protected void onDisconnected()
  {
  }
  
  /** metodo chiamato prima della disconnessione (gli stream sono ancora aperti) */
  protected void onDisconnecting()
  {  
  }
  
  /**
   * metodo chiamato quando si riceve l'id dal server
   */
  protected void onIdReceived()
  {
    //invio una stringa per far registrare subito l'ip e la porta del client al server, in caso la stringa non arrivi
    //il server registrerà il client ai primi dati ricevuti
	sendDataUdp( (id+" "+Client.FIRST_UDP_STR).getBytes() );
  }
  
  /** metodo chiamato quando la stanza viene cambiata*/
  protected void onRoomChanged(String roomInfo)
  {
  }
  
  /** metodo chiamato quando viene negato l'accesso alla stanza*/
  protected void onRoomChangeNegated()
  {
  }
  
  /**
   * metodo chiamato quando si genera un'eccezione
   * @param e eccezione generata
   */
  protected void onExceptionCaught(Exception e)
  {
  }
}
