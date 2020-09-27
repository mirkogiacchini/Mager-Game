package it.MirkoGiacchini.menu.basic;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import it.MirkoGiacchini.menu.Image;
import it.MirkoGiacchini.menu.Menu;

/** menu il cui sfondo è composto da più immagini*/
public class CollageMenu extends Menu
{
  /**immagini usate come sfondo*/
  protected ArrayList<Image> images;
  
  public CollageMenu(float defWidth, float defHeight)
  {
	super(defWidth, defHeight);
	images = new ArrayList<Image>();
  }
  
  public void addImage(Image m)
  {
	images.add(m);  
  }
  
  @Override
  public void add(Object obj)
  {
	if(obj instanceof Image)
	 addImage((Image)obj);
	else
	 super.add(obj);
  }
  
  @Override
  public void draw(SpriteBatch batch)
  {
	for(Image i : images)
	 i.draw(batch);  
	super.draw(batch);
  }
  
  @Override
  public void dispose()
  {
	super.dispose();
	for(Image im : images)
	 im.dispose();
  }
}
