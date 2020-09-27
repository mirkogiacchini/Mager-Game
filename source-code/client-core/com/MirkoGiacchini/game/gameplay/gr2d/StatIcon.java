package com.MirkoGiacchini.game.gameplay.gr2d;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import it.MirkoGiacchini.menu.Image;
import it.MirkoGiacchini.menu.Label;

/**
 * icona per visualizzare valori delle statistiche, es:mana, hp, sp
 * @author Mirko
 *
 */
public class StatIcon 
{
  Image icon;
  private Label valueLab;
  
  /**
   * @param x x-coord
   * @param y y-coord
   * @param img icona
   * @param sx size-x label
   * @param sy size-y label
   * @param c colore label
   * @param fontPath font label
   * @param offset offset font label
   * @param vIn valore iniziale
   */
  public StatIcon(float x, float y, Image img, Label lb, int vIn)
  {
	icon = img;
	icon.setX(x);
	icon.setY(y);
	valueLab = lb;
	valueLab.setText(vIn+"");
	valueLab.setX(x + icon.getWidth());
	valueLab.setY(y + valueLab.getHeight());
  }
  
  public void setTexture(Texture t)
  {
	icon.setTexture(t);  
  }
  
  public void setValue(int v)
  {
	valueLab.setText(v+"");
  }
  
  public void setText(String s)
  {
	valueLab.setText(s);  
  }
  
  public void draw(SpriteBatch batch)
  {
	icon.draw(batch);
	valueLab.draw(batch);
  }
  
  public void dispose()
  {
	icon.dispose();
	valueLab.dispose();
  }
}
