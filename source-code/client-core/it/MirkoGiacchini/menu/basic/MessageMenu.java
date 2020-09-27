package it.MirkoGiacchini.menu.basic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import it.MirkoGiacchini.menu.InformationBox;
import it.MirkoGiacchini.menu.Menu;
import it.MirkoGiacchini.menu.MessageBox;

public class MessageMenu extends Menu implements InputProcessor
{    
  InformationBox infoBox;
  
  public MessageMenu(float width, float height, String msg, Texture background, Color msgcolor, Texture buttonT1, Texture buttonT2, Color buttonc1, Color buttonc2, float sizeX, float sizeY, String fontPath, float sx, float sy) 
  {
    super(width, height);
    add((infoBox = new InformationBox(background, width, height, 0, 0, msg, msgcolor, buttonT1, buttonT2, buttonc1, buttonc2, sizeX, sizeY, fontPath, sx, sy)
    {
      @Override
      public void onButtonReleased()
      {
    	MessageMenu.this.onButtonReleased();
      }
    }));
  }
  
  public void giveController()
  {
	Gdx.input.setInputProcessor(this);  
	infoBox.setVisible(true);
  }
  
  public void setText(String txt)
  {
	infoBox.setText(txt);  
  }
  
  @Override
  public void draw(SpriteBatch batch)
  {
	for(MessageBox mb : messagesBox)
	 mb.draw(batch);
  }
  
  public void setVisible(boolean b)
  {
	infoBox.setVisible(b);  
  }
  
  protected void onButtonReleased()
  {  
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
