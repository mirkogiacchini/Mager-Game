package com.MirkoGiacchini.game.abstracted;

import java.util.ArrayList;
import java.util.HashSet;

import com.MirkoGiacchini.game.shop.Shop;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import it.MirkoGiacchini.game.abstracted.Player;
import it.MirkoGiacchini.menu.Label;
import it.MirkoGiacchini.pairing.Pair;

/**
 * player nei menu del videogioco
 * @author Mirko
 *
 */
public class GamePlayer extends Player
{
  public static final short HP = 0;
  public static final short CRIT = 1;
  public static final short PERF = 2;
  public static final short BOSS_DMG = 3;
  public static final short DARK = 4;
  public static final short LIGHT = 6;
  public static final short FIRE = 8;
  public static final short ICE = 10;
  public static final short LIGHTNING = 12;
  public static final short CHAOS = 14;
  
  /**tutti gli item del personaggio*/	
  HashSet<Integer> items = new HashSet<Integer>();
  /** item comprati durante l'attuale sessione di gioco*/
  HashSet<Integer> newItems = new HashSet<Integer>(); 
  
  /**statistiche base pg: hp, critici, perforanti, danni boss, (statistiche magie x12)*/
  public Stat stats[] = new Stat[16];
  
  public GamePlayer()
  {
	for(int i=0; i<stats.length; i++)
	{
	  stats[i] = new Stat();
	  stats[i].value = 0;
	  stats[i].descr = getDescr(i);
	}
  }
  
  @Override
  public String toRecord()
  {
	String str = super.toRecord();  //prendo stringa standard
	for(int i : newItems) //e ci aggiungo gli items
	 str += (" "+i);	
	return str;
  }
  
  public HashSet<Integer> getItems()
  {
	return items;  
  }
  
  public HashSet<Integer> getNewItems()
  {
	return newItems;  
  }
  
  /** aggiunge item alla lista ma non li compra (non verranno aggiornati nel db) */
  public void addItem(int a)
  {
	items.add(a);  
	ArrayList<Pair<Integer, Integer>> statInc = Shop.getItemInc(a);
	for(Pair<Integer, Integer> p : statInc)
	 stats[p.a].value += p.b;
  }
  
  /** 'comprare' nuovo item (verranno aggiornati i valori nel db)*/
  public void addNewItem(int a)
  {
	newItems.add(a);  
	addItem(a);
  }
  
  /** stampa i dati del giocatore nello shop*/
  public void drawOnShop(SpriteBatch batch, Label label, float w, float h)
  {
	label.setText("A");
	float hh = label.getHeight() * 1.15f;  
    label.setText("Stats:"); label.setX(0); label.setY(h - hh/2); label.draw(batch);
    label.setText("Pen: "+getPen()); label.setY(h - 2f * hh - 1); label.draw(batch);
    for(int i=0; i<stats.length; i++)
    {
      label.setText(stats[i].descr+": "+stats[i].value+"%");
      label.setY(h - (3f+i) * hh - 1);
      label.draw(batch);
    }
  }
  
  /**caratteristiche giocatore*/
  public static String getDescr(int i)
  {
    switch(i)
	{
	  case HP: return "HP"; 
	  case CRIT: return "Crit";
	  case PERF: return "Perf";
	  case BOSS_DMG: return "Boss dmg";
	  case DARK: return "Dark pow";
	  case DARK+1: return "Dark res";
	  case LIGHT: return "Light pow";
	  case LIGHT+1: return "Light res";
	  case FIRE: return "Fire pow";
	  case FIRE+1: return "Fire res";
	  case ICE: return "Ice pow";
	  case ICE+1: return "Ice res";
	  case LIGHTNING: return "Lightning pow";
	  case LIGHTNING+1: return "Lightning res";
	  case CHAOS: return "Chaos pow";
	  case CHAOS+1: return "Chaos res";
    }
    return "";
  }
}
