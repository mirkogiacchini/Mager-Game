package it.MirkoGiacchini.menu;

import it.MirkoGiacchini.menu.basic.FontButton;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/** message box che può essere chiuso dall'utente con un bottone*/
public class InformationBox extends MessageBox
{
  FontButton okButton;
  
  public InformationBox(Texture b, float defWidth, float defHeight, float x, float y, String s, Color c, Texture tb1, Texture tb2, Color c1, Color c2, float sizeX, float sizeY, String fontPath, float sx, float sy) 
  {
	super(b, defWidth, defHeight, x, y, s, c, sizeX, sizeY, fontPath);
	okButton = new FontButton(tb1, tb2, x + defWidth/2 - defWidth/6, y, defWidth/3, defHeight/3, new Label("OK", 0, 0, c1, sx*defWidth/50, sy*defHeight/50, 0, fontPath), c1, c2, false)
	{
	  @Override
	  public void onReleased(int pointer)
	  {
		visible = false;  
		onButtonReleased();    
	  }
	};
  } 
  
  public InformationBox(Texture b, float defWidth, float defHeight, float x, float y, String s, Color c, Texture tb1, Texture tb2, Color c1, Color c2, String fontPath)
  {
	this(b, defWidth, defHeight, x, y, s, c, tb1, tb2, c1, c2, 0.5f * defWidth / 50, 0.5f * defHeight / 50, fontPath, .7f, .7f);  
  }
  
  @Override
  public void draw(SpriteBatch batch)
  {
	if(isVisible())
	{
	  super.draw(batch);
	  okButton.draw(batch);
	}
  }
  
  @Override
  public void touchUp(float x, float y, int pointer)
  {
	if(isVisible())  
	 okButton.touchUp(x, y, pointer);  
  }
  
  @Override
  public void touchDown(float x, float y, int pointer)
  {
	if(isVisible())  
	 okButton.touchDown(x, y, pointer);  
  }
  
  @Override
  public void touchDrag(float x, float y, int pointer)
  {
	if(isVisible())
	 okButton.touchDrag(x, y, pointer);   
  }
  
  /** chiamato quando l'ok viene premuto*/
  protected void onButtonReleased()
  {
  }
  
  @Override
  public void dispose()
  {
	super.dispose();
	okButton.dispose();
  }
}
