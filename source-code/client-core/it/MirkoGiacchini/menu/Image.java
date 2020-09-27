package it.MirkoGiacchini.menu;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;

/** immagine per menu */
public class Image implements Disposable
{
  Texture img;
  float x, y;
  float defWidth, defHeight;
  
  public Image(Texture img, float x, float y, float w, float h)
  {
	this.img = img;
	this.x = x;
	this.y = y;
	defWidth = w;
	defHeight = h;
  }
  
  public Image(Texture img, float w, float h)
  {
    this(img, 0, 0, w, h);	  
  }
  
  /** stampa immagine */
  public void draw(SpriteBatch batch)
  {
	batch.draw(img, x, y, defWidth, defHeight);  
  }
  
  public void setTexture(Texture t)
  {
	img = t;
  }
  
  public void setX(float x)
  {
	this.x = x;  
  }
  
  public void setY(float y)
  {
	this.y = y;  
  }
  
  public float getWidth()
  {
	return defWidth;  
  }
  
  public float getHeight()
  {
	return defHeight;  
  }
  
  @Override
  public void dispose()
  {
	img.dispose();  
  }
}
