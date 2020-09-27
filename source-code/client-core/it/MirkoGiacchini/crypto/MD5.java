package it.MirkoGiacchini.crypto;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5 extends Cipher
{
  MessageDigest md;
  
  public MD5() 
  {
	super(null);
	try 
	{
	  md = MessageDigest.getInstance("MD5");
	} catch (NoSuchAlgorithmException e) {}
  }
  
  @Override
  public String encrypt(String s) 
  {
	try
	{
	  byte b[] = md.digest(s.getBytes("UTF-8"));
	  
	  StringBuffer hexString = new StringBuffer();
      for (int i=0;i<b.length;i++) 
      {
        String hex=Integer.toHexString(0xFF & b[i]);
        if(hex.length()==1)
         hexString.append('0');
        hexString.append(hex);
      }
      
	  return hexString.toString();
	}catch(Exception e){}
	return null;
  }

  @Override
  public String decrypt(String s)
  {
	return null;
  }
}
