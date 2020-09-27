package it.MirkoGiacchini.crypto;

import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.spec.SecretKeySpec;

/**classe per usare algoritmo AES (basata su classi standard java)*/
public class AES extends Cipher
{
  javax.crypto.Cipher cipher;
  
  public AES() 
  {
	super();
	keyObj = null;//new SecretKeySpec(((String)key).getBytes(), "AES");
	
	try 
	{
	  cipher = javax.crypto.Cipher.getInstance("AES/ECB/PKCS5Padding");
	} catch (Exception e) {}
  }

  @Override
  public String encrypt(String s) 
  {
	if(keyObj == null) return s;
	
	try
	{
	  cipher.init(javax.crypto.Cipher.ENCRYPT_MODE, (SecretKeySpec)keyObj);
      return Base64.getEncoder().encodeToString(cipher.doFinal(s.getBytes("UTF-8")));  
	}catch(Exception e){System.out.println(e.getMessage()); e.printStackTrace();}
	return null;
  }

  @Override
  public String decrypt(String s) 
  {
	if(keyObj == null) return s;
	
    try
    {
      cipher.init(javax.crypto.Cipher.DECRYPT_MODE, (SecretKeySpec)keyObj);
      return new String(cipher.doFinal(Base64.getDecoder().decode(s)));
    }catch(Exception e){}
    return null;
  }
  
  @Override
  public void setKey(Object key)
  {
	try
	{
	  String key2 = ((Integer)key)+"";
	  byte [] k = key2.getBytes("UTF-8");
	  MessageDigest sha = MessageDigest.getInstance("SHA-1");
	  k = sha.digest(k);
	  k = Arrays.copyOf(k, 16);   
      keyObj = new SecretKeySpec(k, "AES");
	}catch(Exception e){}
  }
}
