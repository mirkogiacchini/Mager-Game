package it.MirkoGiacchini.GameSocket.Server;

import java.sql.ResultSet;
import java.util.HashMap;

import it.MirkoGiacchini.database.Database;

/**
 * server con accesso a database SQL
 * @author Mirko
 *
 */
public class DBServer extends Server
{
  public static final String REG_OK = "#regok"; //registration to db ended happened
  public static final String REG_FAIL = "#regfail"; //registration to db failed
  
  public static final String CONN_OK = "#conok"; //connection to db happened: #conok^data
  public static final String CONN_FAIL = "#confail"; //connection to db failed
  
  public static final String DEL_OK = "#delok"; //eliminazione avvenuta con successo
  public static final String DEL_FAIL = "#delfail"; //eliminazione fallita
  
  public static final String ACC_LOGOUT = "#acout";
  
  /**set degli utenti connessi (contiene l'username)*/
  HashMap<String, Integer> usersConnected = new HashMap<String, Integer>();
  
  /** database SQL */
  protected Database db;
  
  protected String table, unc, pwc; //tabella utenti, colonna username, colonna password
  
  public DBServer(int port, int maxClients, String url, String username, String psw, String dbname, String table, String unc, String pwc) throws Exception
  {
	super(port, maxClients);
	db = new Database(url, username, psw);
	db.set(dbname);
	this.table = table;
	this.unc = unc;
	this.pwc = pwc;
  } 
  
  public DBServer(int port, int maxClients, String dbname, String table, String unc, String pwc) throws Exception
  {
	super(port, maxClients);
	db = new Database();
	db.set(dbname);
	this.table = table;
	this.unc = unc;
	this.pwc = pwc;
  }
  
  @Override
  public boolean open()
  {
	if(super.open())
	 return initDB();
	return false;
  }
  
  public void executeQuery(String query) throws Exception
  {
	db.executeQuery(query);  
  }
  
  public int executeUpdate(String query) throws Exception
  {
	return db.executeUpdate(query);  
  }
  
  /**restituisce il record da username e psw*/
  public String getRecord(String username, String password)
  {
	String str = "";
	try
	{
	  ResultSet record = db.executeQuery("SELECT * FROM "+table+" WHERE "+unc+" = '"+username+"' AND "+pwc+" = '"+password+"'");
	  if(record.first())
	  {
		int col = record.getMetaData().getColumnCount();
		for(int i=1; i<=col; i++)
	     str += record.getString(i)+" ";   
	  }
	}catch(Exception e){}
	return str;
  }
  
  /** override per tabelle personalizzate -> username deve essere primary key*/
  public String createInsertQuery(String username, String psw)
  {
	return "INSERT INTO "+table+" VALUES ('"+username+"', '"+psw+"', '', '1', '0', '0')";  
  }
  
  /** crea le query di aggiornamento dalla stringa record creata dal player*/
  protected String[] createUpdateQuery(String precord)
  {
	String data[] = precord.split(" "); //record standard: nome, lv, exp, pen
	String ris[] = new String[1];
	ris[0] = "UPDATE "+table+" SET level='"+data[1]+"', exp='"+data[2]+"', pen='"+data[3]+"' WHERE "+unc+"='"+data[0]+"'";  
	return ris;
  }
  
  /**chiamato quando si cancella l'account*/
  public void onDeletingAccount(String username)
  {
  }
  
  /** inizializza db -> si crea la tabella degli utenti */
  public boolean initDB()
  {
	try
	{
	  executeUpdate("CREATE TABLE "+table+" ("+unc+" varchar(30) collate utf8_bin PRIMARY KEY, "+pwc+" varchar(40) collate utf8_bin NOT NULL, email varchar(60) NOT NULL, level int NOT NULL, exp int NOT NULL,"
	  		         +"pen int NOT NULL)");	
	  executeUpdate("create unique index i"+table+" on "+table+"("+unc+", "+pwc+")");
	  executeUpdate("create unique index i2"+table+" on "+table+"("+unc+")");
	}catch(Exception e){ }
	return true;
  }
}
