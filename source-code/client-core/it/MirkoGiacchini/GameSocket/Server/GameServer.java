package it.MirkoGiacchini.GameSocket.Server;

import java.util.ArrayList;

/**
 * server di gioco
 * @author Mirko
 *
 */
public class GameServer extends DBServer
{
  /** stringa per inviare informazioni su una stanza*/
  public static final String ROOM_INFO = "#rinf";
  public static final String ROOM_REMOVED = "#rrem";
  
  int lastRoom = 1;
  ArrayList<Integer> availableRooms = new ArrayList<Integer>();	
	
  public GameServer(int port, int maxClients, String dbname, String table, String unc, String pwc) throws Exception 
  {
	super(port, maxClients, dbname, table, unc, pwc);
  }
  
  @Override
  boolean canChangeRoom(int id, int roomId)
  {
	if(!rooms.containsKey(roomId))
	 return true;  
	return rooms.get(roomId).a.size() < rooms.get(roomId).b; //il client può entrare se la stanza non è piena
  }
  
  @Override
  public int getNewRoomId()
  {
	if(availableRooms.isEmpty())
	 return lastRoom++;
	else
	{
	  int r = availableRooms.get(0);
	  availableRooms.remove(0);
	  return r;
	}
  }
  
  @Override
  public void roomUpdated(final int room)
  {
	new Thread() //notifico a default room l'aggiornamento della stanza
	{
	  @Override
	  public void run()
	  {
	    synchronized(usersInDefRoom)
	    {
	      try
	      {
		    for(int i : usersInDefRoom)
		     GameServer.this.sendObjectTcp(ROOM_INFO+" "+getRoomString(room), i);
	      }catch(Exception e){}
	    }
	  }
	}.start();
  }
  
  @Override
  public void roomRemoved(final int room)
  {
	availableRooms.add(room);  
	new Thread() //notifico a tutti gli utenti nella default room la rimozione della stanza
	{
	  @Override
	  public void run()
	  {
		synchronized(usersInDefRoom)
		{
		  for(int i : usersInDefRoom)
		   GameServer.this.sendObjectTcp(ROOM_REMOVED+" "+room, i);
		}
	  }
	}.start();
  }
  
  /** restituisce stringa stanza*/
  public String getRoomString(int room)
  {
	return room+" "+rooms.get(room).a.size()+" "+rooms.get(room).b+" "+rooms.get(room).c;  
  }
}
