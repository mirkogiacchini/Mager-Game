package it.MirkoGiacchini.GameSocket.Client;

import it.MirkoGiacchini.GameSocket.Server.GameServer;

/**
 * client di gioco
 * @author Mirko
 *
 */
public class GameClient extends DBClient 
{
  /** stringa per richiedere le stanze*/
  public static final String REQ_R = "#grms";
  
  public GameClient(int port, String ip) 
  {
	super(port, ip);
  }	 
  
  /**metodo da chiamare quando si vogliono aggiornare le stanze*/
  public void requestRooms()
  {
	this.sendObjectTcp(REQ_R);  
  }
  
  @Override
  public boolean checkSpecialCommands(Object obj)
  {
	if(obj instanceof String)
	{
      String s = (String)obj;	
      if(s.startsWith(GameServer.ROOM_INFO))
      {
    	String s2 = s.substring(s.indexOf(' ')+1, s.length());
    	onRoomReceived(s2);
    	return true;
      }
      
      if(s.startsWith(GameServer.ROOM_REMOVED))
      {
    	String s2 = s.substring(s.indexOf(' ')+1, s.length());
    	onRoomRemoved(s2);
    	return true;
      }
	}
	return super.checkSpecialCommands(obj);  
  }
  
  /**metodo chiamato quando si ricevono informazioni su una stanza*/
  protected void onRoomReceived(String roomInfo)
  {
  }
  
  /**metodo chiamato quando si rimuove una stanza*/
  protected void onRoomRemoved(String roomInfo)
  {
  }
}
