package it.MirkoGiacchini.menu.basic;

import it.MirkoGiacchini.menu.Label;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * font button con 3 scritte
 * @author Mirko
 */
public class TripleFontButton extends FontButton
{
  Label label2, label3;
  
  /**
   * label: label sx
   * label2: label centrale
   * label3: label dx
   */
  public TripleFontButton(Texture t1, Texture t2, float x, float y, float defW, float defH, Label label, Label label2, Label label3, Color c1, Color c2, boolean keepSelected)
  {
	super(t1, t2, x, y, defW, defH, label, c1, c2, keepSelected);
	
	this.label2 = label2;
	this.label3 = label3;
	recomputePositions();
  }
  
  @Override
  public void draw(SpriteBatch batch)
  {
	super.draw(batch);
	if(this.isPressed())
	{
	  label2.setColor(c1);
	  label3.setColor(c1);
	}
	else
	{
	  label2.setColor(c2);
	  label3.setColor(c2);
	}
    label2.draw(batch);
	label3.draw(batch);
  }
  
  @Override
  public void setText(String txt)
  {
	label.setText(txt);  
	recomputePositions();
  }
  
  public void setText2(String txt)
  {
	label2.setText(txt);  
	recomputePositions();
  }
  
  public void setText3(String txt)
  {
	label3.setText(txt);
	recomputePositions();
  }
  
  public void setX(float x)
  {
	this.x = x;  
	recomputePositions();
  }
  
  public void setY(float y)
  {
	this.y = y; 
	recomputePositions();
  }
  
  private void recomputePositions()
  {
	if(label != null && label2 != null && label3 != null)
	{
	  float totW = label.getWidth() + label2.getWidth() + label3.getWidth();
	  float space = (defWidth - totW) / 2;
	  if(space < 0) 
	   space = 0;
	
	  label.setX(x);  
	  label.setY(y + defHeight - (defHeight - label.getHeight())/2);
	  label.setMaxWidth(label.getWidth());
 	
 	  label2.setX(label.getX() + label.getWidth() + space);
 	  label2.setY(y + defHeight - (defHeight - label2.getHeight())/2);
	  label2.setMaxWidth(label2.getWidth());
	
	  label3.setX(label2.getX() + label2.getWidth() + space);
	  label3.setY(y + defHeight - (defHeight - label3.getHeight())/2);  
	  label3.setMaxWidth(label3.getMaxWidth());
	}
  }
  
  @Override
  public void dispose()
  {
	label2.dispose();
	label3.dispose();
	super.dispose();
  }
}
