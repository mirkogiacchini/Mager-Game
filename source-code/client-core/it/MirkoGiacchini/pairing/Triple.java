package it.MirkoGiacchini.pairing;

public class Triple<T, T2, T3> 
{
  public T a;
  public T2 b;
  public T3 c;
  
  public Triple(){}
  
  public Triple(T a, T2 b, T3 c)
  {
	this.a = a;
	this.b = b;
	this.c = c;
  }
}
