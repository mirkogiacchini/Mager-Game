package it.MirkoGiacchini.game.abstracted;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import it.MirkoGiacchini.menu.Label;

/** player astratto... definisce solamente i caratteri generali di un giocatore */
public class Player 
{ 
  public static final int MAX_LV = 100;
  String name; //nome giocatore (corrisponde a username)
  int lv; //livello player
  int exp; //esperienza attuale del giocatore
  int pen; //soldi del giocatore
  
  /** restituisce esperienza necessaria in base al livello */
  public int getNeededExp()
  {
	if(lv == MAX_LV) return exp;  
	float l = lv > 90 ? 1 + (float)Math.sin((lv - 90) * 3.14f / 180) : (float)Math.sin(lv * 3.14f / 180); //all'aumentare del livello aumenta il fattore 'l'
	return (int)(l * 1000);
  }
  
  public void set(String name, int lv, int exp, int pen)
  {
	this.name = name;
	this.lv = lv;
	this.exp = exp;
	this.pen = pen;
  }
  
  public void setLv(int lv)
  {
	this.lv = lv;  
  }
  
  public void setExp(int exp)
  {
	this.exp = exp;  
  }
  
  public void setPen(int pen)
  {
	this.pen = pen;  
  }
  
  public String getName()
  {
	return name;  
  }
  
  public int getLv()
  {
	return lv;  
  }
  
  public int getExp()
  {
	return exp;  
  }
  
  public int getPen()
  {
	return pen;  
  }
  
  public void drawLobbyMenu(SpriteBatch batch, Label playerLab, float defWidth, float defHeight)
  {
     playerLab.setText("A");  
	 float h = playerLab.getHeight() * 1.2f;
	 playerLab.set(getName(), 0, defHeight-defHeight/10-2, 3*defWidth/640, 3*defHeight/480, defWidth/2);
	 playerLab.draw(batch);
	 playerLab.set("Lv "+getLv(), 0, defHeight-defHeight/10-2-h, 3*defWidth/640, 3*defHeight/480, defWidth/2);
	 playerLab.draw(batch);
	 playerLab.set("Exp "+getExp()+"/"+getNeededExp(), 0, defHeight-defHeight/10f-2-h*2, 3*defWidth/640, 3*defHeight/480, defWidth/2);
	 playerLab.draw(batch);
	 playerLab.set("Pen "+getPen(), 0, defHeight-defHeight/10f-2-h*3, 3*defWidth/640, 3*defHeight/480, defWidth/2);
	 playerLab.draw(batch);  
  }
  
  /** trasforma il player in un record per l'update*/
  public String toRecord()
  {
	return name+" "+lv+" "+exp+" "+pen; //nel player base gli unici attributi da aggiornare sono livello, exp e soldi (name per cercarlo nel db)
  }
  
  public static int getNeededExp(int lv)
  {
	if(lv == MAX_LV) return -1;  
	float l = lv > 90 ? 1 + (float)Math.sin((lv - 90) * 3.14f / 180) : (float)Math.sin(lv * 3.14f / 180); //all'aumentare del livello aumenta il fattore 'l'
	return (int)(l * 1000);  
  }
}
