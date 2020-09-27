package com.MirkoGiacchini.game.gameplay;

import it.MirkoGiacchini.world3d.PhysicsEntity;

/**
 * valore dei vari rigid-body.
 * Il valore delle spell è un valore positivo
 * @author Mirko
 *
 */
public class CollisionObjValue 
{
  public static final int DEF = PhysicsEntity.DEF_VALUE;

  public static boolean isPlayerValue(int v)
  {
	return v <= -1000;  
  }
  
  /**
   * @param id del player
   * @return value del player
   */
  public static int getPlayerValue(int id)
  {
	return - id - 1000;  
  }
  
  /**
   * @param v value del player
   * @return id del player
   */
  public static int getPlayerId(int v)
  {
	if(!isPlayerValue(v)) return DEF;
	return - v - 1000;  
  }
  
  public static boolean isSpellValue(int v)
  {
	return v >= 0;  
  }
  
  /**
   * @param id della spell
   * @return value della spell
   */
  public static int getSpellValue(int id)
  {
	return id;  
  }
  
  /**
   * @param v della spell
   * @return id della spell
   */
  public static int getSpellId(int v)
  {
	return v;  
  }
  
  public static boolean isBossValue(int v)
  {
	return v == -5;  
  }
  
  public static int getBossValue()
  {
	return -5;  
  }
}
