package it.MirkoGiacchini.menu;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;

/**
 * bottone generale
 * @author Mirko
 */
public abstract class Button implements Disposable
{
  /** id del tocco */
  protected int pointer;
  
  /** coordinata x */
  protected float x;
  
  /** coordinata y */
  protected float y;
  
  protected float defWidth, defHeight;
  
  protected boolean keepSelected; //il bottone viene mantenuto selezionato dopo averlo cliccato?
  
  /** menu di appartenenza */
  public Menu menu;
  
  /** routine di quando il bottone viene premuto */
  public abstract void onPressed(int pointer);
  
  /** routine rilasciamento */
  public abstract void onReleased(int pointer);
  
  /**routine di quando viene deselezionato*/
  public abstract void onDeselected(int pointer);
  
  /** stampa a video */
  public abstract void draw(SpriteBatch batch);
  
  /** il punto è nel bottone? */
  public boolean collide(float x, float y)
  {
	return (x >= this.x && x < this.x + defWidth && y >= this.y && y < this.y + defHeight);  
  }
  
  /** premuto? */
  public boolean isPressed()
  {
	return pointer != -1;  
  }
  
  public void setPointer(int p)
  {
	pointer = p; 
  }
  
  public int getPointer()
  {
	return pointer;  
  }
  
  public float getX()
  {
	return x;  
  }
  
  public float getY()
  {
	return y;  
  }
  
  public float getWidth()
  {
	return defWidth;  
  }
  
  public float getHeight()
  {
	return defHeight;  
  }
  
  public void touchDown(float x, float y, int pointer)
  {
    if(collide(x, y) && (!isPressed() || keepSelected) ) //bottone premuto
	{
	  setPointer(getPointer() == -1 ? pointer : -1);
	  onPressed(pointer);
	}
  }
  
  public void touchUp(float x, float y, int pointer)
  {
	if(collide(x, y) && getPointer() == pointer && !keepSelected)
	{
	  setPointer(-1);
	  onReleased(pointer);
	}	  
  }
  
  public void touchDrag(float x, float y, int pointer)
  {
	if(!collide(x, y) && getPointer() == pointer && !keepSelected) //sono andato via dal bottone
	{
	  onDeselected(pointer);		
	  setPointer(-1);
	}
	else
	 if(collide(x, y) && (!isPressed() || keepSelected)) //sono arrivato su un bottone
	 {
	   setPointer(pointer);
	   onPressed(pointer);
	 }
  }
}
