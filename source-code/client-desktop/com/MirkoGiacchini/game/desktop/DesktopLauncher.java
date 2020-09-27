package com.MirkoGiacchini.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.MirkoGiacchini.game.Game;

public class DesktopLauncher 
{
  public static void main (String[] arg) 
  {
	LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
	//config.fullscreen = true;
	//config.width = 1400;
	//config.height = 800;
	config.width = 900;
	config.height = 550;
	config.title = "Mager";
	new LwjglApplication(new Game(), config);
  }
}
