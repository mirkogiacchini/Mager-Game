package com.MirkoGiacchini.game.gameplay.gr2d;

import com.MirkoGiacchini.game.AssetConstants;
import com.MirkoGiacchini.game.Game;
import com.MirkoGiacchini.game.GameState;
import com.MirkoGiacchini.game.abstracted.GamePlayer;
import com.MirkoGiacchini.game.gameplay.Gameplay;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Texture;

import it.MirkoGiacchini.game.abstracted.Player;
import it.MirkoGiacchini.menu.Label;
import it.MirkoGiacchini.menu.basic.FontButton;
import it.MirkoGiacchini.menu.basic.TextureMenu;
import it.MirkoGiacchini.util.Util;

public class EndGameMenu extends TextureMenu implements InputProcessor
{
  FontButton exitButton;
  boolean started;
  
  public EndGameMenu(float width, float height) 
  {
	super(null, width, height);
	started = false;
  }
  
  public void start(int numKills, int numDeaths, float timePlayed, final Game game)
  {  
	float sx = AssetConstants.DEF_BUTTON_SX * defWidth / 640;
	float sy = AssetConstants.DEF_BUTTON_SY * defHeight / 480;
	
	float rateo = (float)numKills / Math.max(numDeaths, 0.5f);
	int addExp = (int)Math.floor((timePlayed / Gameplay.GAME_DURATION * 100) + 30 * rateo);
	int addPen = (int)Math.floor(addExp * 0.8f);
	GamePlayer player = game.getMenuController().getPlayer();
	player.setExp(player.getExp() + addExp);
	while(player.getExp() >= player.getNeededExp() && player.getLv() < Player.MAX_LV)
	{
	  player.setExp(player.getExp() - player.getNeededExp());	
	  player.setLv(player.getLv() + 1);
	}
	player.setPen(player.getPen() + addPen);
	
	labels.clear();
	buttons.clear();
	
	addLabel(new Label("Kills: "+numKills, defWidth/2-defWidth/8, defHeight-defHeight/8, AssetConstants.END_GAME_COLOR, sx, sy, Label.INFW, AssetConstants.FONT_PATH, .5f));
	float h = labels.get(0).getHeight();
	addLabel(new Label("Deaths: "+numDeaths, defWidth/2-defWidth/8, defHeight-defHeight/8-h*1.2f, AssetConstants.END_GAME_COLOR, sx, sy, Label.INFW, AssetConstants.FONT_PATH, .5f));
	addLabel(new Label("Time played: "+Util.getMinutesFormatTime((int)Math.ceil(timePlayed)), defWidth/2-defWidth/8, defHeight-defHeight/8-h*1.2f*2, AssetConstants.END_GAME_COLOR, sx, sy, Label.INFW, AssetConstants.FONT_PATH, .5f));
	addLabel(new Label("Exp earned: "+addExp, defWidth/2-defWidth/8, defHeight-defHeight/8-h*1.2f*3, AssetConstants.END_GAME_COLOR2, sx, sy, Label.INFW, AssetConstants.FONT_PATH, .5f));
	addLabel(new Label("Pen earned: "+addPen, defWidth/2-defWidth/8, defHeight-defHeight/8-h*1.2f*4, AssetConstants.END_GAME_COLOR2, sx, sy, Label.INFW, AssetConstants.FONT_PATH, .5f));
	
	super.background = game.getAsset().get(AssetConstants.END_GAME_SCREEN, Texture.class);
	addButton(new FontButton(game.getAsset().get(AssetConstants.DEF_BUTTON, Texture.class), game.getAsset().get(AssetConstants.DEF_BUTTON2, Texture.class), defWidth/2 - defWidth/8, defHeight/10, 
			defWidth/4, defHeight/10, new Label("Main Menu", sx, sy, defWidth, AssetConstants.FONT_PATH, .5f), AssetConstants.DEF_BUTTON_COLOR, AssetConstants.DEF_BUTTON2_COLOR, false)
	{
	  @Override
	  public void onReleased(int p)
	  {
		game.setState(GameState.MAIN_MENU);
	  }
	});
	started = true;
  }
  
  public boolean isStarted()
  {
	return started;
  }
  
  public void reset()
  {
	started = false;  
  }
  
  public void giveController()
  {
	Gdx.input.setInputProcessor(this);  
  }

  @Override
  public boolean keyDown(int keycode) 
  {
	return false;
  }

  @Override
  public boolean keyUp(int keycode) 
  {
	return false;
  }

  @Override
  public boolean keyTyped(char character) 
  {
	return false;
  }

  @Override
  public boolean touchDown(int screenX, int screenY, int pointer, int button) 
  {
	super.touchDown(screenX, screenY, pointer);
	return false; 
  }

  @Override
  public boolean touchUp(int screenX, int screenY, int pointer, int button) 
  {
	super.touchUp(screenX, screenY, pointer);
	return false;  
  } 

  @Override
  public boolean touchDragged(int screenX, int screenY, int pointer) 
  {
	super.touchDrag(screenX, screenY, pointer);
	return false;
  }

  @Override
  public boolean mouseMoved(int screenX, int screenY) 
  {
	return false;
  }

  @Override
  public boolean scrolled(int amount) 
  {
	return false;
  }
}
