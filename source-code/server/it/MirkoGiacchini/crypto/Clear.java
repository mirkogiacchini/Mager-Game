package it.MirkoGiacchini.crypto;

/**
 * messaggio in chiaro... non critta niente
 * @author Mirko
 *
 */
public class Clear extends Cipher
{
  public Clear(Object key) 
  {
 	super(key);
  }

  @Override
  public String encrypt(String s) 
  {
    return s;
  }

  @Override
  public String decrypt(String s) 
  {
	return s;
  }
}
