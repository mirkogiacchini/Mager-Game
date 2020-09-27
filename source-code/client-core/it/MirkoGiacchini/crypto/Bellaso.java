package it.MirkoGiacchini.crypto;

import java.util.HashMap;
import java.util.HashSet;

/**
 * cifrario bellaso: vengono creati più alfabeti involutori partendo da una chiave e mediante una seconda chiave
 * viene deciso quale alfabeto usare per criptare l'i-esimo carattere del messaggio
 * molto più sicuro del cifrario di cesare
 * @author Mirko
 * 
 */
public class Bellaso extends Cipher
{
  public String alphabet = "";
  
  public Bellaso(Object key) 
  {
    super(key);
    
    //creo alfabeto
    for(int i=0; i<26; i++)
     alphabet += (char)('a' + i);
    for(int i=0; i<26; i++)
     alphabet += (char)('A' + i);
    for(int i=0; i<10; i++)
     alphabet += (char)('0' + i);
  }

  @Override
  public String encrypt(String s) 
  {
	if(keyObj instanceof String[]) //servono 2 chiavi: una per creare gli alfabeti involutori, l'altra per scegliere quale alfabeto usare
	{
	  String tmp[] = (String[])keyObj;
	  String key1 = tmp[0];
	  String key2 = tmp[1];
	  
	  String alph00 = "", alph01 = ""; //1° alfabeto involutorio (parte superiore e inferiore) 
	  
	  HashSet<Character> charUsed = new HashSet<Character>(); //caratteri usati negli alfabeti
	  HashMap<Character, Integer> quadruplets = new HashMap<Character, Integer>(); //quaterne dei caratteri a cui è associato il numero di alfabeto da usare
	  
	  //crazione primo alfabeto involutorio
	  //divido la prima chiave sulle due sottostringhe
	  for(int i=0; i<Math.ceil(key1.length()/2); i++)
	  {
	    alph00 += key1.charAt(i);
	    charUsed.add(key1.charAt(i));
	  }
	  
	  for(int i=(int)Math.ceil(key1.length()/2); i<key1.length(); i++)
	  {
		alph01 += key1.charAt(i);
		charUsed.add(key1.charAt(i));
	  }
	  
	  //completo gli alfabeti
	  for(int i=0; i<alphabet.length(); i++)
	   if(!charUsed.contains(alphabet.charAt(i)))
	   {
		 if(alph00.length() < alphabet.length()/2)
		  alph00 += alphabet.charAt(i);
		 else
		  alph01 += alphabet.charAt(i);
	   }
	  
	  //creazione quaterne
	  for(int i=0; i<Math.ceil(alph00.length()/2); i++)
	  {
		quadruplets.put(alph00.charAt(i), i);  
		quadruplets.put(alph00.charAt(alph00.length()-i-1), i);  
		quadruplets.put(alph01.charAt(i), i);  
		quadruplets.put(alph01.charAt(alph01.length()-i-1), i);  
	  }
	  
	  //cripto tutti i caratteri della stringa
	  String ret = "";
	  int kind = 0;
	  for(int i=0; i<s.length(); i++) //cripto carattere per carattere O(L * N / 2)
	  {
		int offset = quadruplets.get(key2.charAt(kind)); //leggo alfabeto da usare
		for(int j=0; j<alph00.length(); j++)
		{
		  if(alph00.charAt(j) == s.charAt(i)) //carattere trovato nella prima metà 
		  { 
			int ind = j + offset; //shifto i caratteri
			if(ind >= alph00.length()) 
			 ind -= alph00.length();
			ret += alph01.charAt(ind); //aggiungo il corrispondente carattere della seconda metà
		  }
		  else
		   if(alph01.charAt(j) == s.charAt(i)) //carattere trovato nella seconda metà
		   {
			 int ind = j - offset; //shifto caratteri
			 if(ind < 0)
			  ind += alph00.length();
			 ret += alph00.charAt(ind); //aggiungo corrispondente carattere della prima metà
		   }
		}
		kind++; //aumento indice della chiave
		if(kind == key2.length())
		 kind = 0;
	  }
	  return ret;
	}
	return null;
  }

  @Override
  public String decrypt(String s) 
  {
    return encrypt(s); //criptando una stringa già criptata questa verrà decriptata
  }
}
