package it.MirkoGiacchini.menu.basic;

import it.MirkoGiacchini.menu.Label;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * bottone con testo
 * @author Mirko
 *
 */
public class FontButton extends TextureButton
{
  Label label;
  Color c1, c2;
  
  public FontButton(Texture t1, Texture t2, float x, float y, float defW, float defH, Label label, Color c1, Color c2, boolean keepSelected) 
  {
	super(t1, t2, x, y, defW, defH, keepSelected);
	
	this.label = label;
	setText(label.getText());
	this.c1 = c1;
	this.c2 = c2;
  }
  
  public FontButton(Texture t1, Texture t2, float x, float y, float defW, float defH, Label label, Color c1, Color c2)
  {
	this(t1, t2, x, y, defW, defH, label, c1, c2, false);  
  }
  
  public void setText(String txt)
  {
	label.setText(txt);
	if(label.getWidth() > defWidth)
	 label.setX(x);
	else
	 label.setX(x + defWidth/2 - label.getWidth()/2);
		
	label.setY(y + defHeight - (defHeight - label.getHeight())/2);
	label.setMaxWidth(defWidth);
  }
  
  @Override
  public void draw(SpriteBatch batch)
  {
	if(isPressed())
	{
	  batch.draw(tex1, x, y, defWidth, defHeight);	
	  label.setColor(c1);
	}
	else
	{
	  batch.draw(tex2, x, y, defWidth, defHeight);	
	  label.setColor(c2);
	}
	
	label.draw(batch);
  }
  
  @Override
  public void dispose()
  {
	label.dispose();  
	super.dispose();
  }
}
