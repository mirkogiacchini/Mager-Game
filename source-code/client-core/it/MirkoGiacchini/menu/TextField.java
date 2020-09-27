package it.MirkoGiacchini.menu;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;

public abstract class TextField implements Disposable
{
  //stringa a sinistra del cursore
  String left;
  //stringa a destra del cursore
  String right;
  boolean encrypted, keepSelecting; //criptato? va mantenuto selezionato mentre si premono bottoni?
  
  protected float x, y;
  
  protected int pointer;
  
  //string cursore
  protected String cursor;
  
  //filtro dei caratteri che possono essere scritti
  TextFilter filter;
  
  int maxCar; //massimi caratteri?
  
  public Menu menu;
  
  public TextField(boolean encrypted, boolean keepSelecting, TextFilter tfil, int maxCar)
  {
	left = "";
	right = "";
	cursor = "";
	this.encrypted = encrypted;
	this.keepSelecting = keepSelecting;
	this.filter = tfil;
	this.maxCar = maxCar;
  }
  
  public TextField(boolean encrypted, boolean keepSelecting, int maxCar)
  {
	this(encrypted, keepSelecting, TextFilter.NO_FILTER, maxCar);  
  }
  
  /** funzione chiamata quando si digita un tasto*/
  public void onKeyTyped(char key)
  {
	if(canBeTyped(key) && getText().length() < maxCar)
	 left += key;
  }
  
  /** funzione chiamata quando si rilascia un tasto */
  public void onKeyReleased(int key)
  {
    if(key == 21 && left.length() > 0) //freccia sx -> sposto carattere a destra
    {
      right = left.substring(left.length()-1, left.length()) + right;
      left = left.substring(0, left.length()-1);
    }
    
    if(key == 22 && right.length() > 0) //freccia dx -> sposto carattere a sinistra
    {
      left += right.substring(0, 1);
      right = right.substring(1, right.length());
    }
    
    if(key == 67 && left.length() > 0) //backspace -> cancello carattere
     left = left.substring(0, left.length()-1);
  }
  
  public boolean isSelected()
  {
	return pointer != -1;  
  }
  
  /**
   * setta id del tocco
   */
  public void setPointer(int p)
  {
	pointer = p;  
  }
  
  /**
   * @return id del tocco
   */
  public int getPointer()
  {
	return pointer;  
  }
  
  /**
   * @return testo in chiaro
   */
  public String getText()
  {
	return left+right;  
  }
  
  /**
   * @return testo che può essere stampato a video
   */
  public String getPrintableText()
  {
	if(!encrypted)
	 return getText();
	else
	 return getEncrypted(false);
  }
  
  public boolean equals(TextField tf)
  {
	return (tf.left.equals(tf.left) && tf.right.equals(tf.right) && pointer == tf.pointer &&
			x == tf.x && y == tf.y && encrypted == tf.encrypted && keepSelecting == tf.keepSelecting);  
  }
  
  /**
   * @return testo animato (contiene il cursore)
   */
  public String getAnimText()
  {
	if(!encrypted)
	 return left+cursor+right;  
	else
	 return getEncrypted(true);
  }
  
  /**
   * @param cursor bisogna considerare il cursore?
   * @return stringa criptata
   */
  public String getEncrypted(boolean cursor)
  {
	String sxc = "", dxc = "";
	for(int i=0; i<left.length(); i++)
	 sxc+='¤';
	for(int i=0; i<right.length(); i++)
	 dxc+='¤';
	String ret = cursor ? sxc + this.cursor + dxc : sxc + dxc;
	return ret;  
  }
  
  private boolean canBeTyped(char key)
  {
	switch(filter)  
	{
	  case NO_FILTER: return (key >= 32 && key <= 126);
	  case PSW_FILTER: return ( (key >= 'a' && key <= 'z') || (key >= 'A' && key <= 'Z') || (key >= '0' && key <= '9') );
	  case EMAIL_FILTER: return ( (key >= 'a' && key <= 'z') || (key >= 'A' && key <= 'Z') || (key >= '0' && key <= '9') || key == '.' || key == '@');
	  case IP_FILTER: return ((key >= '0' && key <= '9') || key == '.');
	}
	return false;
  }
  
  public void touchDown(float x, float y, int pointer)
  {
	if(collide(x, y) && !isSelected()) //text field premuta
	{
	  setPointer(pointer); 	 
	  menu.deselectFieldFiltered(this);
	}
	else
	 if(collide(x, y) || !keepSelecting) //text field da deselezionare	
	  setPointer(-1); 
  }
  
  public void touchDrag(float x, float y, int pointer)
  {
	if(!collide(x, y) && getPointer() == pointer && !keepSelecting) //andato via dal text field
     setPointer(-1);
    else
	 if(collide(x, y) && !isSelected()) //arrivato sul textfield
	 {
	   setPointer(pointer);	 
	   menu.deselectFieldFiltered(this);
	 }
  }
  
  public void keyTyped(char c)
  {
	if(isSelected())
	 onKeyTyped(c);	  
  }
  
  public void keyReleased(int key)
  {
	if(isSelected())
	 onKeyReleased(key);    
  }
  
  public abstract void draw(SpriteBatch batch);
  public abstract boolean collide(float x, float y);
}
