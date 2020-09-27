package com.MirkoGiacchini.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;

import it.MirkoGiacchini.menu.Label;
import it.MirkoGiacchini.menu.basic.FontButton;
import it.MirkoGiacchini.menu.basic.TextureMenu;

/**
 * menu opzioni
 */
public class OptionsMenu extends TextureMenu implements InputProcessor
{
  ActiveButton musicButton, sfxButton;
  GameState prevState;
  
  public OptionsMenu(float width, float height, AssetManager asset, final Game game) 
  {
	super(asset.get(AssetConstants.OPTIONSBACK, Texture.class), width, height);
	musicButton = new ActiveButton(asset.get(AssetConstants.MUSICBUTTON1, Texture.class), asset.get(AssetConstants.MUSICBUTTON2, Texture.class), 
			                       width/2-width/6, height/2, width/6, width/6, game.musicManager.getMusic())
	{
	  @Override
	  public void onReleased(int p)
	  {
		super.onReleased(p);
		game.getMusicManager().setMusic(this.isActive());
	  }
	};
	
	sfxButton = new ActiveButton(asset.get(AssetConstants.SFXBUTTON1, Texture.class), asset.get(AssetConstants.SFXBUTTON2, Texture.class), 
                                 width/2, height/2, width/6, width/6, game.musicManager.getSfx())
	{
	  @Override
	  public void onReleased(int p)
	  {
		super.onReleased(p);
		game.getMusicManager().setSfx(this.isActive());
	  }
	};
	addButton(musicButton);
	addButton(sfxButton);
	prevState = GameState.LOGIN_MENU;
	float sx = AssetConstants.DEF_BUTTON_SX * width / 640;
	float sy = AssetConstants.DEF_BUTTON_SY * height / 480;
	addButton(new FontButton(asset.get(AssetConstants.DEF_BUTTON, Texture.class), asset.get(AssetConstants.DEF_BUTTON2, Texture.class), width/2-width/8, 
			                 0, width/4, height/5, new Label("Back", sx, sy, Label.INFW, AssetConstants.FONT_PATH), AssetConstants.DEF_BUTTON_COLOR, AssetConstants.DEF_BUTTON2_COLOR)
	{
	  @Override
	  public void onReleased(int p)
	  {
		game.setState(prevState);  
	  }
	});
  }
  
  public boolean getMusic()
  {
	return musicButton.isActive();  
  }
  
  public boolean getSfx()
  {
	return sfxButton.isActive();  
  }
  
  public void setPrevState(GameState gs)
  {
	prevState = gs;  
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
