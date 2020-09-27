package it.MirkoGiacchini.menu.basic;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * analogico per android
 * @author Mirko
 *
 */
public class AnalogStick 
{
  //parte esterna ed interna dell'analogico
  Texture ext, inte;
  float x, y; //coordinate analogico: parte esterna
  float inX, inY;
  float centerX, centerY; //coordinate centro analogico
  int pointer;
  float defWidth, defHeight;
  float defWidth2, defHeight2;
  
  float angle;
  
  /**
   * parte esterna maggiore di parte interna!
   * defWidth, defHeight: dimensioni parte esterna
   * defWidth2, defHeight2: dimensioni parte interna
   */
  public AnalogStick(float x, float y, Texture ext, Texture in, float defWidth, float defHeight, float defWidth2, float defHeight2)
  {
	this.x = x;
	this.y = y;
	this.ext = ext;
	inte = in;
	centerX = x + defWidth/2 - defWidth2/2;
	centerY = y + defHeight/2 - defHeight2/2; 
	inX = centerX;
	inY = centerY;
	this.defHeight = defHeight;
	this.defWidth = defWidth;
	this.defHeight2 = defHeight2;
	this.defWidth2 = defWidth2;
	pointer = -1;
	angle = Float.MAX_VALUE;
  }
  
  /**
   * @return true se pointer viene usato dall'analogStick, false altrimenti
   */
  public boolean touchDown(int screenX, int screenY, int pointer)
  {
	if(!isPressed() && collide(screenX, screenY))
	{
      this.pointer = pointer;		
      computeInternalCoords(screenX, screenY);
      return true;
	}
	return false;
  }
  
  public void touchUp(int screenX, int screenY, int pointer)
  {
	if(getPointer() == pointer)
	{
	  this.pointer = -1;
	  angle = Float.MAX_VALUE;
	  inX = centerX;
	  inY = centerY;
	}
  }
  
  /**
   * @return true se 'pointer' viene usato dall'analogstick, false altrimenti (da usare per non attivare analogstick contemporaneamente a bottoni)
   */
  public boolean touchDrag(int screenX, int screenY, int pointer)
  {
	if(getPointer() == pointer)
	{
	  computeInternalCoords(screenX, screenY);
	  return true;
	} 
	else
	 return touchDown(screenX, screenY, pointer);
  }
  
  /**ricalcola coordinate*/
  private void computeInternalCoords(int sx, int sy)
  {
	angle = (float)Math.atan2(sy - centerY, sx - centerX) * 180 / 3.14f;
	
	if(Math.sqrt(Math.pow(sx - centerX, 2) + Math.pow(sy - centerX, 2)) > defWidth/2)
	{
      inX = centerX + getCos() * defWidth/2;
      inY = centerY + getSin() * defWidth/2;
	}
	else
	{
	  inX = sx - defWidth2/2;
   	  inY = sy - defHeight2/2;
	}
  }
  
  public void render(SpriteBatch batch)
  {
	batch.draw(ext, x, y, defWidth, defHeight);
	batch.draw(inte, inX, inY, defWidth2, defHeight2);
  }
  
  public boolean isPressed()
  {
	return pointer != -1;  
  }
  
  public int getPointer()
  {
	return pointer;  
  }
  
  public boolean collide(float x, float y)
  {
	return (x >= this.x && x <= this.x + defWidth && y >= this.y && y <= this.y + defHeight);  
  }
  
  /**coseno dell'angolo formato dall'analogico*/
  public float getCos()
  {
	return (float)Math.cos(angle * 3.14f / 180); 
  }
  
  /**seno dell'angolo formato dall'analogico*/
  public float getSin()
  { 
	return (float)Math.sin(angle * 3.14f / 180); 
  }
  
  public int getAngle()
  {
	return angle == Float.MAX_VALUE ? Integer.MAX_VALUE : (int)angle; 
  }
  
  public void dispose()
  {
	ext.dispose();
	inte.dispose();
  }
}
