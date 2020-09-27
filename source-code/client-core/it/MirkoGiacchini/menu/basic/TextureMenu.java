package it.MirkoGiacchini.menu.basic;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import it.MirkoGiacchini.menu.Menu;

/** menu con una texture come sfondo */
public class TextureMenu extends Menu
{
  /** immagine background */
  protected Texture background;
  
  public TextureMenu(Texture background, float defW, float defH)
  {
	super(defW, defH);
	this.background = background;  
  }
  
  @Override
  public void draw(SpriteBatch batch) 
  {
	if(background != null)  
	 batch.draw(background, 0, 0, defWidth, defHeight);	
	super.draw(batch);
  }
  
  @Override
  public void dispose()
  {
	if(background != null)  
	 background.dispose();
	super.dispose();
  }
}
