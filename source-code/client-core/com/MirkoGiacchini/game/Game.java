package com.MirkoGiacchini.game;

import com.MirkoGiacchini.game.gameplay.Gameplay;
import com.MirkoGiacchini.game.music.MusicManager;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect;
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffectLoader;
import com.badlogic.gdx.graphics.g3d.particles.ParticleSystem;
import com.badlogic.gdx.graphics.g3d.particles.batches.PointSpriteParticleBatch;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.ScreenUtils;

import it.MirkoGiacchini.GameSocket.Client.Client;
import it.MirkoGiacchini.GameSocket.Client.GameClient;
import it.MirkoGiacchini.GameSocket.Server.Server;
import it.MirkoGiacchini.util.Util;
import it.MirkoGiacchini.world3d.heightmap.Heightmap;
import it.MirkoGiacchini.world3d.heightmap.HeightmapLoader;
import it.MirkoGiacchini.world3d.heightmap.HeightmapLoader.HeightmapParameter;

/**
 * classe principale del videogioco
 * @author Mirko
 *
 */
public class Game extends ApplicationAdapter
{ 
  /** porta */
  public static final int PORT = 2453;
  /** nome gioco*/
  private static final String GAME_NAME = "Mager";
  /** nome preferenze di gioco */
  public static final String GAME_PREFS = GAME_NAME+"prefs";
  /** sito del gioco*/
  public static final String GAME_WEBSITE = GAME_NAME+"/index.php";
  /**tag nell'xml delle preferenze per gli screenshot*/
  public static final String SCREENSHOT_FOLDER_PREF = "screenshot_folder";
  /** standard ip */
  public static final String DEF_IP = "127.0.0.1"; //game-server-ip
  /** standard port per sito ecc*/
  public static final int DEF_PORT = 8080;
  
  /**sistema per gestire effetti particellari*/
  ParticleSystem pSys;
  
  /** gestore dei menu*/	
  MenuController menuController;
  /**gameplay*/
  Gameplay gameplay;
  /** asset manager del gioco*/
  AssetManager asset;
  /** stato del gioco*/
  GameState state;
  /** client di gioco*/
  Client client;
  /**preferenze di gioco*/
  Preferences prefs;
  /** ip del server */
  String ip;
  /**gestore della musica*/
  MusicManager musicManager;
  /**porta sulla quale � aperto il sito del server*/
  int port;
  
  @Override
  public void create()
  {
	Bullet.init();
	createParticleSystem();
	state = GameState.LOADING_MENU;
	
	prefs = Gdx.app.getPreferences(GAME_PREFS);
	if(!prefs.contains("ip"))
	 prefs.putString("ip", DEF_IP);
	
	if(!prefs.contains("port"))
	 prefs.putInteger("port", 8080);
	
	if(!prefs.contains("music"))
	 prefs.putBoolean("music", true);
	
	if(!prefs.contains("sfx"))
	 prefs.putBoolean("sfx", true);
	
	if(!prefs.contains(SCREENSHOT_FOLDER_PREF))
	 prefs.putString(SCREENSHOT_FOLDER_PREF, Gdx.files.getExternalStoragePath()+GAME_NAME+"Screenshots");
	
	prefs.flush();
	
	ip = prefs.getString("ip");
	boolean music = prefs.getBoolean("music");
	boolean sfx = prefs.getBoolean("sfx");
	port = prefs.getInteger("port");
	
	initAsset();
	createClient();
	
	musicManager = new MusicManager(music, sfx);
	menuController = new MenuController(this);
	gameplay = new Gameplay(this, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	
	if(Gdx.app.getType() == ApplicationType.Desktop)
	{
  	  Pixmap pm = new Pixmap(Util.getHandle("cursor.png"));
	  Gdx.graphics.setCursor(Gdx.graphics.newCursor(pm, 0, 0));
	  pm.dispose();
	}
	
	Gdx.input.setCatchBackKey(true);
  }
  
  @Override
  public void render()
  {
	switch(state)
	{
	  case ERROR_STATE:	
	  case MAIN_MENU: 
	  case LOGIN_MENU:
	  case LOADING_MENU:
	  case END_GAME:
	  case OPTIONS:
	   menuController.render();
	  break;
      
	  case GAMEPLAY:
	   gameplay.update();
	   gameplay.render();
	  break;
	}
	
	takeScreenshot();
  }
  
  public AssetManager getAsset()
  {
	return asset;  
  }
  
  public GameState getState()
  {
	return state;  
  }
  
  public Client getClient()
  {
	return client;  
  }
  
  public Gameplay getGameplay()
  {
	return gameplay;  
  }
  
  public MenuController getMenuController()
  {
	return menuController;  
  }
  
  public int getPort()
  {
	return port;  
  }
  
  public void setState(GameState st)
  {
	GameState old = state;
	state = st;  
	musicManager.stateChanged(state);
	menuController.stateChanged(old, state);
  }
  
  private void createParticleSystem()
  {
	pSys = ParticleSystem.get();  
	
	PointSpriteParticleBatch pointSpriteBatch = new PointSpriteParticleBatch();
	OrthographicCamera cam = new OrthographicCamera();
    cam.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	pointSpriteBatch.setCamera(cam);
	pSys.getBatches().clear();
	pSys.add(pointSpriteBatch);
  }
  
  private void createClient() 
  {
	client = new GameClient(PORT, ip)
	{
	  @Override
	  public void onObjectTcpReceived(Object obj) //ricevuto messaggio tcp non standard
	  {  
		if(getState() == GameState.GAMEPLAY)
		 gameplay.tcpReceived(obj);
	  }
		  
	  @Override
	  protected void onDataUdpReceived(byte data[]) //ricevuto messaggio udp non standard
	  {
		if(getState() == GameState.GAMEPLAY)
		 gameplay.udpReceived(data);
	  }
	  
	  @Override
	  public void onDbConnection() //connessione avvenuta con il db
	  {  
		menuController.onDbConnection();
      }
		  
	  @Override
	  public void onDbConnectionFailed() //errore nella connessione al db
	  {
	    menuController.onDbConnectionFailed();
	  }
		  
	  @Override
	  public void onRegistration() //registrazione a db
	  {
		menuController.onRegistration();
	  }
		  
      @Override 
	  public void onRegistrationFailed() //registrazione fallita
	  {
	    menuController.onRegistrationFailed();
	  }
		  
	  @Override
	  public void onAccountDeleted() //account eliminato
	  {
	    menuController.onAccountDeleted();
	  }
		  
	  @Override
	  public void onAccountDelFailed() //eliminazione account fallita
	  {
		menuController.onAccountDelFailed();
	  }
		
      @Override
	  public void onRoomChanged(String roomInfo) //stanza del client cambiata
	  {
    	if(state != GameState.LOADING_MENU && state != GameState.LOGIN_MENU)
    	{
    	  if(getRoom() == Server.DEFAULT_ROOM)
	       Game.this.setState(GameState.MAIN_MENU);
    	  else
    	  {
	        gameplay.begin(roomInfo.split(" ")[3], menuController.getPlayer()); //#cr room maxsize descr
	        Game.this.setState(GameState.GAMEPLAY);
	        menuController.onGameplayBegin();
    	  }
    	}
	  }
		  
      @Override
	  public void onRoomChangeNegated() //negato cambio di stanza
	  {
	    menuController.onRoomChangeNegated();	
	  }
	  
	  @Override
	  public void onRoomReceived(String roomInfo) //ricevute informazioni su una stanza nuova
	  {
		menuController.onRoomReceived(roomInfo);
	  }
		  
	  @Override
	  public void onRoomRemoved(String roomInfo) //rimossa una stanza
	  {
		menuController.onRoomRemoved(roomInfo);
	  }
		
	  @Override
	  public void onDisconnecting()
	  {
		try
		{
		  if(menuController.mainMenu.playerInited())  
		   ((GameClient)this).sendDbUpdate(menuController.mainMenu.getPlayerRecord()); //aggiorno i valori del db prima di chiudere il gioco  
		}catch(Exception e){}
	  }
	  
	  @Override
	  public void onAccountLogout()
	  {
	    ((GameClient)this).sendDbUpdate(menuController.mainMenu.getPlayerRecord()); 
	    menuController.mainMenu.resetPlayer();
	    menuController.loginMenu.showInformation("Someone connected to your account!");
	    setState(GameState.LOGIN_MENU);
	  }
	  
	  @Override
	  public void onDisconnected()
	  {
		setState(GameState.ERROR_STATE);
	  }
	};
  }
  
  public MusicManager getMusicManager()
  {
	return musicManager;  
  }
  
  private void initAsset()
  {
	asset = new AssetManager(); 

	asset.setLoader(Heightmap.class, new HeightmapLoader(new InternalFileHandleResolver()));
	
	ParticleEffectLoader.ParticleEffectLoadParameter loadParam = new ParticleEffectLoader.ParticleEffectLoadParameter(pSys.getBatches());
	
	asset.load(AssetConstants.MUSIC1, Music.class);
	asset.load(AssetConstants.MUSIC2, Music.class);
	asset.load(AssetConstants.DEATHSFX, Sound.class);
	asset.load(AssetConstants.HITSFX, Sound.class);
	asset.load(AssetConstants.FIRESFX, Sound.class);
	asset.load(AssetConstants.BOSSATKSFX, Sound.class);
	
	asset.load(AssetConstants.DEF_BUTTON, Texture.class);  
	asset.load(AssetConstants.DEF_BUTTON2, Texture.class);
	asset.load(AssetConstants.DEF_TEXTFIELD, Texture.class);
	asset.load(AssetConstants.DEF_TEXTFIELD2, Texture.class);
	asset.load(AssetConstants.LOGIN_MENU_BACKGROUND, Texture.class);
	asset.load(AssetConstants.LOGIN_MENU_BACKBOX, Texture.class);
	asset.load(AssetConstants.MAIN_MENU_INFOBACK, Texture.class);
	asset.load(AssetConstants.SHOP_BACKGROUND, Texture.class);
	asset.load(AssetConstants.SHOP_BOX, Texture.class);
	asset.load(AssetConstants.LARROW, Texture.class);
	asset.load(AssetConstants.LARROW2, Texture.class);
	asset.load(AssetConstants.RARROW, Texture.class);
	asset.load(AssetConstants.RARROW2, Texture.class);
	asset.load(AssetConstants.ROOM_BUTTON, Texture.class);
	asset.load(AssetConstants.ROOM_BUTTON2, Texture.class);
	asset.load(AssetConstants.DARK_ATK, Texture.class);
	asset.load(AssetConstants.DARK_DEF, Texture.class);
	asset.load(AssetConstants.LIGHT_ATK, Texture.class);
	asset.load(AssetConstants.LIGHT_DEF, Texture.class);
	asset.load(AssetConstants.CHAOS_ATK, Texture.class);
	asset.load(AssetConstants.CHAOS_DEF, Texture.class);
	asset.load(AssetConstants.LIGHTNING_ATK, Texture.class);
	asset.load(AssetConstants.LIGHTNING_DEF, Texture.class);
	asset.load(AssetConstants.FIRE_ATK, Texture.class);
	asset.load(AssetConstants.FIRE_DEF, Texture.class);
	asset.load(AssetConstants.ICE_ATK, Texture.class);
	asset.load(AssetConstants.ICE_DEF, Texture.class);
	asset.load(AssetConstants.HP_SH, Texture.class);
	asset.load(AssetConstants.CRIT_SH, Texture.class);
	asset.load(AssetConstants.PERF_SH, Texture.class);
	asset.load(AssetConstants.BOSS_SH, Texture.class);
	asset.load(AssetConstants.PLAYER_ARMS_TEXTURE, Texture.class);
	asset.load(AssetConstants.IN_STICK, Texture.class);
	asset.load(AssetConstants.EXT_STICK, Texture.class); 
	asset.load(AssetConstants.SP_ICON, Texture.class);
	asset.load(AssetConstants.HP_ICON, Texture.class);
	asset.load(AssetConstants.MANA_ICON, Texture.class);
	asset.load(AssetConstants.MANA_TEXT, Texture.class);
	asset.load(AssetConstants.HP_TEXT, Texture.class);
	asset.load(AssetConstants.DEATH_SCREEN, Texture.class);
	asset.load(AssetConstants.DARK_SPELL_T, Texture.class);
	asset.load(AssetConstants.LIGHT_SPELL_T, Texture.class);
	asset.load(AssetConstants.FIRE_SPELL_T, Texture.class);
	asset.load(AssetConstants.ICE_SPELL_T, Texture.class);
	asset.load(AssetConstants.LIGHTNING_SPELL_T, Texture.class);
	asset.load(AssetConstants.CHAOS_SPELL_T, Texture.class);
	asset.load(AssetConstants.MUSICBUTTON1, Texture.class);
	asset.load(AssetConstants.MUSICBUTTON2, Texture.class);
	asset.load(AssetConstants.SFXBUTTON1, Texture.class);
	asset.load(AssetConstants.SFXBUTTON2, Texture.class);
	asset.load(AssetConstants.OPTIONSBACK, Texture.class);
	asset.load(AssetConstants.HEIGHTMAP1TEXT, Texture.class);
	asset.load(AssetConstants.CHAOS_GICON, Texture.class);
	asset.load(AssetConstants.LIGHTNING_GICON, Texture.class);
	asset.load(AssetConstants.FIRE_GICON, Texture.class);
	asset.load(AssetConstants.ICE_GICON, Texture.class);
	asset.load(AssetConstants.LIGHT_GICON, Texture.class);
	asset.load(AssetConstants.DARK_GICON, Texture.class);
	asset.load(AssetConstants.MAIN_MENU_ROOMBACK, Texture.class);
	asset.load(AssetConstants.MAIN_MENU_BACKBOX, Texture.class);
	
	asset.load(AssetConstants.DUMMY, Model.class);
	asset.load(AssetConstants.MONEYBAG, Model.class);
	asset.load(AssetConstants.GOLD, Model.class);
	asset.load(AssetConstants.ROLLER, Model.class);
	asset.load(AssetConstants.BUSH, Model.class);
	asset.load(AssetConstants.BANNER, Model.class);
	asset.load(AssetConstants.TREE1_MODEL, Model.class);
	asset.load(AssetConstants.TOWER_MODEL, Model.class);
	asset.load(AssetConstants.BARREL, Model.class);
	asset.load(AssetConstants.CROSS1, Model.class);
	asset.load(AssetConstants.CROSS2, Model.class);
	asset.load(AssetConstants.CROSS3, Model.class);
	asset.load(AssetConstants.TOMBSTONE, Model.class);
	asset.load(AssetConstants.HOUSE1, Model.class);
	asset.load(AssetConstants.PLAYER_ARMS, Model.class);
	asset.load(AssetConstants.ENEMY_PLAYER, Model.class);
	asset.load(AssetConstants.POTION_ITEM, Model.class);
	asset.load(AssetConstants.SPELL, Model.class);
	asset.load(AssetConstants.BOSS1, Model.class);
	asset.load(AssetConstants.BOSS2, Model.class);
	asset.load(AssetConstants.SKYBOX1, Model.class);
	asset.load(AssetConstants.PUMPKIN, Model.class);
	asset.load(AssetConstants.TOWER2, Model.class);
	asset.load(AssetConstants.HOUSE2, Model.class);
	asset.load(AssetConstants.WELL, Model.class);
	asset.load(AssetConstants.MOUNTAIN1, Model.class);
	asset.load(AssetConstants.MOUNTAIN2, Model.class);
	asset.load(AssetConstants.CEMSTONE, Model.class);
	asset.load(AssetConstants.WALL, Model.class);
	
	asset.load(AssetConstants.HEIGHTMAP1, Heightmap.class, new HeightmapParameter(AssetConstants.HEIGHTMAP1TEXT, 10, 40));
	asset.load(AssetConstants.HEIGHTMAP2, Heightmap.class, new HeightmapParameter(AssetConstants.HEIGHTMAP2TEXT, 6, 40));
  
    asset.load(AssetConstants.ICE_PFX, ParticleEffect.class, loadParam);
    asset.load(AssetConstants.FIRE_PFX, ParticleEffect.class, loadParam);
    asset.load(AssetConstants.CHAOS_PFX, ParticleEffect.class, loadParam);
    asset.load(AssetConstants.LIGHTNING_PFX, ParticleEffect.class, loadParam);
    asset.load(AssetConstants.DARK_PFX, ParticleEffect.class, loadParam);
    asset.load(AssetConstants.LIGHT_PFX, ParticleEffect.class, loadParam);
  }
  
  public ParticleSystem getParticleSystem()
  {
	return pSys;  
  }
  
  private void takeScreenshot()
  {
	if(Gdx.input.isKeyJustPressed(Keys.F3))
	{
	  byte[] pixels = ScreenUtils.getFrameBufferPixels(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
	  Pixmap pixmap = new Pixmap(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), Pixmap.Format.RGBA8888);
	  BufferUtils.copy(pixels, 0, pixmap.getPixels(), pixels.length);
	  PixmapIO.writePNG(new FileHandle(prefs.getString(SCREENSHOT_FOLDER_PREF)+"/"+System.currentTimeMillis()+".png"), pixmap);
	  pixmap.dispose();	
	}
  }
  
  @Override
  public void dispose()
  {
	prefs.putBoolean("music", musicManager.getMusic()); 
	prefs.putBoolean("sfx", musicManager.getSfx()); prefs.flush();
	asset.clear();
	asset.dispose();
	menuController.dispose();  
	gameplay.dispose();
	if(client.isConnected())
	 client.disconnect();
	if(musicManager != null)
	 musicManager.dispose();
  }
}
