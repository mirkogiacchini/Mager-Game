package it.MirkoGiacchini.menu.basic;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import it.MirkoGiacchini.menu.Button;

/**
 * bottone con texture di sfondo
 * @author Mirko
 *
 */
public class TextureButton extends Button
{
  protected Texture tex1, tex2;
  
  /**
   * @param t1 texture selezionata
   * @param t2 texture non selezionata
   */
  public TextureButton(Texture t1, Texture t2, float x, float y, float defW, float defH, boolean keepSelected)
  {
	this.x = x;
	this.y = y;
	tex1 = t1;
	tex2 = t2;
	defWidth = defW;
	defHeight = defH;
	pointer = -1;
	this.keepSelected = keepSelected;
  }
  
  public TextureButton(Texture t1, Texture t2, float x, float y, float defW, float defH)
  {
	this(t1, t2, x, y, defW, defH, false);  
  }

  @Override
  public void draw(SpriteBatch batch) 
  {
	if(isPressed())
	 batch.draw(tex1, x, y, defWidth, defHeight); 	
	else
	 batch.draw(tex2, x, y, defWidth, defHeight);
  }
  
  @Override
  public void dispose()
  {
	tex1.dispose();
	tex2.dispose();
  }

  @Override
  public void onPressed(int pointer) 
  {
  }

  @Override
  public void onReleased(int pointer) 
  {
  }
  
  @Override
  public void onDeselected(int pointer)
  {
  }
}
