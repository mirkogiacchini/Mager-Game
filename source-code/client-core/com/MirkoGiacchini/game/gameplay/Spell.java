package com.MirkoGiacchini.game.gameplay;

import com.MirkoGiacchini.game.abstracted.GamePlayer;


/**
 * magia astratta
 * @author Mirko
 *
 */
public class Spell
{
  int damage;
  int crit;
  int perf;
  int manaNeed;
  int recNeed;
  int bossDmg;
  long lastFired;
	
  public Spell(int type, int stats[]) 
  {
	computeStats(type, stats);
	lastFired = -1;
  }
  
  public Spell()
  {
	lastFired = -1;  
  }
  
  public void computeStats(int type, int stats[])
  {
	float mul = (float)stats[type]/100.f + 1;
	damage = round(mul * getDmg(type));
	crit = round(mul * getCrit(type));
	perf = round(mul * getPerf(type));
	manaNeed = getManaNeed(type);
	recNeed = getRecNeed(type);
	bossDmg = round(mul * getBossDmg(type));
  }
  
  private int getDmg(int type)
  {
	switch(type)
	{
	 case GamePlayer.DARK: return 20;
	 case GamePlayer.LIGHT: return 20;
	 case GamePlayer.FIRE: return 5;
	 case GamePlayer.ICE: return 15;
	 case GamePlayer.LIGHTNING: return 35;
	 case GamePlayer.CHAOS: return 150;
	}
	return 0;
  }
  
  private int getCrit(int type)
  {
	if(type == GamePlayer.DARK) return 2;  
	return 0;  
  }
  
  private int getPerf(int type)
  {  
	if(type == GamePlayer.LIGHT) return 2;
	return 0;  
  }
  
  private int getBossDmg(int type)
  {
	if(type == GamePlayer.FIRE) return 30;
	if(type == GamePlayer.ICE) return 10;
	return 1;  
  }
  
  private int getManaNeed(int type)
  {
	switch(type)
	{
	  case GamePlayer.DARK:
	  case GamePlayer.LIGHT:
	  case GamePlayer.LIGHTNING: return 2;
	  case GamePlayer.FIRE:
	  case GamePlayer.ICE: return 1;
	  case GamePlayer.CHAOS: return 20;
	}
	return 0;
  }
  
  private int getRecNeed(int type)
  {
	switch(type)
	{
	  case GamePlayer.DARK:
	  case GamePlayer.LIGHT: return 600;
	  case GamePlayer.LIGHTNING: return 450;
	  case GamePlayer.FIRE: return 400;
	  case GamePlayer.ICE: return 500;
	  case GamePlayer.CHAOS: return 1500;
	}
	return 0;  
  }
  
  private int round(float f)
  {
	return (int)Math.ceil(f);  
  }
}
