package com.MirkoGiacchini.game.gameplay.items;

import com.MirkoGiacchini.game.gameplay.Player3D;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.math.Matrix4;

import it.MirkoGiacchini.world3d.Entity;

/**
 * item presente nel gioco
 * @author Mirko
 *
 */
public class IngameItem extends Entity
{
  ItemType type;
  boolean taken;
  int value;
  int respTime; //tempo di respawn
  long last;
	
  public IngameItem(Model model, Matrix4 transform, ItemType type, int v, int respTime) 
  {
    super(model, 0, transform);
    this.type = type;
    value = v;
    taken = false;
    this.respTime = respTime;
    last = -1;
  }
  
  @Override
  public void update(float deltaTime)
  {
	super.update(deltaTime);
	if(isTaken())
	 if(System.currentTimeMillis() - last >= respTime * 1000)
	  taken = false;
  }
  
  public boolean isTaken()
  {
	return taken;  
  }
  
  /**
   * da chiamare quando viene preso da un giocatore
   * @param player
   */
  public void onTaken(Player3D player)
  {
	if(!isTaken())
	{
	  if(type == ItemType.HP)
	   player.setHp(player.getHp() + value);
	  else
	   player.setMana(player.getMana() + value);
	  last = System.currentTimeMillis();
	}
	taken = true;
  }
  
  public void setLast(long ls)
  {
	last = ls;  
  }
  
  public void setTaken(boolean b)
  {
	taken = b;  
  }
  
  public long getLast()
  {
	return last;  
  }
  
  public int getValue()
  {
	return value;  
  }
  
  public ItemType getType()
  {
	return type;  
  }
}
