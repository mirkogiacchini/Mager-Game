package com.MirkoGiacchini.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import it.MirkoGiacchini.menu.basic.TextureButton;

/**
 * bottone per indicare attivazione/disattivazione di qualche opzione
 * @author Mirko
 *
 */
public class ActiveButton extends TextureButton
{
  boolean active;
  
  public ActiveButton(Texture t1, Texture t2, float x, float y, float defW, float defH, boolean a) 
  {
    super(t1, t2, x, y, defW, defH);
    active = a;
  }
  
  public boolean isActive()
  {
	return active;  
  }
  
  @Override
  public void onReleased(int p)
  {
	active = !active;  
  }
  
  @Override
  public void draw(SpriteBatch b)
  {
	if(active)
	 b.draw(tex1, x, y, defWidth, defHeight);
	else
	 b.draw(tex2, x, y, defWidth, defHeight);
  }
}
