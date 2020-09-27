package com.MirkoGiacchini.game.gameplay.gr2d;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import com.MirkoGiacchini.game.AssetConstants;
import com.MirkoGiacchini.game.gameplay.EnemyPlayer3D;
import com.MirkoGiacchini.game.gameplay.Gameplay;
import com.MirkoGiacchini.game.gameplay.Player3D;
import com.MirkoGiacchini.game.gameplay.boss.Boss;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Input.Keys;

import it.MirkoGiacchini.menu.Image;
import it.MirkoGiacchini.menu.Label;
import it.MirkoGiacchini.menu.basic.AnalogStick;
import it.MirkoGiacchini.menu.basic.FontButton;
import it.MirkoGiacchini.menu.basic.TextureButton;
import it.MirkoGiacchini.pairing.Triple;
import it.MirkoGiacchini.util.Util;
import it.MirkoGiacchini.world3d.Entity;

/**
 * Hud del gioco
 * @author Mirko
 *
 */
public class Hud 
{
  AnalogStick stick; //solo su android
  FontButton flyButton, exmenuButton, fireButton;
  TextureButton leftI, rightI;
  
  StatIcon spIcon, hpIcon, manaIcon;
  StatIcon spellIcon;
  
  float defWidth, defHeight;
  
  Texture spellTextures[] = new Texture[6];
  
  Label timeLabel;
  
  Label enemiesLabel;
  
  Label scoreboardLabel;
  ArrayList<Triple<String, Integer, Integer>> scoreboard = new ArrayList<Triple<String, Integer, Integer>>(); //a = nome, b = kills, c = deaths
  
  public static final int MSG_DURATION = 3;
  public ArrayList<Triple<String, Color, Long>> messages = new ArrayList<Triple<String, Color, Long>>();
  Label messagesLabel;
  
  long startTime;
  
  Gameplay gameplay;
  
  boolean showEnemyName;
  
  public Hud(AssetManager asset, float defWidth, float defHeight, BitmapFont font, final Player3D player, Gameplay gameplay)
  {
	this.defHeight = defHeight;
	this.defWidth = defWidth;
	this.gameplay = gameplay;
	float sx = 2.5f*defWidth/640, sy = 2.5f*defHeight/480;
	Label flyL = new Label("Fly", sx, sy, font, .5f); 
	Label exitL = new Label("Exit", sx, sy, font, .5f);
	Label fireL = new Label("Fire", sx, sy, font, .5f);
	Label spellL = new Label("100%", sx, sy, font, .5f); spellL.setColor(AssetConstants.SPELL_COLOR); 
	Label spL = new Label("", sx, sy, font, .5f); spL.setColor(AssetConstants.SP_COLOR);
	Label hpL = new Label("", sx, sy, font, .5f); hpL.setColor(AssetConstants.HP_COLOR);
	Label manaL = new Label("", sx, sy, font, .5f); manaL.setColor(AssetConstants.MANA_COLOR); 
	
	enemiesLabel = new Label("", 0, 0, AssetConstants.ENEMIES_LABEL_COLOR, sx/2.5f, sy/2.5f, Label.INFW, font, .5f);
	timeLabel = new Label("", 0, defHeight - defHeight/50, AssetConstants.TIME_LABEL_COLOR, sx, sy, Label.INFW, font, .5f);
	
	spellTextures[0] = asset.get(AssetConstants.DARK_GICON, Texture.class);
	spellTextures[1] = asset.get(AssetConstants.LIGHT_GICON, Texture.class);
	spellTextures[2] = asset.get(AssetConstants.FIRE_GICON, Texture.class);
	spellTextures[3] = asset.get(AssetConstants.ICE_GICON, Texture.class);
	spellTextures[4] = asset.get(AssetConstants.LIGHTNING_GICON, Texture.class);
	spellTextures[5] = asset.get(AssetConstants.CHAOS_GICON, Texture.class);
	
	startTime = -1;
	
	showEnemyName = false;
	
	if(Gdx.app.getType() == ApplicationType.Android)
	{
	  stick = new AnalogStick(0, 0, asset.get(AssetConstants.EXT_STICK, Texture.class), asset.get(AssetConstants.IN_STICK, Texture.class), defWidth/6, defWidth/6, defWidth/18, defWidth/18);
	  flyButton = new FontButton(asset.get(AssetConstants.DEF_BUTTON, Texture.class), asset.get(AssetConstants.DEF_BUTTON2, Texture.class), defWidth - defWidth/6, 0, defWidth/6, 
			                     defHeight/6, flyL, AssetConstants.DEF_BUTTON_COLOR, AssetConstants.DEF_BUTTON2_COLOR, false)
	  {
		@Override
		public void onPressed(int pointer)
		{
		  onFlyButtonPressed();	
		}
		
		@Override
		public void onReleased(int pointer)
		{
		  onFlyButtonReleased(); 	
		}
		
		@Override
		public void onDeselected(int p)
		{
		  onFlyButtonReleased();	
		}
	  };
	  
	  exmenuButton = new FontButton(asset.get(AssetConstants.DEF_BUTTON, Texture.class), asset.get(AssetConstants.DEF_BUTTON2, Texture.class), defWidth - defWidth/6, defHeight - defHeight/6, defWidth/6, 
              defHeight/6, exitL, AssetConstants.DEF_BUTTON_COLOR, AssetConstants.DEF_BUTTON2_COLOR, false)
      {
        @Override
        public void onReleased(int pointer) 
        {
          onExitButtonReleased();   	 
        }
      };
      
      fireButton = new FontButton(asset.get(AssetConstants.DEF_BUTTON, Texture.class), asset.get(AssetConstants.DEF_BUTTON2, Texture.class), defWidth - defWidth/6, defHeight/6, defWidth/6, 
              defHeight/6, fireL, AssetConstants.DEF_BUTTON_COLOR, AssetConstants.DEF_BUTTON2_COLOR, false)
      {
    	@Override
    	public void onPressed(int p)
    	{
    	  player.setFiring(true);	
    	}
    	
    	@Override
    	public void onDeselected(int p)
    	{
    	  player.setFiring(false);
    	}
    	
    	@Override
    	public void onReleased(int p)
    	{
          player.setFiring(false);		
    	}
      };
      
      leftI = new TextureButton(asset.get(AssetConstants.LARROW, Texture.class), asset.get(AssetConstants.LARROW2, Texture.class), 2*defWidth/5-defWidth/18, defWidth/18, defWidth/18, defWidth/18)
      {
    	@Override
    	public void onReleased(int p)
    	{
    	  player.decrementSpell();	
    	}
      };
      
      rightI = new TextureButton(asset.get(AssetConstants.RARROW, Texture.class), asset.get(AssetConstants.RARROW2, Texture.class), 2*defWidth/5+defWidth/18+spellL.getWidth(), defWidth/18, defWidth/18, defWidth/18)
      {
    	@Override
    	public void onReleased(int p)
    	{
          player.incrementSpell();		
    	}
      };
	}
	
	hpIcon = new StatIcon(defWidth/5, 0, new Image(asset.get(AssetConstants.HP_ICON, Texture.class), defWidth/18, defWidth/18), hpL, player.getHp());
	manaIcon = new StatIcon(2*defWidth/5, 0, new Image(asset.get(AssetConstants.MANA_ICON, Texture.class), defWidth/18, defWidth/18), manaL, player.getMana());
	spIcon = new StatIcon(3*defWidth/5, 0, new Image(asset.get(AssetConstants.SP_ICON, Texture.class), defWidth/18, defWidth/18), spL, (int)Player3D.MAX_SP);
	spellIcon = new StatIcon(2*defWidth/5, defWidth/18, new Image(asset.get(AssetConstants.SP_ICON, Texture.class), defWidth/18, defWidth/18), spellL, 100);
	spellIcon.setText("100%");
	scoreboardLabel = new Label("", 0, 0, AssetConstants.SCOREBOARD_LABEL_COLOR, sx, sy, Label.INFW, font, .3f);
	messagesLabel = new Label("", defWidth/3.6f, defHeight-1, AssetConstants.MESSAGES_COLOR_NEUTRAL, 2.5f*defWidth/640, 2.5f*defHeight/480, Label.INFW, font, .3f);
	messages.clear();
  }
  
  public void setStartTime(long st)
  {
	startTime = st;  
  }
  
  public void update(Player3D player, ConcurrentHashMap<Integer, EnemyPlayer3D> enemies, Boss boss, PerspectiveCamera camera)
  {
	if(startTime != -1)
	 timeLabel.setText(Util.getMinutesFormatTime( (int)((System.currentTimeMillis() - startTime)/1000 )));
	spIcon.setValue((int)player.getSp());  
	hpIcon.setValue(player.getHp());
	manaIcon.setValue(player.getMana());
	spellIcon.setText( (int)Math.floor((Math.min( (float)(System.currentTimeMillis() - player.getLastShoot())/player.getTimeNeeded(), 1) * 100))+"%" );
	spellIcon.setTexture(spellTextures[player.getActualSpell()]);
	
	if(messages.size() > 0)
	{
      messagesLabel.setText(messages.get(0).a); 
      messagesLabel.setColor(messages.get(0).b);
	  if( (System.currentTimeMillis() - messages.get(0).c) / 1000 >= MSG_DURATION ) 
	  {
		messages.remove(0);
		if(messages.size() > 0)
		 messages.get(0).c = System.currentTimeMillis();
	  }
	}
	
	if(Gdx.input.isKeyPressed(Keys.TAB))
	{
	  scoreboard.clear();
      for(Entry<Integer, EnemyPlayer3D> e : enemies.entrySet())
       scoreboard.add(new Triple<String, Integer, Integer>(e.getValue().getName(), e.getValue().getNumKilled(), e.getValue().getNumDeath()));
      scoreboard.add(new Triple<String, Integer, Integer>(player.getName(), player.getNumKilled(), player.getNumDeath()));
      Collections.sort(scoreboard, new Comparator<Triple<String, Integer, Integer>>()
      {
		 @Override
		 public int compare(Triple<String, Integer, Integer> arg0, Triple<String, Integer, Integer> arg1) 
		 {
			if(arg1.b == arg0.b)
			 return arg0.c - arg1.c;
			return arg1.b - arg0.b;
		 } 
      });
	}
  }
  
  public void render(Player3D player, ConcurrentHashMap<Integer, EnemyPlayer3D> enemies, Boss boss, PerspectiveCamera camera, SpriteBatch batch)
  {
	if(Gdx.app.getType() == ApplicationType.Android)
	{
	  stick.render(batch);  
	  flyButton.draw(batch);
	  fireButton.draw(batch);
	  exmenuButton.draw(batch);
	  leftI.draw(batch);
	  rightI.draw(batch);
	}
	timeLabel.draw(batch);
    spIcon.draw(batch);
    hpIcon.draw(batch);
    manaIcon.draw(batch);
    spellIcon.draw(batch);
    if(showEnemyName) renderEnemiesName(batch, camera, player, boss, enemies);
   
    if(messages.size() > 0)
     messagesLabel.draw(batch);
    
    if(Gdx.input.isKeyPressed(Keys.TAB))
    {
      float h = defHeight - defHeight / 12;
      int ps = -1, pd = -1;
      int p = 0;
      scoreboardLabel.setX(defWidth/3.6f);
      for(Triple<String, Integer, Integer> t : scoreboard)
      {
    	if(t.b != ps || t.c != pd) p++;  
    	ps = t.b; pd = t.c;
    	scoreboardLabel.setY(h);
    	scoreboardLabel.setText(p+" - "+t.a+" Kills:"+t.b+" Deaths:"+t.c);
    	if(t.a == player.getName()) scoreboardLabel.setColor(AssetConstants.MY_SCOREBOARD_LABEL_COLOR); else scoreboardLabel.setColor(AssetConstants.SCOREBOARD_LABEL_COLOR);
    	scoreboardLabel.draw(batch);
    	h -= defHeight / 12;  
      }
    }
  }
  
  public void touchDown(int screenX, int screenY, int pointer)
  {
    if(Gdx.app.getType() == ApplicationType.Android)
    {
      stick.touchDown(screenX, screenY, pointer);	
      flyButton.touchDown(screenX, screenY, pointer);
      exmenuButton.touchDown(screenX, screenY, pointer);
      fireButton.touchDown(screenX, screenY, pointer);
      leftI.touchDown(screenX, screenY, pointer);
      rightI.touchDown(screenX, screenY, pointer);
    }
  }
  
  public void touchUp(int screenX, int screenY, int pointer)
  {
	if(Gdx.app.getType() == ApplicationType.Android)
	{
	  stick.touchUp(screenX, screenY, pointer);	
	  flyButton.touchUp(screenX, screenY, pointer);
	  exmenuButton.touchUp(screenX, screenY, pointer);
	  fireButton.touchUp(screenX, screenY, pointer);
	  leftI.touchUp(screenX, screenY, pointer);
	  rightI.touchUp(screenX, screenY, pointer);
	}
  }
  
  public void touchDrag(int screenX, int screenY, int pointer)
  {
	if(Gdx.app.getType() == ApplicationType.Android)
	{
	  stick.touchDrag(screenX, screenY, pointer);	
	  flyButton.touchDrag(screenX, screenY, pointer);
	  exmenuButton.touchDrag(screenX, screenY, pointer);
	  fireButton.touchDrag(screenX, screenY, pointer);
	  leftI.touchDrag(screenX, screenY, pointer);
	  rightI.touchDrag(screenX, screenY, pointer);
	}  
  }
  
  public void addItemMessage(int itemId, int playerId)
  {
	int v = gameplay.getMap().getItems().get(itemId).getValue(); 
	String type = gameplay.getMap().getItems().get(itemId).getType().toString().toLowerCase();
	messages.add(new Triple<String, Color, Long>(gameplay.getPlayer(playerId).getName()+" gained "+v+" "+type, gameplay.getGame().getClient().getId() == playerId ? 
			                                     AssetConstants.MESSAGES_COLOR_GOOD : AssetConstants.MESSAGES_COLOR_NEUTRAL, System.currentTimeMillis()));	  
  }
  
  private void renderEnemiesName(SpriteBatch batch, PerspectiveCamera camera, Player3D player, Boss boss, ConcurrentHashMap<Integer, EnemyPlayer3D> enemies)
  {
	 renderEnemyName(batch, camera, boss, player, "Boss HP:"+boss.getHp());
	 
	 for(Entry<Integer, EnemyPlayer3D> e : enemies.entrySet())
	  renderEnemyName(batch, camera, e.getValue(), player, e.getValue().getName()+" HP:"+e.getValue().getHp());	 
  }
  
  private void renderEnemyName(SpriteBatch batch, PerspectiveCamera camera, Entity e, Player3D player, String name)
  {
	float offy = Math.max(AssetConstants.OFFSET_Y_ENLAB - 0.0003f * (player.dst2(e) - AssetConstants.BASE_DIST), AssetConstants.OFFSET_Y_ENLAB);
	Vector3 tmp = camera.project(e.getTranslation());
	enemiesLabel.setX((tmp.x + AssetConstants.OFFSET_X_ENLAB) * defWidth / Gdx.graphics.getWidth());
    enemiesLabel.setY((tmp.y + offy) * defHeight / Gdx.graphics.getHeight());
	if(tmp.z < 1)
	{
	  enemiesLabel.setText(name);
	  enemiesLabel.draw(batch);
	}	
  }
  
  public void setShowingEnName(boolean b)
  {
	showEnemyName = b;  
  }
  
  public boolean isShowingEnName()
  {
	return showEnemyName;  
  }
  
  public int getStickPointer()
  {
	return stick.getPointer();  
  }
  
  public int getStickAngle()
  {
	return stick.getAngle();  
  }
  
  public void onFlyButtonPressed()
  {
  }
  
  public void onFlyButtonReleased()
  {
  }
  
  public void onExitButtonReleased()
  {
  }
  
  public void dispose()
  {
	if(Gdx.app.getType() == ApplicationType.Android)
	{
	  stick.dispose();
	  flyButton.dispose();
	  exmenuButton.dispose();
	  fireButton.dispose();
	  leftI.dispose();
	  rightI.dispose();
	}
	timeLabel.dispose();
	scoreboardLabel.dispose();
	spIcon.dispose();
	hpIcon.dispose();
	manaIcon.dispose();
	spellIcon.dispose();
	for(int i=0; i<6; i++) spellTextures[i].dispose();
  }
}
