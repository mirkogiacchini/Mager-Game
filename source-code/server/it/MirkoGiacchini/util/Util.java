package it.MirkoGiacchini.util;

import java.util.ArrayList;
import java.util.Random;

import it.MirkoGiacchini.crypto.Bellaso;
import it.MirkoGiacchini.crypto.Cipher;

/**
 * classe contentente alcuni metodi utili
 * @author Mirko
 *
 */
public class Util 
{
  public static final String BELLASO_KEY1 = "Sj23KKs";
  public static final String BELLASO_KEY2 = "Ps23dOd2";
  public static final int MAX_PRIME = 1000000;
  static ArrayList<Integer> primes = null;
  static Random rand = new Random();
  
  /**
   * @return true se 's' è un ip, false altrimenti
   */
  public static boolean isIpV4(String s)
  {
	for(int i=0; i<s.length(); i++)
	 if(s.charAt(i) != '.' && !Character.isDigit(s.charAt(i)))
	  return false;
	
    String q[] = s.split(".");
    if(q.length != 4) return false;
    for(int i=0; i<4; i++)
     if(Integer.parseInt(s) > 255 || Integer.parseInt(s) < 0)
      return false;
    return true;
  }
  
  /**
   * restituisce un cifrario di bellaso
   * @param key1 prima chiave
   * @param key2 seconda chiave
   * @return cifrario bellaso
   */
  public static Cipher getBellaso(String key1, String key2)
  {
	String key[] = {key1, key2};
	return new Bellaso(key);
  }
  
  public static Cipher getBellaso()
  {
	return getBellaso(BELLASO_KEY1, BELLASO_KEY2);  
  }
  
  /**arrotonda il float alla k-esima cifra decimale*/
  public static float round(float a, int k)
  {
	int pow = (int)Math.pow(10, k);  
	int b = (int)(a * pow);
	return (float)b / pow;  //return a;
  }
  
  /**arrotonda a matematicamente: dopo .5 passa al successivo*/
  public static int round(float a)
  {
	int ia = (int)a;
	if( a - ia < .5f)
	 return ia;
	return (int)Math.ceil(a);
  }
  
  public static String getMinutesFormatTime(int seconds)
  {
	float minutes = (float)seconds/60;
	int sec = round( (minutes - (int)minutes) * 60 );
	return ((int)minutes)+":"+(sec < 10 ? "0"+sec : sec);
  }
  
  public static void sieveOfEratosthenes()
  {
	sieveOfEratosthenes(MAX_PRIME);  
  }
  
  /**precalcola molti primi... da chiamare prima di getRandomPrime()*/
  public static void sieveOfEratosthenes(int maxprime)
  {
	if(primes == null) primes = new ArrayList<Integer>();
	primes.clear();
	
	boolean bit[] = new boolean[maxprime];
	for(int i=0; i<maxprime; i++) bit[i] = true;
    	
	for(long i=2; i<maxprime; i++)
	 if(bit[(int)i])
	 {
	   primes.add((int)i);	 
	   for(long j=i*i; j<maxprime; j+=i)
		bit[(int)j] = false;
	 }
  }
  
  /**restituisce un primo casuale*/
  public static int getRandomPrime()
  {
	if(primes.size() == 0) return 2;
	return primes.get(getRandomNumber(0, primes.size()-1));  
  }
  
  public static int getRandomNumber(int l, int r)
  {
	return l + rand.nextInt(r - l + 1);  
  }
  
  /**potenza con modulo O(N)*/
  public static int modPow(int b, int e, int p)
  {
	int answ = 1;
	for(int i=0; i<e; i++)
	 answ = (int)(((long)((long)(answ%p) * (long)(b%p)))%(long)p);
	return answ;
  }
  
  /**potenza con modulo O(logN)*/
  public static int fastModPow(int b, int e, int p)
  {
	if(e == 0) return 1;
	if(e%2 == 1) return (int)(((long)((long)(b%p) * (long)(fastModPow(b, e-1, p)%p)))%(long)p);
	int answ = fastModPow(b, e/2, p);
	return (int)(((long)((long)(answ%p) * (long)(answ%p)))%(long)p);
  }
}
