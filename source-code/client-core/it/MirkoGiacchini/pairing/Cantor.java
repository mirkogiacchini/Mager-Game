package it.MirkoGiacchini.pairing;

/** pairing function di Cantor*/
public class Cantor 
{  
  /** dati due numeri ne genera uno unico*/
  public static int pair(int x, int y)
  {
	return (x + y) * (x + y + 1) / 2 + y;  
  }
  
  /** dato un numero trova i due numeri che lo generano*/
  public static Pair<Integer, Integer> invert(int z)
  {
	int w = (int)Math.floor( (Math.sqrt(8 * z + 1) - 1) / 2); 
	int t = (w*w + w) / 2;
	
	Pair<Integer, Integer> ret = new Pair<Integer, Integer>();
	ret.b = z - t;
	ret.a = w - ret.b;
	return ret;
  }
}  
