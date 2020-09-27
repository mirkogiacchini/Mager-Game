package com.MirkoGiacchini.game.music;

import com.MirkoGiacchini.game.AssetConstants;
import com.MirkoGiacchini.game.GameState;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

/**
 * gestore della musica
 * @author Mirko
 *
 */
public class MusicManager 
{
   boolean music, sfx;
   Music background1, background2;
   Sound fire, hit, death, bossAttack;
   GameState actualState;
   
   public MusicManager(boolean m, boolean s)
   {
	 music = m;
	 sfx = s;
   }
   
   public boolean getMusic()
   {
	 return music;  
   }
   
   public boolean getSfx()
   {
	 return sfx;  
   }
   
   /**
    * da chiamare quando la musica è stata caricata
    * @param asset asset manager con musica caricata
    */
   public void init(AssetManager asset)
   {
	 background1 = asset.get(AssetConstants.MUSIC1);
	 background2 = asset.get(AssetConstants.MUSIC2);
	 fire = asset.get(AssetConstants.FIRESFX);
	 hit = asset.get(AssetConstants.HITSFX);
	 death = asset.get(AssetConstants.DEATHSFX);
	 bossAttack = asset.get(AssetConstants.BOSSATKSFX);
	 background1.setLooping(true);
	 background1.setVolume(0.5f);
	 background2.setLooping(true);
	 background2.setVolume(0.5f);
   }
   
   public void setMusic(boolean b)
   {
	 music = b;  
	 stateChanged(actualState);
   }
   
   public void setSfx(boolean b)
   {
	 sfx = b; 
   }
   
   public void stateChanged(GameState actual)
   {
	 switch(actual)
	 {
	   case LOGIN_MENU:
	   case LOADING_MENU:
	   case MAIN_MENU:
	   case END_GAME:
	   case ERROR_STATE:
	   case OPTIONS:
		if(background2 != null)   
	     background2.pause();
		if(background1 != null)
		 if(music)
		  background1.play();
		 else
		  background1.pause();
	   break;
		
	   case GAMEPLAY:
		if(background1 != null)   
		 background1.pause();   
		if(background2 != null)
		 if(music)
		  background2.play();
		 else
		  background2.pause();
	   break;
	 }
	 actualState = actual;
   }
   
   public void playFire()
   {
	 if(sfx)  
	  fire.play(1);  
   }
   
   public void playBossAttack()
   {
	 if(sfx)
	  bossAttack.play(1);
   }
   
   public void playHit()
   {
	 if(sfx)  
	  hit.play(1);  
   } 
   
   public void playDeath()
   {
	 if(sfx)  
	  death.play(1);  
   }
   
   public void dispose()
   {
	 if(background1 != null) background1.dispose(); 
	 if(background2 != null) background2.dispose();
	 if(death != null) death.dispose();
	 if(hit != null) hit.dispose();
	 if(fire != null) fire.dispose();
	 if(bossAttack != null) bossAttack.dispose();
   }
}
