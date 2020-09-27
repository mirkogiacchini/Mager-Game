package it.MirkoGiacchini.menu.basic;

import it.MirkoGiacchini.menu.Label;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * classe usata per far selezionare un valore all'utente
 * @author Mirko
 *
 */
public class ValueSelector 
{ 
  /** descrizioni dei possibili valori */
  String valueDescr[];
  int actual = 0;
  
  TextureButton sx, dx;
  Texture background;
  
  Label label;
  
  float x, y, defWidth, defHeight;
  
  /**
   * @param valueD descrizioni dei possibili valori
   * @param sx1 texture sx selezionata
   * @param sx2 texture sx non selezionata
   * @param dx1 texture dx selezionata
   * @param dx2 texture dx non selezionata
   * @param back texture background
   * @param x coord x 
   * @param y coord y
   * @param defW width standard (al momento creazione)
   * @param defH height standard (al momento creazione)
   * @param sizeX dimensione x label
   * @param sizeY dimensione y label
   * @param labC colore label
   * @param fontPath percorso font label
   */
  public ValueSelector(String valueD[], Texture sx1, Texture sx2, Texture dx1, Texture dx2, Texture back, float x, float y, float defW, float defH, float sizeX, float sizeY, Color labC, String fontPath)
  {
	valueDescr = valueD;  
	background = back;
	this.x = x;
	this.y = y;
	defWidth = defW;
	defHeight = defH;
	
	sx = new TextureButton(sx1, sx2, x, y, defH, defH, false)
	{
	  @Override
	  public void onReleased(int pointer)
	  {
		actual--;
		if(actual < 0)
		 actual = valueDescr.length - 1;
	  }
	};
	
	dx = new TextureButton(dx1, dx2, x + defW - defH, y, defH, defH, false)
	{
	  @Override
	  public void onReleased(int pointer)
	  {
		actual++;
		if(actual >= valueDescr.length)
		 actual = 0; 
	  }
	};
	
	label = new Label("", x + defH +1, y + defH, labC, sizeX, sizeY, defW - 2*defH, fontPath);
	float diff = defH - label.getHeight();
	label.setY(label.getY() - diff/2);
  }
  
  public int getIndex()
  {
	return actual;  
  }
  
  public void touchDown(float x, float y, int pointer)
  {
	sx.touchDown(x, y, pointer);  
	dx.touchDown(x, y, pointer);
  }
  
  public void touchUp(float x, float y, int pointer)
  {
	sx.touchUp(x, y, pointer);  
	dx.touchUp(x, y, pointer);
  }
  
  public void touchDrag(float x, float y, int pointer)
  {
	sx.touchDrag(x, y, pointer);  
	dx.touchDrag(x, y, pointer);
  }
  
  public void draw(SpriteBatch batch)
  {
	if(background != null)  
	 batch.draw(background, x, y, defWidth, defHeight);
	sx.draw(batch);
	dx.draw(batch);
	label.setText(valueDescr[actual]);
	label.draw(batch);
  }
  
  public void dispose()
  {
	 sx.dispose();
	 dx.dispose();
	 label.dispose();
	 if(background != null)
	  background.dispose();
  }
}
