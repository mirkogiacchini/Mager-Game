package com.MirkoGiacchini.game.gameplay.gr2d;

import com.MirkoGiacchini.game.AssetConstants;
import com.MirkoGiacchini.game.Game;
import com.MirkoGiacchini.game.GameState;
import com.MirkoGiacchini.game.gameplay.GameplayState;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;

import it.MirkoGiacchini.GameSocket.Server.Server;
import it.MirkoGiacchini.menu.Label;
import it.MirkoGiacchini.menu.Menu;
import it.MirkoGiacchini.menu.basic.FontButton;

/**
 * menu per uscire dal gameplay e tornare alla lobby
 * @author Mirko
 *
 */
public class ExPauseMenu extends Menu
{
  public ExPauseMenu(float width, float height, AssetManager asset, final Game game) 
  {
    super(width, height);
    
    float sx = 2.5f * width / 640, sy = 2.5f * height / 480;
    Label label = new Label("You wanna exit?", 0, 0, AssetConstants.EX_PAUSE_MENU_LABEL_COLOR, sx, sy, width, AssetConstants.FONT_PATH, 0.5f);
    label.setX(width/2 - Math.min(width/2, label.getWidth()/2)); 
    label.setMaxWidth(width - label.getX());
    label.setY(height*3/4 - label.getHeight()/2);
    addLabel(label);
    addButton(new FontButton(asset.get(AssetConstants.DEF_BUTTON, Texture.class), asset.get(AssetConstants.DEF_BUTTON2, Texture.class), width/2 - width/4, height/2, width/4, height/6,
    		                 new Label("Yes", sx, sy, width, AssetConstants.FONT_PATH, .5f), AssetConstants.DEF_BUTTON_COLOR, AssetConstants.DEF_BUTTON2_COLOR, false)
    {
      @Override
      public void onReleased(int p)
      {
    	game.getGameplay().setState(GameplayState.PLAYING);  
    	game.setState(GameState.MAIN_MENU);
    	game.getClient().changeRoom(Server.DEFAULT_ROOM);
        Gdx.input.setCursorCatched(false);
      }
    });
    
    addButton(new FontButton(asset.get(AssetConstants.DEF_BUTTON, Texture.class), asset.get(AssetConstants.DEF_BUTTON2, Texture.class), width/2, height/2, width/4, height/6,
            new Label("No", sx, sy, width, AssetConstants.FONT_PATH, .5f), AssetConstants.DEF_BUTTON_COLOR, AssetConstants.DEF_BUTTON2_COLOR, false)
    {
      @Override
      public void onReleased(int p)
      {
        game.getGameplay().setState(GameplayState.PLAYING); 
      }
    });
  }
}
