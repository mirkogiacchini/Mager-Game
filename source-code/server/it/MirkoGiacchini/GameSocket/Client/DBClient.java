package it.MirkoGiacchini.GameSocket.Client;

import it.MirkoGiacchini.GameSocket.Server.DBServer;

/**
 * client connesso a db sql
 * @author Mirko
 *
 */
public class DBClient extends Client
{
  public static final String REQ_CONNECTION = "#conn"; //stringa connessione
  public static final String REQ_REGISTRATION = "#reg"; //stringa registrazione
  public static final String DEL_ACCOUNT = "#delacc"; //eliminazione account
  public static final String DB_UPDATE = "#upd"; //update dei dati nel db
  public static final String DB_LOGOUT = "#logout"; //logout dal db
  
  /** record ricevuto dal db (quando conterrà un valore corretto verrà chiamato il metodo 'onDbConnection'*/
  protected String dbRecord;
  
  public DBClient(int port, String ip) 
  {	
	super(port, ip);
	dbRecord = "";
  }
  
  /**
   * richiede connessione a db utenti
   */
  public void connectToDB(String username, String psw)
  {
	this.sendObjectTcp(REQ_CONNECTION+" "+username+" "+psw);  
  }
  
  /**
   * si registra sul DB
   */
  public void registerOnDB(String username, String psw)
  {
	this.sendObjectTcp(REQ_REGISTRATION+" "+username+" "+psw);
  }
  
  /**
   * si cancella account dal db
   */
  public void deleteFromDB(String username, String psw)
  {
	this.sendObjectTcp(DEL_ACCOUNT+" "+username+" "+psw);  
  }
  
  /** invia al server il record aggiornato, da utilizzare in genere prima della disconnessione*/
  public void sendDbUpdate(String newdbrecord)
  {
	sendObjectTcp(DB_UPDATE+"^"+newdbrecord);  
  }
  
  /**invia al server una richiesta per fare il logout dall'account*/
  public void askLogout()
  {
	sendObjectTcp(DB_LOGOUT);  
  }
  
  @Override
  public boolean checkSpecialCommands(Object obj)
  {
	if(obj instanceof String)
	{
	  String msg = (String)obj;
	  if(msg.equals(DBServer.REG_OK)) //registrato
	  {
	    onRegistration();
	    return true;
	  }
	  
	  if(msg.equals(DBServer.REG_FAIL)) //registrazione fallita
	  {
	    onRegistrationFailed();
	    return true;
	  }
	  
	  if(msg.startsWith(DBServer.CONN_OK)) //connesso
	  {
		String data[] = msg.split("\\^");
		dbRecord = data[1];
		onDbConnection(); 
		return true;
	  }
	  
	  if(msg.equals(DBServer.CONN_FAIL)) //connessione fallita
	  {
		onDbConnectionFailed();
		return true;
	  }
	  
	  if(msg.equals(DBServer.DEL_OK)) //eliminazione ok
	  {
		this.onAccountDeleted();
		return true;
	  }
	  
	  if(msg.equals(DBServer.DEL_FAIL)) //eliminazione fallita
	  {
		this.onAccountDelFailed();
		return true;
	  }
	  
	  if(msg.equals(DBServer.ACC_LOGOUT)) //costretto a sloggare dal server
	  {
		onAccountLogout();
		this.sendObjectTcp(DB_LOGOUT);  
	  }
	}
	
	return super.checkSpecialCommands(obj);  
  }

  public String getDbRecord()
  {
	return dbRecord;  
  }
  
  /** metodo chiamato quando avviene connessione al db */
  public void onDbConnection()
  {
  }
  
  /** metodo chiamato quando fallisce la connessione con il db*/
  public void onDbConnectionFailed()
  {
  }
  
  /** registrazione avvenuta */
  public void onRegistration()
  {
  }
  
  /** registrazione fallita */
  public void onRegistrationFailed()
  {
  }
  
  /**account eliminato*/
  public void onAccountDeleted()
  {
  }
  
  /**eliminazione fallita*/
  public void onAccountDelFailed()
  { 
  }
  
  /**costretto a sloggare dal server*/
  public void onAccountLogout()
  {
	  
  }
}

