package com.MirkoGiacchini.game.shop;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import it.MirkoGiacchini.menu.Image;
import it.MirkoGiacchini.menu.Label;
import it.MirkoGiacchini.menu.basic.FontButton;

/**
 * icone degli item nel negozio
 * @author Mirko
 *
 */
public class ItemIcon 
{
  Image icon;
  FontButton buyButton;
  Label label;
  int cost;
  
  /**
   * @param w width icon
   * @param h height icon
   * @param icon icona
   * @param x x_icona
   * @param y y_icona
   * @param label label della descrizione
   * @param cost costo dell'item
   * @param buttonT1 texture bottone selezionato
   * @param buttonT2 texture bottone non selezionato
   * @param bc1 colore bottone selezionato
   * @param bc2 colore bottone non selezionato
   * @param fontPath font per bottone
   * @param offset offset font bottone
   * @param sx size x font
   * @param sy size y font
   */
  public ItemIcon(float w, float h, Texture icon, float x, float y, Label label, int cost, Texture buttonT1, Texture buttonT2, Color bc1, Color bc2, String fontPath, float offset, float sx, float sy, float sx2, float sy2)
  {
	this.icon = new Image(icon, x, y + h/10, w, 9.f/10.f*h);
	label.setMaxWidth(w);
	float lw = label.getWidth();
	label.setSize(sx2 * w / 640, sy2 * 9.f/10*h / 480);
	label.setX(x + w/3 - lw/2); label.setY(y + h/6*5);
	this.label = label;
	this.cost = cost;
	buyButton = new FontButton(buttonT1, buttonT2, x, y, w, h/10, new Label("Buy ("+cost+" PEN)", sx*w/640, sy*h/480, 1, fontPath, offset), bc1, bc2)
	{
	  @Override
	  public void onReleased(int p)
	  {
		onBuyPressed();  
	  }
	};
  }
  
  public void draw(SpriteBatch batch)
  {
	icon.draw(batch);
	buyButton.draw(batch);
	label.draw(batch);
  }
  
  protected void onBuyPressed()
  {
  }
  
  public void onTouchDown(float x, float y, int pointer)
  {
	buyButton.touchDown(x, y, pointer);
  }
  
  public void onTouchUp(float x, float y, int pointer)
  {
	buyButton.touchUp(x, y, pointer);  
  }
  
  public void onTouchDrag(float x, float y, int pointer)
  {
	buyButton.touchDrag(x, y, pointer);  
  }
  
  /** da chiamare quando si compra l'item*/
  protected void onBought()
  {
	buyButton.setText("Bought!");
	buyButton.setPointer(-2);
  }
  
  /**metodo per "restituire" l'item*/
  protected void sell()
  { 
	buyButton.setText("Buy ("+cost+" PEN)");
	buyButton.setPointer(-1);
  }
  
  public void dispose()
  {
	icon.dispose();
	buyButton.dispose();
	label.dispose();
  }
}
