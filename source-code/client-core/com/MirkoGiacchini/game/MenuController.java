package com.MirkoGiacchini.game;

import com.MirkoGiacchini.game.abstracted.GamePlayer;
import com.MirkoGiacchini.game.gameplay.gr2d.EndGameMenu;
import com.MirkoGiacchini.game.shop.Shop;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import it.MirkoGiacchini.GameSocket.Client.DBClient;
import it.MirkoGiacchini.GameSocket.Client.GameClient;
import it.MirkoGiacchini.crypto.MD5;
import it.MirkoGiacchini.game.abstracted.Player;
import it.MirkoGiacchini.menu.Label;
import it.MirkoGiacchini.menu.basic.FontButton;
import it.MirkoGiacchini.menu.basic.MessageMenu;
import it.MirkoGiacchini.menu.basic.TextureMenu;
import it.MirkoGiacchini.menu.util.LoadingMenu;
import it.MirkoGiacchini.menu.util.LoginMenu;
import it.MirkoGiacchini.menu.util.game.MainMenu;

/**
 * classe che gestisce i menu
 * @author Mirko
 *
 */
public class MenuController 
{
  /** menu caricamento*/
  LoadingMenu loadingMenu;
  /**menu nei casi di errore*/
  MessageMenu errorMenu;
  LoginMenu loginMenu;
  MainMenu mainMenu;
  EndGameMenu egmenu;
  OptionsMenu options;
  
  Game game;
  SpriteBatch batch;
  OrthographicCamera camera;
  
  AssetManager asset;
  
  float defWidth, defHeight;
  
  public MenuController(Game game)
  {
	this.game = game;  
	asset = game.asset;
	createLoadingMenu();
	camera = new OrthographicCamera();
	camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()); 
	batch = new SpriteBatch();
  }
  
  public void render()
  {	
	update();
	draw();
  }
  
  private void update()
  {
	switch(game.getState())
	{
	  case LOADING_MENU:
	   if(loadingMenu.update()) //finito di caricare e connettere
	   {
		 if(loadingMenu.connectionStatus()) //connesso con successo?
		 {
		   createMenus(); 
		   game.musicManager.init(asset);
		   game.setState(GameState.LOGIN_MENU);
		 }
		 else
		  game.setState(GameState.ERROR_STATE); 
	   }
	  break;
	  
	  default:
	}
  }
  
  private void draw()
  {
    Gdx.gl.glClearColor(0, 0, 0, 1);
	Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	camera.update();
    batch.setProjectionMatrix(camera.combined);
    batch.begin();  
    
	switch(game.getState())
	{
	  case LOADING_MENU: loadingMenu.draw(batch); break;
	  case LOGIN_MENU: loginMenu.draw(batch); break;
	  case MAIN_MENU: mainMenu.draw(batch); break;
	  case ERROR_STATE: errorMenu.draw(batch); break;
	  case END_GAME: egmenu.draw(batch); break;
	  case OPTIONS: options.draw(batch); break;
	  default:
	}
	
	batch.end();
  }
  
  /** chiamato dal gioco quando lo stato cambia */
  public void stateChanged(GameState old, GameState news)
  {
	if(news != GameState.GAMEPLAY)
	 Gdx.input.setCursorCatched(false);
	switch(news)
	{
	  case ERROR_STATE:
	   if(game.getClient().getDisconnectionStrInfo().equals("Exited"))
		Gdx.app.exit();
	   errorMenu.setText(game.getClient().getDisconnectionStrInfo());
	   errorMenu.giveController();
      break;
      
	  case LOGIN_MENU: loginMenu.giveController(); break;
	  
	  case MAIN_MENU: 
	   mainMenu.clearRooms();	  
	   mainMenu.giveController(); 
	   //System.gc(); //forza attivazione garbage collector !!!SOLO DEBUGGING!!!
	   ((GameClient)game.getClient()).requestRooms();
	  break;	
	  
	  case END_GAME:
	   egmenu.giveController();
	  break;
	  
	  case OPTIONS:
	   options.giveController();
	  break;

	  default:
	}
  }
  
  public void createLoadingMenu()
  {
	defWidth = Gdx.graphics.getWidth(); defHeight = Gdx.graphics.getHeight();  
	egmenu = new EndGameMenu(defWidth, defHeight);
	loadingMenu = new LoadingMenu(new Label("Loading", defWidth/2, defHeight/2, AssetConstants.LOADING_MENU_COLOR, 3 * defWidth / 640, 3 * defHeight / 480, Label.INFW, AssetConstants.FONT_PATH), 
                                  new TextureMenu(new Texture(AssetConstants.LOADING_MENU_BACKGROUND), defWidth, defHeight), game.getAsset(), game.getClient());  
    loadingMenu.getLabel().setX(loadingMenu.getLabel().getX() - loadingMenu.getLabel().getWidth()/2); //metto la label centrata  
    
	errorMenu = new MessageMenu(defWidth, defHeight, "", new Texture(AssetConstants.ERROR_MENU_BACKGROUND), AssetConstants.ERROR_MENU_STRCOLOR, 
                                new Texture(AssetConstants.DEF_BUTTON), new Texture(AssetConstants.DEF_BUTTON2), AssetConstants.DEF_BUTTON_COLOR, 
                                AssetConstants.DEF_BUTTON2_COLOR, 3*defWidth/640, 3*defHeight/480, AssetConstants.FONT_PATH, AssetConstants.DEF_BUTTON_SX/3, AssetConstants.DEF_BUTTON_SY/3)
    {
	  @Override
	  public void setText(String text)
	  {
		super.setText(text+" ip:"+game.ip);   
	  }
	  
      @Override
      public void onButtonReleased()
      {
    	setVisible(true);
        Gdx.app.exit();  
      }
    };
  }
  
  public void createMenus()
  {	
	 loginMenu = new LoginMenu(asset.get(AssetConstants.LOGIN_MENU_BACKGROUND, Texture.class), asset.get(AssetConstants.DEF_BUTTON, Texture.class), asset.get(AssetConstants.DEF_BUTTON2, Texture.class),
			                   AssetConstants.DEF_BUTTON_COLOR, AssetConstants.DEF_BUTTON2_COLOR, asset.get(AssetConstants.DEF_TEXTFIELD, Texture.class), 
				               asset.get(AssetConstants.DEF_TEXTFIELD2, Texture.class), AssetConstants.DEF_TEXTFIELD_COLOR, asset.get(AssetConstants.DEF_BUTTON, Texture.class),
				               asset.get(AssetConstants.DEF_BUTTON2, Texture.class), AssetConstants.DEF_BUTTON_COLOR, AssetConstants.DEF_BUTTON2_COLOR, (DBClient)game.getClient(),
				               asset.get(AssetConstants.LOGIN_MENU_BACKBOX, Texture.class), AssetConstants.LOGIN_MENU_BACKBOX_COLOR, asset.get(AssetConstants.DEF_BUTTON, Texture.class), 
		                       asset.get(AssetConstants.DEF_BUTTON2, Texture.class), AssetConstants.DEF_BUTTON_COLOR, AssetConstants.DEF_BUTTON2_COLOR, AssetConstants.FONT_PATH,
	    	                   .5f, new MD5(), 3, 3, AssetConstants.DEF_BUTTON_SX, AssetConstants.DEF_BUTTON_SY, defWidth, defHeight)
	 {  //aggiungo tasti per sito web e opzioni
	   @Override
	   public void onCreating()
	   {
		 float h = menu.getButtons().get(0).getHeight();
		 
		 menu.addButton(new FontButton(asset.get(AssetConstants.DEF_BUTTON, Texture.class), asset.get(AssetConstants.DEF_BUTTON2, Texture.class), menu.getWidth()/2 - menu.getWidth()/4, 
				        menu.getButtons().get(0).getY() - h * 1.2f, menu.getWidth()/4, h, new Label("Website", AssetConstants.DEF_BUTTON_SX*menu.getWidth()/640.f, 
				        AssetConstants.DEF_BUTTON_SY*menu.getHeight()/480.f, menu.getWidth()/4, AssetConstants.FONT_PATH, 0.5f), AssetConstants.DEF_BUTTON_COLOR, AssetConstants.DEF_BUTTON2_COLOR, false)
		 {
		   @Override
		   public void onReleased(int p) //apri sito
		   {
			 Gdx.net.openURI("http://"+game.getClient().getIp()+":"+game.getPort()+"/"+Game.GAME_WEBSITE);  
		   }
		 });
		 
		 menu.addButton(new FontButton(asset.get(AssetConstants.DEF_BUTTON, Texture.class), asset.get(AssetConstants.DEF_BUTTON2, Texture.class), menu.getWidth()/2, 
				        menu.getButtons().get(0).getY() - h * 1.2f, menu.getWidth()/4, h, new Label("Options", AssetConstants.DEF_BUTTON_SX*menu.getWidth()/640.f, 
				        AssetConstants.DEF_BUTTON_SY*menu.getHeight()/480.f, menu.getWidth()/4, AssetConstants.FONT_PATH, 0.5f), AssetConstants.DEF_BUTTON_COLOR, AssetConstants.DEF_BUTTON2_COLOR, false)
		 {
		   @Override
		   public void onReleased(int p)
		   {
			 options.setPrevState(GameState.LOGIN_MENU);
			 game.setState(GameState.OPTIONS);
		   }
		 });
	   }
	 };

	 mainMenu = new MainMenu(asset.get(AssetConstants.DEF_BUTTON, Texture.class), asset.get(AssetConstants.DEF_BUTTON2, Texture.class), AssetConstants.DEF_BUTTON_COLOR, 
			                 AssetConstants.DEF_BUTTON2_COLOR, (DBClient)game.getClient(), asset.get(AssetConstants.MAIN_MENU_INFOBACK, Texture.class),
			                 asset.get(AssetConstants.MAIN_MENU_ROOMBACK, Texture.class), asset.get(AssetConstants.LARROW, Texture.class), asset.get(AssetConstants.LARROW2, Texture.class),
			                 asset.get(AssetConstants.RARROW, Texture.class), asset.get(AssetConstants.RARROW2, Texture.class), asset.get(AssetConstants.ROOM_BUTTON, Texture.class), 
			                 asset.get(AssetConstants.ROOM_BUTTON2, Texture.class), AssetConstants.ROOM_BUTTON_COLOR, AssetConstants.ROOM_BUTTON2_COLOR, AssetConstants.FONT_PATH, 
			                 asset.get(AssetConstants.MAIN_MENU_BACKBOX, Texture.class), AssetConstants.ROOMS_INFO, AssetConstants.ROOMS_SIZES, asset.get(AssetConstants.LARROW, Texture.class), 
			                 asset.get(AssetConstants.LARROW2, Texture.class), asset.get(AssetConstants.RARROW, Texture.class), asset.get(AssetConstants.RARROW2, Texture.class),
			                 AssetConstants.BACKBOX_COLOR, asset.get(AssetConstants.DEF_BUTTON, Texture.class), asset.get(AssetConstants.DEF_BUTTON2, Texture.class), AssetConstants.DEF_BUTTON_COLOR, 
			                 AssetConstants.DEF_BUTTON2_COLOR, AssetConstants.INFO_COLOR, 
			                 new Shop(defWidth, defHeight-defHeight/10, asset), AssetConstants.DEF_BUTTON_SX, AssetConstants.DEF_BUTTON_SY, defWidth, defHeight)
	 {
		@Override
		public void onCreated()
		{
		  lobby.addButton(new FontButton(asset.get(AssetConstants.DEF_BUTTON, Texture.class), asset.get(AssetConstants.DEF_BUTTON2, Texture.class), defWidth/9, 
			              defHeight/10, defWidth/4, defHeight/10, new Label("Options", AssetConstants.DEF_BUTTON_SX*defWidth/640.f, AssetConstants.DEF_BUTTON_SY*defHeight/480.f, 
			              defWidth/4, AssetConstants.FONT_PATH, 0.5f), AssetConstants.DEF_BUTTON_COLOR, AssetConstants.DEF_BUTTON2_COLOR, false)
		  {
			@Override
			public void onReleased(int p)
			{
			  options.setPrevState(GameState.MAIN_MENU);
			  game.setState(GameState.OPTIONS);
			}
		  });	 
		}
		
	    @Override
	    public Player playerFromRecord(String record)
	    {
	      GamePlayer player = new GamePlayer();	
	      String data[] = record.split(" ");
	  	  player.set(data[0], Integer.parseInt(data[3]), Integer.parseInt(data[4]), Integer.parseInt(data[5]));
	  	  
	  	  for(int i=6; i<data.length; i++) //tutti i dati seguenti sono gli items...
	  	   player.addItem(Integer.parseInt(data[i]));
	  	  
	      return player; 	
	    }
	    
	    @Override
	    public void recordReceived(String record)
	    {
	      Player p = playerFromRecord(record);
	      lobby.onPlayerReceived(p);
	      ((Shop)shop).setPlayer((GamePlayer)p);
	    }
	    
	    @Override
	    public void exitingFromMainMenu()
	    {
	      ((DBClient)game.getClient()).sendDbUpdate(mainMenu.getPlayerRecord());
	      ((DBClient)game.getClient()).askLogout();
	      mainMenu.resetPlayer();
	      game.setState(GameState.LOGIN_MENU);
	    }
	 };	  
	 
	 options = new OptionsMenu(defWidth, defHeight, asset, game);
  }
  
  protected void onDbConnection()
  {
	if(game.getState() == GameState.LOGIN_MENU)
	{
      loginMenu.onDbConnection();
	  game.setState(GameState.MAIN_MENU);
	  mainMenu.recordReceived(((DBClient)game.getClient()).getDbRecord());
	}
  }
  
  protected void onDbConnectionFailed()
  {
	if(game.getState() == GameState.LOGIN_MENU) 
	 loginMenu.onDbConnectionFailed();
  }
  
  protected void onRegistration()
  {
	if(game.getState() == GameState.LOGIN_MENU)   
	 loginMenu.onRegistration();
  }
  
  protected void onRegistrationFailed()
  {
	if(game.getState() == GameState.LOGIN_MENU)   
	 loginMenu.onRegistrationFailed();
  }
  
  protected void onAccountDeleted()
  {
	if(game.getState() == GameState.LOGIN_MENU)   
	 loginMenu.onAccountDeleted();	  
  }
  
  protected void onAccountDelFailed()
  {
    if(game.getState() == GameState.LOGIN_MENU)   
	 loginMenu.onAccountDelFailed();
  }
  
  protected void onRoomChangeNegated()
  {
	if(game.getState() == GameState.MAIN_MENU)
	 mainMenu.roomChangeNegated();
  }
  
  protected void onRoomReceived(String rinfo)
  {
	if(game.getState() == GameState.MAIN_MENU)
	 mainMenu.onRoomReceived(rinfo); 
  }
  
  protected void onRoomRemoved(String rinfo)
  {
    if(game.getState() == GameState.MAIN_MENU)
	 mainMenu.onRoomDeleted(rinfo);
  }
  
  protected void onGameplayBegin()
  {
	mainMenu.onGameplayBegin();  
  }
  
  public GamePlayer getPlayer()
  {
	return (GamePlayer)mainMenu.getPlayer();  
  }
  
  public EndGameMenu getEndGameMenu()
  {
	return egmenu;  
  }
  
  public void dispose()
  {  
	batch.dispose();
	errorMenu.dispose();
	loadingMenu.dispose();
	if(loginMenu != null)
	 loginMenu.dispose();
	if(mainMenu != null)
	 mainMenu.dispose();
	if(egmenu != null)
	 egmenu.dispose();
	if(options != null)
	 options.dispose();
  }
}
