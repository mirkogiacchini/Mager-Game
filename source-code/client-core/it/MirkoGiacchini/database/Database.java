package it.MirkoGiacchini.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * database sql
 * @author Mirko
 */
public class Database 
{
  protected Connection connection;
  protected Statement statement;
  
  /**
   * url del database localost
   */
  public final static String LOCAL_URL = "jdbc:mysql://localhost";
  
  /**
   * si connette al database con i dati passati in input
   * @param url db
   * @param username db
   * @param psw db
   */
  public Database(String url, String username, String psw) throws Exception
  {
	connect(url, username, psw);
  }
  
  /**
   * si connette al database in localost
   */
  public Database() throws Exception
  {
	connect();  
  }
  
  /**
   * usa il database dato in input, lo crea se non esiste
   * @param name nome del database
   */
  public void set(String name) throws SQLException 
  {
	try
	{
	  create(name); 
	}catch(Exception e){}
	
	setButNoCreate(name);
  }
  
  /**
   * usa il database dato in input, se non esiste NON lo crea
   * @param name nome database
   */
  public void setButNoCreate(String name) throws SQLException
  {
	connection.setCatalog(name);
	statement = connection.createStatement();
  }
  
  /**
   * crea un database dato in input
   * @param name nome database
   */
  public void create(String name) throws SQLException
  {
	statement.executeUpdate("CREATE DATABASE "+name);   
  }
  
  /**
   * elimina un database dato in input
   * @param name
   */
  public void delete(String name) throws SQLException
  {
	statement.executeUpdate("DROP DATABASE "+name);  
  }
  
  /**
   * esegue un update: inserimento/eliminazione di un database, di una tabella o di una riga
   * @param query query da eseguire
   */
  public int executeUpdate(String query) throws SQLException
  {
	return statement.executeUpdate(query);  
  }
  
  /**
   * esegue una query SELECT
   * @param query query da eseguire
   * @return risultato della SELECT
   */
  public ResultSet executeQuery(String query) throws SQLException
  {
	return statement.executeQuery(query);  
  }
  
  /**
   * si connette a un url, con username e psw
   * @param url db
   * @param username db
   * @param psw db
   */
  public void connect(String url, String username, String psw) throws Exception
  {
	String driver = "com.mysql.jdbc.Driver";	
	Class.forName(driver);
	connection = DriverManager.getConnection(url, username, psw);
	statement = connection.createStatement();	  
  }
  
  /**
   * si connette in localost con username="root" e psw=""
   */
  public void connect() throws Exception
  {
	connect(LOCAL_URL, "root", "");  
  }
  
  /**
   * chiude connessioni con database
   */
  public void close() throws Exception
  {  
	statement.close();
	connection.close();
  }
}
