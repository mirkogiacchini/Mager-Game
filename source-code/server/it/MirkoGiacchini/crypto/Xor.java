package it.MirkoGiacchini.crypto;

public class Xor extends Cipher
{
  public Xor(int key) 
  {
	super(key);
  }
  
  @Override
  public String encrypt(String str) 
  {	 
	String r = "";
	for(int i=0; i<str.length(); i++)
	 r += (char)(str.charAt(i) ^ ((Integer)keyObj));
	return r;
  }

  @Override
  public String decrypt(String s) 
  {
	return encrypt(s);
  }
}
