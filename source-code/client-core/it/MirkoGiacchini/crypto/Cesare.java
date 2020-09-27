package it.MirkoGiacchini.crypto;

/**
 * cifrario di cesare
 * prende un intero come chiave, ogni carattere è criptato sommando la chiave al suo valore, (l'alfabeto è circolare: z+1 = a...)
 * @author Mirko
 *
 */
public class Cesare extends Cipher
{
  public Cesare(Object obj)
  {
	super(obj);  
  }
	
  @Override
  public String encrypt(String s) 
  {
	if(keyObj instanceof Integer) //la chiave è un intero nel cifrario di cesare
	{
	  int key = (Integer)keyObj;
	  String res = "";
	  for(int i=0; i<s.length(); i++)
	   res += encryptChar(s.charAt(i), key);
	  return res;
	}
	return null;
  }

  @Override
  public String decrypt(String s) 
  {
	if(keyObj instanceof Integer) //la chiave è un intero nel cifrario di cesare
	{
	  int key = (Integer)keyObj;
	  String res = "";
	  for(int i=0; i<s.length(); i++)
	   res += decryptChar(s.charAt(i), key);
	  return res;
	}  
	return null;
  }
  
  /** cripta un solo carattere */
  public static char encryptChar(char car, int key)
  {
	int c = car + key;
	int max, dec;
		
	if(Character.isDigit(car)) //numero
	{
	  max = '9'; //numero massimo '9'
	  dec = 10; //decremento di 10: 10 cifre
	}
	else
	{
	  max = Character.isLowerCase(car) ? 'z' : 'Z'; //massimo 'z' o 'Z', a seconda se la lettera è minuscola o maiuscola
	  dec = 26; //decremento di 26: 26 lettere nell'alfabeto
	}
		
	while(c > max) //finchè si sfora il massimo 
	 c -= dec; //si torna indietro  
	return (char)c;  
  }
  
  /** decripta un solo carattere*/
  public static char decryptChar(char car, int key)
  {
	int c = car - key;
	int min, inc;
			
	if(Character.isDigit(car)) //numero
	{
	  min = '0'; //numero minimo '0'
	  inc = 10; //incremento di 10: 10 cifre
	}
	else
	{
	  min = Character.isLowerCase(car) ? 'a' : 'A'; //minimo 'a' o 'A' 
	  inc = 26; //incrementi di 26: 26 lettere nell'alfabeto
	}
			
	while(c < min) //finchè si sfora il massimo 
	 c += inc; //si torna indietro	  
	
	return (char)c;
  }
}
