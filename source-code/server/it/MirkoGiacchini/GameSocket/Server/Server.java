package it.MirkoGiacchini.GameSocket.Server;

import it.MirkoGiacchini.GameSocket.Client.Client;
import it.MirkoGiacchini.pairing.Triple;
import it.MirkoGiacchini.util.Util;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

/**
 * Server usato nei videogiochi
 * @author Mirko
 *
 */
public class Server 
{
  /** stanza di default, non usata da nessuno */
  public static final int DEFAULT_ROOM = -1;
  
  /** comando che indica la chiusura del server */
  public static final String CLOSE_COMMAND = "#close";
  
  /** comando che indica al client che è un master*/
  public static final String MASTER = "#master";
  
  /** comando che indica al client che non è un master*/
  public static final String NOT_MASTER = "#notmaster";
  
  /** comando per far cambiare la stanza a un client*/
  public static final String CHANGE_ROOM = "#cr";
  
  /** comando per non far cambiare stanza a un client*/
  public static final String NO_CHANGE_ROOM = "#ncr"; 
  
  /**comando per rinnovare il timer dei client, altrimenti vengono disconnessi*/
  public static final String RENEW_TIME = "#rentime";
  
  /**comando per comunicare al client che, visto che non è stato rinnovato il timer, il tempo è scaduto*/
  public static final String TIME_EXPIRED = "#tiexp";
  
  /**tempo dopo il quale i client vengono disconnessi se non si è rinnovata la connessione, in secondi*/
  public static final int MAX_TIME = 100;
  
  /** stanza automatica: il server sceglie la stanza*/
  public static final int AUTO_ROOM = -2;
  
  /** porta sulla quale il server è in ascolto */
  public int port; 
  
  /** massimo numeri di client che possono connettersi contemporaneamente */
  protected int maxClients;
  
  /** lista degli id disponibili da assegnare ai client */
  protected ArrayList<Integer>availableId;
  
  /** prossimo id da usare (nel caso in cui la lista degli id disponibili sia vuota) */
  protected int nextIdToUse;
  
  /** mappa dei client connessi, la chiave è l'id del client */
  protected HashMap<Integer, ClientConnected> clients;
  
  /** stanze a cui connettersi -> chiave: id della stanza, valore: a = set degli id dei client connessi su quella stanza, b = dimensione massima della stanza, c = descrizione stanza*/
  protected HashMap<Integer, Triple<HashSet<Integer>, Integer, String>> rooms;
  
  /** utenti nella room di default */
  protected HashSet<Integer> usersInDefRoom;
  
  /** serversocket usato per il protocollo TCP */
  protected ServerSocket serverTCP;
  
  /** server per protocollo udp */
  protected DatagramSocket serverUDP;
  
  /** server aperto? */
  protected boolean isOpen;
  
  /** crea un server 
   * @param port porta su cui il server si mette in ascolto
   * @param maxClients massima grandezza coda delle connessioni
   */
  public Server(int port, int maxClients)
  {
	this.port = port;  
	this.maxClients = maxClients;
	
	availableId = new ArrayList<Integer>();
	
	clients = new HashMap<Integer, ClientConnected>();
	rooms = new HashMap<Integer, Triple<HashSet<Integer>, Integer, String>>();
	usersInDefRoom = new HashSet<Integer>();
	isOpen = false;
	Util.sieveOfEratosthenes();
  }
  
  /**
   * apre il server
   * @return true se il server è stato aperto, false altrimenti
   */
  public boolean open()
  {
	if(!isOpen) //se il server non è aperto 
	{
	  initFields();
	  
	  try //cerco di aprirlo
	  {
	    serverTCP = new ServerSocket(port, maxClients);	
	    serverUDP = new DatagramSocket(port);
		isOpen = true; 
	    acceptClients.start();
	    getUdpData.start();
	    onServerOpened();
	  }
	  catch(Exception e) //se si genera un'eccezione non sono riuscito ad aprire il server
	  { 
	    onExceptionCaught(e);
	    return false;
	  }
	}
	
	return true; //se arrivo qui il server è stato aperto (o era già aperto alla chiamata di open() )
  }
  
  /**
   * thread che accetta le richieste di connessione dei client
   */
  protected Thread acceptClients = new Thread()
  {
	@Override
	public void run()
	{
	  Socket connection; //connessione tra client e server
	   
	  while(isOpen) //finchè il server è aperto
	  {
		try 
		{
		  synchronized(serverTCP)
		  {
		    connection = serverTCP.accept(); //accetto nuovo client
		  }
		  
		  if(isOpen)
		   synchronized(clients) //aggiungo nuovo client alla mappa
		   {
			 int id = getNextId();
			 clients.put(id, new ClientConnected(connection, id, Server.this));  
			 onClientConnected(id);
		   }
		}catch(Exception e){ onExceptionCaught(e); }
	  }
	}
  };
  
  /**
   * thread che riceve messaggi udp
   */
  protected Thread getUdpData = new Thread()
  {
	@Override
	public void run()
	{
	  DatagramPacket packet;
	  
	  while(isOpen)
	  {
		try
		{
		  byte data[] = new byte[1024];
		  packet = new DatagramPacket(data, data.length);
		  
		  synchronized(serverUDP)
		  {
		    serverUDP.receive(packet); //ricevo dati
		  }
		  
		  int idfr = getIdByData(packet.getData()); //prendo id dai dati
		  
		  String tmp = new String(packet.getData(), 0, packet.getLength(), "UTF-8").trim(); //decripto
		  tmp = tmp.substring(tmp.indexOf("#")+1, tmp.length());
		  data = clients.get(idfr).msgCiph.decrypt(tmp).getBytes("UTF-8");
		  //data = Util.xorStr(tmp, clients.get(idfr).getDiffieKey()).getBytes("UTF-8");
		  if(!checkSpecialCommandsUdp(packet, data, idfr))
		   onMessageUdpReceived(data, idfr);
		}catch(Exception e){ onExceptionCaught(e); }
	  }
	}
  };
  
  /**
   * controlla se i dati udp ricevuti rappresentano un messaggio speciale 
   * @param data
   * @return true se il pacchetto è un comando speciale, false altrimenti
   */
  protected boolean checkSpecialCommandsUdp(DatagramPacket packet, byte[]data, int id)
  {
	//anche se non si tratta di un comando speciale viene controllato se il paccheto 'packet' è il primo pacchetto ricevuto dal server
	    
	if(id != -1)
	 if(clients.get(id).ip == null) //se è il primo pacchetto inizializzo ip e porta del client
	 {
	   clients.get(id).ip = packet.getAddress();
	   clients.get(id).port = packet.getPort();
	 }
	
	if( new String(data).trim().endsWith(Client.FIRST_UDP_STR)) //comando speciale -> stringa inviata automaticamente dal client per permettere la registrazione
	 return true;
	
	return false;
  }
  
  /**
   * invia oggetto a un client tramite protocollo tcp
   * @param obj oggetto da inviare
   * @param id id del client
   * @return true se è riuscito ad inviarlo, false altrimenti
   */
  public boolean sendObjectTcp(Object obj, int id)
  {
	try
	{
	  clients.get(id).sendObjectTcp(obj);	
	}
	catch(Exception e)
	{
	  onExceptionCaught(e);
	  return false;
	}
	return true;
  }
  
  /**
   * invia un oggetto con protocollo tcp a un'intera stanza
   * @param obj oggetto da inviare
   * @param room la stanza a cui inviare il messaggio
   */
  public void sendObjectTcpToRoom(Object obj, int room)
  {
	if(rooms.containsKey(room)) //stanza normale
	{
	  for(int id : rooms.get(room).a) //prendo id di ogni client connesso nella stanza 'room'
	   sendObjectTcp(obj, id);
	}
  }
  
  /**
   * invia un oggetto con protocollo tcp a tutte le stanze
   * @param obj oggetto da inviare
   */
  public void sendObjectTcpToAllRooms(Object obj)
  {
	for(Entry<Integer, Triple<HashSet<Integer>, Integer, String>> e : rooms.entrySet()) //per ogni stanza 'e' nelle stanze disponibili
     sendObjectTcpToRoom(obj, e.getKey());
  }
  
  /**
   * invia un oggetto con protocollo tcp a tutti i client
   * @param obj
   */
  public void sendObjectTcpToAll(Object obj)
  {
	for(Entry<Integer, ClientConnected> e : clients.entrySet())  
	 sendObjectTcp(obj, e.getKey()); //la chiave è l'id del client
  }
  
  /**
   * esegue broadcast dell'oggetto a tutta la stanza
   * @param obj oggetto da inviare
   * @param id id del client che ha inviato l'oggetto
   */
  public void broadcastTcpOnRoom(Object obj, int id)
  {
	int roomId = clients.get(id).room; //trovo la stanza del client con id 'id'
	for(int i : rooms.get(roomId).a) //per ogni id nella stanza
	 if(i != id) //se non è lo stesso id del client mittente
	  sendObjectTcp(obj, i); //ripeto messaggio
  }
  
  /**
   * esegue broadcast a tutti gli utenti in una stanza
   * @param obj oggetto da inviare
   * @param id id del client che ha inviato l'oggetto
   */
  public void broadcastTcpOnGame(Object obj, int id)
  {
	for(Entry<Integer, Triple<HashSet<Integer>, Integer, String>> e : rooms.entrySet()) //per ogni stanza
	 if(e.getValue().a.contains(id)) //se la stanza contiene id
	  broadcastTcpOnRoom(obj, id); //eseguo broadcast sulla stanza
	 else //altrimenti
	  sendObjectTcpToRoom(obj, e.getKey()); //invio messaggio a tutta la stanza
  }
  
  /**
   * esegue broadcast a tutti gli utenti
   * @param obj oggetto da inviare
   * @param id id del client che ha inviato l'oggetto
   */
  public void broadcastTcp(Object obj, int id)
  {
	for(Entry<Integer, ClientConnected> e : clients.entrySet()) //per ogni client
	 if(e.getKey() != id) //diverso da id
	  sendObjectTcp(obj, e.getKey()); //invio oggetto
  }
  
  /**
   * invia dati con protocollo udp
   * @param data dati da inviare
   * @param id id del client a cui si invia il pacchetto
   * @return true se sono stati inviati, false altrimenti
   */
  public boolean sendDataUdp(byte data[], int id)
  {
	try
	{
      if(clients.get(id).ip != null) //per utilizzare il protocollo udp il client deve avere inviato già almeno un messsaggio
      {
    	//data = (Util.xorStr(new String(data).trim(), clients.get(id).getDiffieKey())).getBytes("UTF-8");  //cripto dati
    	data = clients.get(id).msgCiph.encrypt(new String(data).trim()).getBytes("UTF-8");
    	  
    	synchronized(serverUDP)
    	{
	      DatagramPacket packet = new DatagramPacket(data, data.length, clients.get(id).ip, clients.get(id).port);
	      serverUDP.send(packet);
	      return true;
    	}
      }
	}catch(Exception e){ onExceptionCaught(e); }
    return false; 
  }
  
  /**
   * invia dati a un'intera stanza
   * @param data dati da inviare
   * @param room stanza a cui inviare dati
   */
  public void sendDataUdpToRoom(byte data[], int room)
  {
	synchronized(rooms)
	{
	  if(rooms.containsKey(room))	
	   for(int i : rooms.get(room).a) //per ogni id nella stanza room
        sendDataUdp(data, i);
	}
  }
  
  /**
   * invia dati a tutte le stanze
   * @param data dati da inviare
   */
  public void sendDataUdpToAllRooms(byte data[])
  {
	for(Entry<Integer, Triple<HashSet<Integer>, Integer, String>> e : rooms.entrySet()) //per ogni stanza
	 for(int i : e.getValue().a) //per ogni id nella stanza 'e'
	  sendDataUdp(data, i);
  }
  
  /**
   * invia dati a tutti gli utenti
   * @param data dati da inviare
   */
  public void sendDataUdpToAll(byte data[])
  {
	for(Entry<Integer, ClientConnected> e : clients.entrySet()) //per ogni client connesso
	 sendDataUdp(data, e.getValue().id); //invia a quel client
  }
  
  /**
   * broadcast di dati udp in un'intera stanza
   * @param data dati su cui fare broadcast
   * @param id id del client che ha inviato i dati
   */
  public void broadcastUdpOnRoom(byte data[], int id)
  {
	int room = clients.get(id).room; //prendo stanza del client che ha inviato i dati
	for(int i : rooms.get(room).a) //per ogni elemento in 'room'
	 if(i != id) //diverso da id
	  sendDataUdp(data, i); //invia dati a quell'elemento
  }
  
  /**
   * broadcast su tutte le stanza
   * @param data dati da inviare
   * @param id id del client che ha inviato dati
   */
  public void broadcastUdpOnGame(byte data[], int id)
  {
	for(Entry<Integer, Triple<HashSet<Integer>, Integer, String>> e : rooms.entrySet()) //per ogni stanza
	 if(e.getValue().a.contains(id)) //se la stanza contiene id
	  broadcastUdpOnRoom(data, id); //faccio broadcast sulla stanza di id
	 else //altrimenti
	  sendDataUdpToRoom(data, e.getKey()); //invio dati a tutta la stanza
  }
  
  /**
   * broadcast a tutti gli utenti connessi
   * @param data dati da inviare
   * @param id id del client che ha inviato i dati
   */
  public void broadcastUdp(byte data[], int id)
  {
	for(Entry<Integer, ClientConnected> e : clients.entrySet()) //per ogni client connesso
	 if(e.getKey() != id) //diverso dal client 'id'
	  sendDataUdp(data, e.getKey()); //invia dati a quel client
  }
  
  /** 
   * @return id per un nuovo client
   */
  protected int getNextId()
  {
	if(availableId.size() > 0) //se ho ancora degli id disponibili nella lista
	{
	  int id = availableId.get(0); //prendo l'id dalla lista
	  availableId.remove(0);
	  return id;
	}
	
	return nextIdToUse++; //altrimenti uso il prossimo id ancora mai usato
  }
  
  /**
   * metodo chiamato quando un client cambia stanza
   * @param id id del client che cambia stanza
   * @param oldRoom id vecchia stanza
   * @param newRoom id nuova stanza
   * @throws Exception 
   */
  protected synchronized void changeRoom(int id, int oldRoom, int newRoom) throws Exception
  {
	if(!clients.containsKey(id))
	 return;
	
	if(oldRoom == DEFAULT_ROOM) //se vado via dalla default room
	 this.usersInDefRoom.remove(id); //la rimuovo dal set
	
	if(newRoom == DEFAULT_ROOM) //se entro nella default room
	 this.usersInDefRoom.add(id); //la aggiungo al set
	
	synchronized(rooms)
	{
	  if(rooms.containsKey(oldRoom)) //se la stanza vecchia esiste
	  {
	    rooms.get(oldRoom).a.remove(id); //rimuovo il client dalla stanza vecchia
	    if(rooms.get(oldRoom).a.isEmpty()) //se la stanza è vuota la rimuovo
	    {
	      rooms.remove(oldRoom);
	      roomRemoved(oldRoom);
	    }
	    else //la stanza esiste ancora
	     if(clients.get(id).master) //se il client che esce era il master della stanza
	     {
		   int newMaster = rooms.get(oldRoom).a.iterator().next(); //il prossimo client nella stanza diventa il master
		   clients.get(newMaster).setMaster(true);
	     }
	   
	    if(rooms.containsKey(oldRoom)) //se la stanza c'è ancora è stata aggiornata
	     roomUpdated(oldRoom);
	  }
	
	  if(!rooms.containsKey(newRoom) && newRoom != DEFAULT_ROOM) //se la nuova stanza non esiste
	  {
	    rooms.put(newRoom, new Triple<HashSet<Integer>, Integer, String>()); //la creo
	    rooms.get(newRoom).a = new HashSet<Integer>();
	    rooms.get(newRoom).b = -1;
	    rooms.get(newRoom).c = "";
	    if(clients.containsKey(id))
	     clients.get(id).setMaster(true); //se ho creato io la stanza sono il master
	  }
	  else
	   if(clients.containsKey(id))	
	    clients.get(id).setMaster(false); //se non sono il primo della stanza in cui mi sposto allora sicuramente non sono il master
	
	  if(newRoom != DEFAULT_ROOM)
	  {
	    rooms.get(newRoom).a.add(id); //aggiungo il client alla nuova stanza
	    roomUpdated(newRoom);
	  }
	}
  }
  
  /**
   * disconnette il server
   */
  public void disconnect()
  {
	if(isOpen)
	{
	  isOpen = false;
	  try { acceptClients.interrupt(); }catch(Exception e) { onExceptionCaught(e); }
	  try { getUdpData.interrupt(); }catch(Exception e) { onExceptionCaught(e); }
	  sendObjectTcpToAll(CLOSE_COMMAND); //dico a tutti i client che il server è offline 

      for(Entry<Integer, ClientConnected> e : clients.entrySet())
	   try{ e.getValue().disconnect(); }catch(Exception e1) { onExceptionCaught(e1); } //disconnette tutti i client
	}
	onServerDisconnected();
  }
  
  /**
   * metodo chiamato quando viene generata un'eccezione
   * @param e eccezione generata
   */
  protected void onExceptionCaught(Exception e)
  {
  }
  
  /**
   * metodo chiamato qunado il server si disconnette
   */
  protected void onServerDisconnected()
  {
  }
  
  /**
   * metodo chiamato all'apertura del server
   */
  protected void onServerOpened()
  {
  }
  
  /**
   * metodo chiamato quando si connette un client
   * @param id id del client connesso
   */
  protected void onClientConnected(int id)
  {
	usersInDefRoom.add(id);
  }  
  
  /** 
   * metodo chiamato quando si riceve un oggetto TCP da un client
   * @param obj oggetto ricevuto
   * @param id id del client
   */
  protected void onMessageTcpReceived(Object obj, int id)
  {
  }
  
  /**
   * metodo chiamato quando si riceve un messaggio udp
   * @param data dati ricevuti
   * @param id id del client che ha inviato dati
   */
  protected void onMessageUdpReceived(byte data[], int id)
  {
  }
  
  /** il client 'id' può andare nella stanza 'roomId'?*/
  boolean canChangeRoom(int id, int roomId)
  {
	if(clients.get(id).room == roomId) 
	 return false;  
	return true;  
  }
  
  /**
   * metodo che presi dei byte deve restituire l'id del client che li ha inviati
   * di default si presume che il client abbia inviato una stringa del tipo "id#messaggio"
   * @param data dati 
   * @return id del client che ha inviato i dati
   */
  protected int getIdByData(byte data[])
  {
	try
	{
	  String s = new String(data, 0, data.length, "UTF-8").trim();
	  int id = Integer.parseInt(s.substring(0, s.indexOf('#')));
	  return id;
	}catch(Exception e) { onExceptionCaught(e); return -1; }
  }
  
  /**
   * metodo chiamato quando un client si disconnette
   * @param id id del client disconnesso
   * @param room stanza sulla quale si trovava il client
   */
  protected void onClientDisconnected(int id, int room)
  {
	try
	{
	  this.changeRoom(id, room, -1);	
	}catch(Exception e){}
	
	clients.remove(id); //lo rimuovo dalla mappa  
	availableId.add(id); //nuovo id disponibile
  }
  
  /**chiamata quando avviene un cambiamento sulla stanza*/
  public void roomUpdated(int roomId)
  {  
  }
  
  /**chiamata alla rimozione della stanza*/
  public void roomRemoved(int roomId)
  {
  }
  
  /** id fornito dal server quando riceve AUTO_ROOM*/
  public int getNewRoomId()
  {
	return 0;  
  }
  
  /**
   * restituisce stanza del client
   * @param id id del client
   */
  public int getClientRoom(int id)
  {
	if(clients.containsKey(id))
	 return clients.get(id).room;
	return Server.DEFAULT_ROOM;
  }
  
  /**USARE SOLO PER DEBUG!*/
  protected void onSpecialCommandReceived(Object obj, int id)
  {  
  }
  
  /**
   * inizializza campi prima della connessione
   */
  protected void initFields()
  {
	nextIdToUse = 0;  
	availableId.clear();
  }
}
