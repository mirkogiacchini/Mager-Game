package it.MirkoGiacchini.menu.basic;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;

import it.MirkoGiacchini.menu.Label;
import it.MirkoGiacchini.menu.TextField;
import it.MirkoGiacchini.menu.TextFilter;
import it.MirkoGiacchini.util.Util;

/**
 * textfield con texture in background
 * @author Mirko
 *
 */
public class TextureField extends TextField
{
  Texture tex1, tex2;
  BitmapFont font;
  float defWidth, defHeight;
  
  float charHeight;
  float offset;
  
  private boolean updateAnimation; //bisogna eseguire animazione cursore?
  
  /**
   * @param t1 texture selezionata
   * @param t2 texture non selezionata
   */
  public TextureField(Texture t1, Texture t2, float x, float y, float w, float h, float sizeX, float sizeY, Color c, boolean encrypted, boolean keepSelecting, TextFilter filter, int maxCar, String fontPath, float offset)
  {
	super(encrypted, keepSelecting, filter, maxCar);  
	this.offset = offset;
	tex1 = t1;
	tex2 = t2;
	this.x = x;
	this.y = y;
	pointer = -1;
	defWidth = w;
	defHeight = h;
	font = new BitmapFont(Util.getHandle(fontPath));
	font.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
	setSize(sizeX, sizeY);
	font.setColor(c);
	updateAnimation = false;
  }
  
  public TextureField(Texture t1, Texture t2, float x, float y, float w, float h, float sizeX, float sizeY, Color c, boolean encrypted, int maxCar, String fontPath, float offset)
  {
	this(t1, t2, x, y, w, h, sizeX, sizeY, c, encrypted, false, TextFilter.NO_FILTER, maxCar, fontPath, offset);  
  }
  
  public TextureField(Texture t1, Texture t2, float x, float y, float w, float h, float sizeX, float sizeY, Color c, boolean encrypted, boolean keepSelecting, TextFilter filter, int maxCar, String fontPath)
  {
	this(t1, t2, x, y, w, h, sizeX, sizeY, c, encrypted, keepSelecting, filter, maxCar, fontPath, .5f);  
  }
  
  public TextureField(Texture t1, Texture t2, float x, float y, float w, float h, float sizeX, float sizeY, Color c, boolean encrypted, int maxCar, String fontPath)
  {
	this(t1, t2, x, y, w, h, sizeX, sizeY, c, encrypted, maxCar, fontPath, .5f);
  }

  @Override
  public void draw(SpriteBatch batch)
  {
	if(isSelected())
	 batch.draw(tex1, x, y, defWidth, defHeight);
	else
	 batch.draw(tex2, x, y, defWidth, defHeight);
	
	float diff = defHeight - charHeight;
	
	if(isSelected())
	{
	  font.draw(batch, getAnimText(), x, y + defHeight - diff/2, 0, getAnimText().length(), defWidth, Align.left, true); //charHeight  + (defHeight/2 - charHeight/2)
	  if(!updateAnimation) //selezionato -> animazione cursore
	  {
		updateAnimation = true;
		switchAnim(0.7f);
	  }
	} 
	else
	{
	  font.draw(batch, getPrintableText(), x, y + defHeight - diff/2, 0, getPrintableText().length(), defWidth, Align.left, true);
	  updateAnimation = false; //niente animazione cursore
	  cursor = "";
	}
  }
  
  public void setSize(float sizeX, float sizeY)
  {
	font.getData().setScale(sizeX * offset, sizeY * offset);
	Label.glyph.setText(font, getText());
	charHeight = Label.glyph.height;
  }
  
  @Override
  public boolean collide(float x, float y) 
  {
 	return (x >= this.x && x <= this.x + defWidth && y >= this.y && y <= this.y + defHeight);
  }
  
  @Override
  public void dispose() 
  {
	tex1.dispose();
	tex2.dispose();
  }
  
  private void switchAnim(final float delay)
  {
	Timer.schedule(new Task()
	{
	  @Override
	  public void run()
	  {
		if(updateAnimation)
		{
		  cursor = (cursor.equals("|")) ? "" : "|"; 	
		  switchAnim(delay);
		}
	  }
	}, delay);  
  }
}
