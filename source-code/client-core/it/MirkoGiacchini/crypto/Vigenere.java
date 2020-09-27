package it.MirkoGiacchini.crypto;

/**
 * cifrario di Vigenere
 * simile a quello di Cesare ma si usano N chiavi invece di una
 * @author Mirko
 *
 */
public class Vigenere extends Cipher
{ 
  public Vigenere(Object obj)
  {
	super(obj);  
  }
  
  @Override
  public String encrypt(String s) 
  {
	if(keyObj instanceof int[])
	{
	  String ret = "";
	  int keys[] = (int[])keyObj;
	  int j = 0;
	
	  for(int i=0; i<s.length(); i++)
	  {
	    ret += Cesare.encryptChar(s.charAt(i), keys[j]); //cripto con la j-esima chiave
	    j = j + 1 == keys.length ? 0 : j+1; //incremento la chiave (se sono finite torno alla prima)
	  }
	  return ret;
	}
	return null;
  }

  @Override
  public String decrypt(String s) 
  {
	if(keyObj instanceof int[])
	{
	  String ret = "";
	  int keys[] = (int[])keyObj;
	  int j = 0;
		
	  for(int i=0; i<s.length(); i++)
	  {
		ret += Cesare.decryptChar(s.charAt(i), keys[j]); //decripto con la j-esima chiave
		j = j + 1 == keys.length ? 0 : j+1; //incremento la chiave (se sono finite torno alla prima)
	  }
	  return ret;
    }  
	return null;
  }
}
