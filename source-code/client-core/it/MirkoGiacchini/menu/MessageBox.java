package it.MirkoGiacchini.menu;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/** message box */
public class MessageBox 
{
  Texture background;
  Label text;
  float defWidth, defHeight, x, y;
  Menu menu;
  boolean visible;
  
  public MessageBox(Texture b, float defWidth, float defHeight, float x, float y, String s, Color c, float sizeX, float sizeY, String fontPath)
  {
	background = b;
	this.defHeight = defHeight;
	this.defWidth = defWidth;
	this.x = x;
	this.y = y;
	text = new Label(s, x, y + defHeight*6/7, c, sizeX, sizeY, defWidth, fontPath);
	setText(s);
	visible = false;
  }
  
  public MessageBox(Texture b, float defWidth, float defHeight, float x, float y, String s, Color c, String fontPath)
  {
	this(b, defWidth, defHeight, x, y, s, c, 0.5f * defWidth / 50, 0.5f * defHeight / 50, fontPath);  
  }
  
  public void draw(SpriteBatch batch)
  {
	if(visible)
	{
	  batch.draw(background, x, y, defWidth, defHeight);  
	  text.draw(batch);
	}
  }
  
  public void setText(String text)
  {
	this.text.setText(text);  
	float w = this.text.getWidth();
	if(w <= this.text.getMaxWidth())
	 this.text.setX( x + defWidth/2 - w/2);
	else
	 this.text.setX(x + defWidth/20);
  }
  
  public boolean isVisible()
  {
	return visible;  
  }
  
  public void setVisible(boolean v)
  {
	visible = v;  
  }
  
  public void touchDown(float x, float y, int pointer)
  {
  }
  
  public void touchUp(float x, float y, int pointer)
  {  
  }
  
  public void touchDrag(float x, float y, int pointer)
  {
  }
  
  public void dispose()
  {
	background.dispose();  
	text.dispose();
  }
}
