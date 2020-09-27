package it.MirkoGiacchini.menu.basic;

import it.MirkoGiacchini.menu.Label;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * permette all'utente di definire un oggetto, ad esempio una stanza
 * @author Mirko
 *
 */
public class ObjCreator 
{
  protected ArrayList<ValueSelector> values = new ArrayList<ValueSelector>();
  float x, y, defWidth, defHeight;
  Texture background;
  FontButton okButton;
  FontButton cancelButton;
  boolean visible;
  
  /**
   * @param x coordinata x
   * @param y coordinata y
   * @param defW width default
   * @param defH height default
   * @param back background
   * @param vD lista dei possibili valori per le valueSelector
   * @param sx1 tasto sx selezionato
   * @param sx2 tasto sx non selezionato
   * @param dx1 tasto dx selezionato
   * @param dx2 tasto dx non selezionato
   * @param c colore
   * @param fontPath percorso font
   * @param sizeX dimensione x font
   * @param sizeY dimensiont y font
   * @param btex1 texture bottone premuto
   * @param btex2 texture bottone non premuto
   * @param bc1 colore bottone premuto 
   * @param bc2 colore bottone non premuto
   */
  public ObjCreator(float x, float y, float defW, float defH, Texture back, ArrayList<String[]> vD, Texture sx1, Texture sx2, Texture dx1, Texture dx2, Color c, String fontPath, float sizeX, float sizeY,
		            Texture btex1, Texture btex2, Color bc1, Color bc2)
  {
    background = back;
    this.x = x;
    this.y = y;
    defWidth = defW;
    defHeight = defH;
    visible = false;
    
    int i = 1;
    float h = defHeight/(vD.size() + 1);
    for(String[] s : vD)
    {
      values.add(new ValueSelector(s, sx1, sx2, dx1, dx2, null, x, y + defHeight - h * i, defWidth, h, sizeX, sizeY, c, fontPath));  
      i++;
    }
    
    okButton = new FontButton(btex1, btex2, x, y, defWidth/2, h, new Label("OK", 0, 0, bc1, sizeX, sizeY, 0, fontPath), bc1, bc2)
    {
      @Override
      public void onReleased(int pointer)
      {
    	objectCreated();  
      }
    };
    
    cancelButton = new FontButton(btex1, btex2, x + defWidth/2, y, defWidth/2, h, new Label("Cancel", 0, 0, bc1, sizeX, sizeY, 0, fontPath), bc1, bc2)
    {
      @Override
      public void onReleased(int pointer)
      {
    	setVisible(false);  
      }
    };
  }
  
  public void objectCreated()
  {
	  
  }
  
  public void setVisible(boolean b)
  {
	visible = b;  
  }
  
  public boolean isVisible()
  {
	return visible;  
  }
  
  public void touchDown(float x, float y, int pointer)
  {
	if(isVisible())
	{
	  for(ValueSelector vs : values)
	   vs.touchDown(x, y, pointer);
      okButton.touchDown(x, y, pointer);
      cancelButton.touchDown(x, y, pointer);
	}
  }
  
  public void touchUp(float x, float y, int pointer)
  {
	if(isVisible())
	{ 
	  for(ValueSelector vs : values)
	   vs.touchUp(x, y, pointer);
	  okButton.touchUp(x, y, pointer);
	  cancelButton.touchUp(x, y, pointer);
	}
  }
  
  public void touchDrag(float x, float y, int pointer)
  {
	if(isVisible())
	{
	  for(ValueSelector vs : values)
	   vs.touchDrag(x, y, pointer);
	  okButton.touchDrag(x, y, pointer);
	  cancelButton.touchDrag(x, y, pointer);
	}
  }
  
  public void draw(SpriteBatch batch)
  {
	if(isVisible())
	{
	  batch.draw(background, x, y, defWidth, defHeight);
	  okButton.draw(batch);
	  cancelButton.draw(batch);
	  for(ValueSelector v : values)
	   v.draw(batch);
	}
  }
  
  public void dispose()
  {
	background.dispose();
	okButton.dispose();
	cancelButton.dispose();
	for(ValueSelector v : values)
	 v.dispose();
  }
}
