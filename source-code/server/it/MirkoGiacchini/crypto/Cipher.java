package it.MirkoGiacchini.crypto;

/**
 * cifrario generale
 * @author Mirko
 *
 */
public abstract class Cipher 
{ 
  /** cripta s usando la chiave key*/
  public abstract String encrypt(String s);
  
  /** decripta s usando la chiave key*/
  public abstract String decrypt(String s);
  
  /** chiave per criptare e decriptare*/
  protected Object keyObj;
  
  public Cipher()
  {
  }
  
  public Cipher(Object key)
  {
	keyObj = key;  
  }
  
  public void setKey(Object key)
  {
	keyObj = key;  
  }
}
