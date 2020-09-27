package it.MirkoGiacchini.menu.util.game;

import it.MirkoGiacchini.GameSocket.Client.DBClient;
import it.MirkoGiacchini.game.abstracted.Player;
import it.MirkoGiacchini.menu.Label;
import it.MirkoGiacchini.menu.Menu;
import it.MirkoGiacchini.menu.basic.CollageMenu;
import it.MirkoGiacchini.menu.basic.FontButton;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/** menu principale del gioco */
public class MainMenu implements InputProcessor
{
  protected CollageMenu menu;
  protected LobbyMenu lobby; //menu lobby        
  protected Menu shop; //menu shop
  MainMenuState state; //stato del menu 
  
  FontButton lobbyButton, shopButton, exitButton; //tasti base del menu: per andare in lobby, al negozio o per uscire
  
  /**
   * @param btex1 texture per bottone selezionato
   * @param btex2 texture per bottone non selezionato
   * @param col1 colore bottone selezionato
   * @param col2 colore bottone non selezionato
   * @param client client di gioco
   * @param infoBack background delle informazioni del giocatore
   * @param roomsBack background per zona stanze
   * @param left1 texture per bottone sinistro selezionato
   * @param left2 texture per bottone sinistro non selezionato
   * @param right1 texture per bottone destro selezionato
   * @param right2 texture per bottone destro non selezionato
   * @param roomBack texture background singola stanza selezionata
   * @param roomBack2 texture background singola stanza non selezionata
   * @param roomC colore su roomBack selezionato
   * @param roomC2 colore su roomback non selezionato
   * @param fontPath percorso font
   * @param objcb background su messaggi
   * @param roomDescr descrizione delle varie mappe possibili   
   * @param left21 texture per bottone sinistro2 selezionato
   * @param left22 texture per bottone sinistro2 non selezionato
   * @param right21 texture per bottone destro2 selezionato
   * @param right22 texture per bottone destro2 non selezionato
   * @param c3 colore sui bottoni left21...right22
   * @param lobtex1 texture bottone generale lobby selezionato
   * @param lobtex2 texture bottone generale lobby non selezionato
   * @param lobcol1 colore bottone generale lobby selezionato
   * @param lobcol2 colore bottone generale lobby non selezionato
   * @param labColor colore label in lobby
   * @param shopMenu shop
   * @param sx font size x
   * @param sy font size y
   */
  public MainMenu(Texture btex1, Texture btex2, Color col1, Color col2, final DBClient client, Texture infoBack, Texture roomsBack, Texture left1, Texture left2, Texture right1, Texture right2, Texture roomBack,
		          Texture roomBack2, Color roomC, Color roomC2, String fontPath, Texture objcb, String roomDescr[], String roomSizes[], Texture left21, Texture left22, Texture right21, Texture right22, Color c3,
		          Texture lobtex1, Texture lobtex2, Color lobcol1, Color lobcol2, Color labColor, Menu shopMenu, float sx, float sy, float width, float height)
  {
	state = MainMenuState.LOBBY;  	
	float labelX = sx*width/640, labelY = sy*height/480;
	
	//--------------- barra superiore del menu ---------------------
	menu = new CollageMenu(width, height);
	
	//bottone che gestisce la lobby
	lobbyButton = new FontButton(btex1, btex2, 0, height - height/10, width/3, height/10, new Label("Lobby", labelX, labelY, 0, fontPath), col1, col2, true)
	{
      @Override
      public void onPressed(int pointer)
      {
    	state = MainMenuState.LOBBY;  
    	this.setPointer(pointer);
    	shopButton.setPointer(-1);
      }
	};     
	lobbyButton.setPointer(-2); //inizialemente in lobby
	
	shopButton = new FontButton(btex1, btex2, width/3, height - height/10, width/3, height/10, new Label("Shop", labelX, labelY, 0, fontPath), col1, col2, true)
	{
	  @Override
	  public void onPressed(int pointer)
	  {
		state = MainMenuState.SHOP;
		this.setPointer(pointer);
		lobbyButton.setPointer(-1);
	  }
	};
	
	exitButton = new FontButton(btex1, btex2, 2*width/3, height - height/10, width/3, height/10, new Label("Exit", labelX, labelY, 0, fontPath), col1, col2, false)
	{
	  @Override
	  public void onReleased(int pointer)
	  {
		exitingFromMainMenu();
	  }
	};
	
	menu.add(lobbyButton);
	menu.add(shopButton);
	menu.add(exitButton);
	
	//----------------------------------- menu lobby ----------------------------
	lobby = new LobbyMenu(width, height, infoBack, roomsBack, left1, left2, right1, right2, roomBack, roomBack2, roomC, roomC2, lobtex1, lobtex2, lobcol1, lobcol2, client, fontPath, objcb, roomDescr,
			              roomSizes, left21, left22, right21, right22, c3, labColor, sx, sy);
	shop = shopMenu;
	onCreated();
  }
  
  /** da il controllo dell'input al main menu*/
  public void giveController()
  {
	Gdx.input.setInputProcessor(this);  
  }
  
  /** metodo da chiamare quando si riceve il record nel client */
  public void recordReceived(String record)
  {
	lobby.onPlayerReceived(playerFromRecord(record));  
  }
  
  /** metodo da chiamare quando il client cambia stanza*/
  public void changedRoom()
  {  
    lobby.onClientRoomChanged();  
  }
  
  public void roomChangeNegated()
  {    
 	lobby.onClientChangeNegated();  
  }
  
  //restituisce un player astratto da un record del db
  protected Player playerFromRecord(String record)
  {
	Player p = new Player();
	String data[] = record.split(" ");
	p.set(data[0], Integer.parseInt(data[3]), Integer.parseInt(data[4]), Integer.parseInt(data[5]));
	return p;  
  }
  
  /** restituisce record del giocatore per la modifica del db*/
  public String getPlayerRecord()
  {
	return lobby.getPlayerRecord();  
  }
  
  public void resetPlayer()
  {
	lobby.resetPlayer();  
  }
  
  public boolean playerInited()
  {
	return lobby.player != null;  
  }
  
  public void draw(SpriteBatch batch)
  {
	menu.draw(batch);
	if(state == MainMenuState.LOBBY)
	 lobby.draw(batch);
	if(state == MainMenuState.SHOP)
	 shop.draw(batch);
  }
  
  @Override
  public boolean keyDown(int keycode) 
  {
	if(state == MainMenuState.LOBBY)
	 lobby.keyPressed(keycode);
	
	if(state == MainMenuState.SHOP)
	 shop.keyPressed(keycode);
	return false;
  }

  @Override
  public boolean keyUp(int keycode)   
  {
	if(state == MainMenuState.LOBBY)
	 lobby.keyReleased(keycode);
	
	if(state == MainMenuState.SHOP)
	 shop.keyReleased(keycode);
	return false;
  }

  @Override
  public boolean keyTyped(char character) 
  {
	if(state == MainMenuState.LOBBY)
	 lobby.onKeyTyped(character);  
	
	if(state == MainMenuState.SHOP)
	 shop.onKeyTyped(character);
	return false;
  }

  @Override
  public boolean touchDown(int screenX, int screenY, int pointer, int button) 
  {
	menu.touchDown(screenX, screenY, pointer);
	if(state == MainMenuState.LOBBY)
	 lobby.touchDown(screenX, screenY, pointer);
	if(state == MainMenuState.SHOP)
	 shop.touchDown(screenX, screenY, pointer);
	return false;
  }

  @Override
  public boolean touchUp(int screenX, int screenY, int pointer, int button) 
  {
	menu.touchUp(screenX, screenY, pointer);
	if(state == MainMenuState.LOBBY)
	 lobby.touchUp(screenX, screenY, pointer);
	if(state == MainMenuState.SHOP)
	 shop.touchUp(screenX, screenY, pointer);
	return false;
  }

  @Override
  public boolean touchDragged(int screenX, int screenY, int pointer) 
  {
	menu.touchDrag(screenX, screenY, pointer);
	if(state == MainMenuState.LOBBY)
	 lobby.touchDrag(screenX, screenY, pointer);
	if(state == MainMenuState.SHOP)
	 shop.touchDrag(screenX, screenY, pointer);
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
  
  /**metodo da chiamare quando si riceve una stanza dal client*/
  public void onRoomReceived(String roomInfo)
  {  
    lobby.onRoomReceived(roomInfo);
  }
  
  /**metodo da chiamare quando si riceve un'eliminazione di stanza dal client*/
  public void onRoomDeleted(String roomInfo)
  {
	lobby.onRoomRemoved(roomInfo);
  }
  
  /**metodo da chiamare quando inizia il gameplay*/
  public void onGameplayBegin()
  {
	lobby.box.setVisible(false);  
  }
  
  /**chiamarlo per rimuovere tutte le stanze*/
  public void clearRooms()
  {
	lobby.rooms.clear();  
  }
  
  /**metodo chiamato quando si preme il tasto di uscita sul main menu*/
  protected void exitingFromMainMenu()
  {
  }
 
  /**
   * chiamato quando è stato inizializzato nel costruttore
   */
  protected void onCreated()
  {
  }
  
  public Player getPlayer()
  {
	return lobby.player;  
  }
  
  public void dispose()
  {
	menu.dispose();
	lobby.dispose();
	shop.dispose();
  }
}
