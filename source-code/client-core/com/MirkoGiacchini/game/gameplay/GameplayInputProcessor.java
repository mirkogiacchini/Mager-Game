package com.MirkoGiacchini.game.gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Vector2;

/**
 * input processor del gameplay
 * @author Mirko
 *
 */
public class GameplayInputProcessor implements InputProcessor
{
  Gameplay gameplay;
  int invSensibility, invSensibilityAndroid;
  
  private Vector2 keyIncrement; //usato solo su Desktop... sono gli incrementi dati dai tasti A/D (asse x) e W/S (asse y)
  
  /**
   * @param g gameplay che usa l'input processor
   * @param sens inverso sensibilità mouse, più è piccola più è sensibile
   * @param sens2 inverso sensibilità android, più è piccola più è sensibile
   */
  public GameplayInputProcessor(Gameplay g, int sens, int sens2)
  {
	gameplay = g;  
	if(Gdx.app.getType() == ApplicationType.Desktop)
     keyIncrement = new Vector2(0, 0);
	invSensibility = sens;
	invSensibilityAndroid = sens2;
  }
 
  @Override
  public boolean keyDown(int keycode) //solo su desktop
  {
	if(Gdx.app.getType() == ApplicationType.Desktop) //evita tasti di android... come aumento volume
	{	
	  incrementKeys(keycode, 1);  //aggiorni tasti premuto
	  gameplay.player.setAngleOffset(computeAngleOffsetDesktop()); //aggiorno offset angolo
	}
	
	switch(gameplay.getState())
	{
	  case PLAYING:
	   if(Gdx.app.getType() == ApplicationType.Desktop)
	   {
	     gameplay.player.recomputeDirectionXZ(); //ricalcolo direzione solo se sto giocando
		 if(keycode == Keys.SPACE)
	      gameplay.player.startFlying();
		 
	     if(keycode == Keys.ESCAPE) //esc per andare in ExPauseMenu
	     {
		   gameplay.setState(GameplayState.EX_PAUSE_MENU);
		   gameplay.getPlayer().setDirection(0, 0, 0);
	     }
	   }
	  break;

	  case EX_PAUSE_MENU:
	   if(keycode == Keys.ESCAPE) //ripremere esc per tornare in gioco
		gameplay.setState(GameplayState.PLAYING);
	  break;
	}
	return false;
  }

  @Override
  public boolean keyUp(int keycode) //solo su desktop
  {
	if(Gdx.app.getType() == ApplicationType.Desktop)
	{
	  incrementKeys(keycode, -1); //controllo tasti premuti
	  gameplay.player.setAngleOffset(computeAngleOffsetDesktop()); //ricalcolo offset basandomi sui tasti	
	  if(keycode == Keys.SHIFT_LEFT || keycode == Keys.SHIFT_RIGHT) gameplay.hud.setShowingEnName(!gameplay.hud.isShowingEnName());
	}
	
	switch(gameplay.getState())
	{
	  case PLAYING:
	   if(Gdx.app.getType() == ApplicationType.Desktop)
	   {
 	     gameplay.player.recomputeDirectionXZ(); //ricalcolo direzione
	
	     if(keycode == Keys.SPACE)
	      gameplay.player.stopFlying();
	   }
	  break;
	  
	  case EX_PAUSE_MENU:
	  break;
	}
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
	switch(gameplay.getState())
	{
	  case PLAYING:
		  
	    if(Gdx.app.getType() == ApplicationType.Android)
	    {
		  //ricalcolo posizione coordinate sullo schermo di default
		  screenX *= gameplay.defWidth / Gdx.graphics.getWidth();
		  screenY *= gameplay.defHeight / Gdx.graphics.getHeight();
		  screenY = (int) Math.abs(screenY - gameplay.defHeight);
		  gameplay.hud.touchDown(screenX, screenY, pointer);
	      gameplay.player.setAngleOffset(gameplay.hud.getStickAngle() == Integer.MAX_VALUE ? Integer.MAX_VALUE : gameplay.hud.getStickAngle() - 90); //calcolo angolo offset in base ad analogico
	      gameplay.player.recomputeDirectionXZ(); //ricalcolo direzione
	    }
	    
	    if(Gdx.app.getType() == ApplicationType.Desktop)
	    {
	      gameplay.player.setFiring(true);
	    }
	  break;
	  
	  case EX_PAUSE_MENU:
	   if(gameplay.getExPauseMenu() != null)	  
	    gameplay.getExPauseMenu().touchDown(screenX, screenY, pointer);
	  break;
	}
	return false;
  }

  @Override
  public boolean touchUp(int screenX, int screenY, int pointer, int button) 
  {
	switch(gameplay.getState())
	{
	  case PLAYING:
	    if(Gdx.app.getType() == ApplicationType.Android)
	    {
		  screenX *= gameplay.defWidth / Gdx.graphics.getWidth();
		  screenY *= gameplay.defHeight / Gdx.graphics.getHeight();
		  screenY = (int) Math.abs(screenY - gameplay.defHeight);
		  gameplay.hud.touchUp(screenX, screenY, pointer);
	      gameplay.player.setAngleOffset(gameplay.hud.getStickAngle() == Integer.MAX_VALUE ? Integer.MAX_VALUE : gameplay.hud.getStickAngle() - 90);
	      gameplay.player.recomputeDirectionXZ();
	    }
	    
	    if(Gdx.app.getType() == ApplicationType.Desktop)
	     gameplay.player.setFiring(false);
	  break;
	  
	  case EX_PAUSE_MENU:
	   if(gameplay.getExPauseMenu() != null)	  
	    gameplay.getExPauseMenu().touchUp(screenX, screenY, pointer);
	  break;
	}
	return false;
  }

  @Override
  public boolean touchDragged(int screenX, int screenY, int pointer) 
  {
	switch(gameplay.getState())
	{
	  case PLAYING:
	
	    if(Gdx.app.getType() == ApplicationType.Android)
	    {
		  screenX *= gameplay.defWidth / Gdx.graphics.getWidth();
		  screenY *= gameplay.defHeight / Gdx.graphics.getHeight();
		  screenY = (int) Math.abs(screenY - gameplay.defHeight);
		  gameplay.hud.touchDrag(screenX, screenY, pointer); 	
	      gameplay.player.setAngleOffset(gameplay.hud.getStickAngle() == Integer.MAX_VALUE ? Integer.MAX_VALUE : gameplay.hud.getStickAngle() - 90);
	      if(pointer != gameplay.hud.getStickPointer()) //rotazione personaggio
	      {
	    	gameplay.player.rotateY(- (float)Gdx.input.getDeltaX(pointer) / invSensibilityAndroid);
		    gameplay.player.rotateXZ(- (float)Gdx.input.getDeltaY(pointer) / invSensibilityAndroid);
	      }
	      gameplay.player.recomputeDirectionXZ(); //ricalcolo direzione
	    }
	
	    if(Gdx.app.getType() == ApplicationType.Desktop)
	    {
	      playerRotationDesktop(screenX, screenY);
	    }
	  break;
	  
	  case EX_PAUSE_MENU:
	   if(gameplay.getExPauseMenu() != null)	  
	    gameplay.getExPauseMenu().touchDrag(screenX, screenY, pointer);
	  break;
	}
	return false;
  }

  @Override
  public boolean mouseMoved(int screenX, int screenY) //solo su desktop
  {
	switch(gameplay.getState())
	{
	  case PLAYING:
	    playerRotationDesktop(screenX, screenY);
	  break;
	  
	  case EX_PAUSE_MENU:
	  break;
	}
	return false;
  }

  @Override
  public boolean scrolled(int amount) 
  {
	if(amount == 1)
	 gameplay.player.incrementSpell();
	else
	 gameplay.player.decrementSpell();
	return false;
  }
  
  //pressed = 1 nel keyPressed, -1 nel released
  private void incrementKeys(int keycode, int pressed)
  {
    if(keycode == Keys.W) keyIncrement.y += pressed; 
	if(keycode == Keys.S) keyIncrement.y -= pressed;
	if(keycode == Keys.D) keyIncrement.x += pressed;
	if(keycode == Keys.A) keyIncrement.x -= pressed;  
	if( (keyIncrement.x < 0 && !Gdx.input.isKeyPressed(Keys.A)) || (keyIncrement.x > 0 && !Gdx.input.isKeyPressed(Keys.D) )) keyIncrement.x = 0; 
	if( (keyIncrement.y < 0 && !Gdx.input.isKeyPressed(Keys.S)) || (keyIncrement.y > 0 && !Gdx.input.isKeyPressed(Keys.W) )) keyIncrement.y = 0; 
  }
  
  //calcola offset angolo sul desktop (in base al vettore keyIncrement)
  private int computeAngleOffsetDesktop()
  {
	int angleOffset = Integer.MAX_VALUE; //y = 0, x = 0
	if(keyIncrement.y == 1 && keyIncrement.x == 0) angleOffset = 0; 
	if(keyIncrement.y == 1 && keyIncrement.x == 1) angleOffset = 315;
 	if(keyIncrement.y == 1 && keyIncrement.x == -1) angleOffset = 45;
 	if(keyIncrement.y == -1 && keyIncrement.x == 0) angleOffset = 180;
 	if(keyIncrement.y == -1 && keyIncrement.x == 1) angleOffset = 225;
 	if(keyIncrement.y == -1 && keyIncrement.x == -1) angleOffset = 135;
 	if(keyIncrement.x == 1 && keyIncrement.y == 0) angleOffset = 270;
 	if(keyIncrement.x == -1 && keyIncrement.y == 0) angleOffset = 90;	  
 	return angleOffset;
  }
  
  //ruota il giocatore quando si muove il mouse (desktop)
  private void playerRotationDesktop(int screenX, int screenY)
  {
	float w = Gdx.graphics.getWidth();
	float h = Gdx.graphics.getHeight();
		  
	float deltaX = screenX - (w/2); //di quanto si è spostato il mouse?
	float deltaY = screenY - (h/2);
	
	float ry = - deltaX / invSensibility;
	float rxz = - deltaY / invSensibility;
	gameplay.getPlayer().setRotation(gameplay.player.getRotDegY() + ry, gameplay.player.getRotDegXZ() + rxz);
	//gameplay.player.rotateY(- deltaX / invSensibility); //calcolo di quanto devo ruotare in base alla sensibilità
    //gameplay.player.rotateXZ(- deltaY / invSensibility);
		  
	Gdx.input.setCursorPosition((int)w/2, (int)h/2);  
	gameplay.player.recomputeDirectionXZ();
  }
}
