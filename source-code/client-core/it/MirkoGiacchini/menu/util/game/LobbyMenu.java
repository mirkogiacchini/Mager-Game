package it.MirkoGiacchini.menu.util.game;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import it.MirkoGiacchini.GameSocket.Client.DBClient;
import it.MirkoGiacchini.GameSocket.Server.Server;
import it.MirkoGiacchini.game.abstracted.Player;
import it.MirkoGiacchini.menu.Button;
import it.MirkoGiacchini.menu.Image;
import it.MirkoGiacchini.menu.InformationBox;
import it.MirkoGiacchini.menu.Label;
import it.MirkoGiacchini.menu.MessageBox;
import it.MirkoGiacchini.menu.TextField;
import it.MirkoGiacchini.menu.basic.CollageMenu;
import it.MirkoGiacchini.menu.basic.FontButton;
import it.MirkoGiacchini.menu.basic.ObjCreator;
import it.MirkoGiacchini.menu.basic.TextureButton;
import it.MirkoGiacchini.menu.basic.TripleFontButton;

/**Menu lobby usato nel MainMenu*/
public class LobbyMenu extends CollageMenu
{ 
  /**stanze create*/
  LinkedHashMap<Integer, RoomInfo> rooms = new LinkedHashMap<Integer, RoomInfo>();
	
  public static final int RPS = 8; //room per screen
  int roomInd;
  
  /**box per attese*/
  MessageBox box;
  /**box per comunicare informazioni*/
  InformationBox infoBox;
  
  /**bottone usato per stampare le stanze*/
  TripleFontButton roomButton;
  
  /** creatore delle stanze*/
  ObjCreator roomCreator;
  
  DBClient client;
  
  Player player = null;
  /** label del giocatore*/
  Label playerLab;
  
  /**
   * @param defWidth width default
   * @param defHeight height default
   * @param infoBack background sinistro
   * @param roomsBack background destro
   * @param left1 tasto sinistro selezionato
   * @param left2 tasto sinistro non selezionato
   * @param right1 tasto destro selezionato
   * @param right2 tasto destro non selezionato
   * @param roomBack1 tasto stanza selezionato
   * @param roomBack2 tasto stanza non selezionato
   * @param roomC1 colore stanza selezionato
   * @param roomC2 colore stanza non selezionato
   * @param btex1 bottone generale selezionato
   * @param btex2 bottone generale non selezionato
   * @param c1 colore bottone generale selezionato
   * @param c2 colore bottone generale non selezionato
   * @param client client connesso
   * @param fontPath percorso font
   * @param objcb background messaggi
   * @oaram roomDescr descrizione delle varie room possibili
   * @param left21 tasto sinistro2 selezionato
   * @param left22 tasto sinistro2 non selezionato
   * @param right21 tasto destro2 selezionato
   * @param right22 tasto destro2 non selezionato
   * @param c3 colore sui tasti left21...right22
   * @param labColor colore label personaggio
   * @param sx size x font
   * @param sy size y font
   */
  public LobbyMenu(float defWidth, float defHeight, Texture infoBack, Texture roomsBack, Texture left1, Texture left2, Texture right1, Texture right2, Texture roomBack1, Texture roomBack2, Color roomC1, Color roomC2,
		           Texture btex1, Texture btex2, Color c1, Color c2, final DBClient client, String fontPath, Texture objcb, final String roomDescr[], final String roomSizes[],
		           Texture left21, Texture left22, Texture right21, Texture right22, Color c3, Color labColor, float sx, float sy) 
  {
	super(defWidth, defHeight);
	this.client = client;
	roomInd = 0; 
    images.add(new Image(infoBack, 0, 0, defWidth/2, defHeight-defHeight/10));
    images.add(new Image(roomsBack, defWidth/2, defHeight/10, defWidth/2, defHeight-defHeight/5));
    
    float sizeX = sx * defWidth / 640, sizeY = sy * defHeight / 480;
    
    box = new MessageBox(objcb, defWidth/2, defHeight/4, defWidth/2 - defWidth/4, defHeight/2 - defHeight/8, "", c1, sizeX, sizeY, fontPath);
    infoBox = new InformationBox(objcb, defWidth/2, defHeight/4, defWidth/2 - defWidth/4, defHeight/2 - defHeight/8, "", c1, btex1, btex2, c1, c2, sx*defWidth/640, sy*defHeight/480, fontPath, sx/3, sy/3);
    
    roomButton = new TripleFontButton(roomBack1, roomBack2, defWidth/2, 0, defWidth/2, defHeight/10, new Label("", sizeX, sizeY, 0, fontPath), new Label("", sizeX, sizeY, 0, fontPath), new Label("", sizeX, sizeY, 0, fontPath), roomC1, roomC2, false);
	
	//crea una stanza
	addButton(new FontButton(btex1, btex2, defWidth/4f - defWidth/5, defHeight/3 - defHeight/20, defWidth/2.5f, defHeight/10, new Label("Create room", sizeX, sizeY, 0, fontPath), c1, c2, false)
	{
      @Override
      public void onReleased(int pointer)
      {
    	if(!roomCreator.isVisible())
    	 roomCreator.setVisible(true);
      }
	});
	
    addButton(new TextureButton(left1, left2, defWidth/2, 0, defWidth/4, defHeight/10, false) //scorri le pagine a sinistra
    {
      @Override
      public void onReleased(int pointer)
      {
    	roomInd--;
    	if(roomInd < 0)
    	 roomInd = rooms.size() / RPS;
      }
    });
    
    addButton(new TextureButton(right1, right2, defWidth/2 + defWidth/4, 0, defWidth/4, defHeight/10, false) //Scorri le pagine a destra
    {
      @Override
      public void onReleased(int pointer)
      {
    	roomInd++;
    	if(roomInd > rooms.size() / RPS)
    	 roomInd = 0;
      }
    });
    
    ArrayList<String[]> descr = new ArrayList<String[]>();
    descr.add(roomSizes);
    descr.add(roomDescr);
    
    roomCreator = new ObjCreator(defWidth/2 - defWidth/4, defHeight/2 - defHeight/4, defWidth/2, defHeight/2, objcb, descr, left21, left22, right21, right22, c3, fontPath, sizeX, sizeY, btex1, btex2, c1, c2)
    {
      @Override
      public void objectCreated()
      {
    	int sizeInd = values.get(0).getIndex();
    	int mapInd = values.get(1).getIndex();
    	
    	client.changeRoom(Server.AUTO_ROOM+" "+roomSizes[sizeInd]+" "+roomDescr[mapInd]); //la stanza viene generata dal server 
  	    box.setText("Creating room...");
  	    box.setVisible(true);
  	    roomCreator.setVisible(false);
      }
    };
    
    playerLab = new Label("", 0, 0, labColor, 1, 1, 0, fontPath);

    add(box);
    add(infoBox);
    add(roomCreator);
  }
  
  @Override
  public void touchDown(float screenX, float screenY, int pointer)
  {
	if(!box.isVisible() && !infoBox.isVisible() && !roomCreator.isVisible())
	{
	  super.touchDown(screenX, screenY, pointer);
	  manageRoomTouch(screenX, screenY, pointer, 0);
	}
	else
	{
	  for(MessageBox mb : getMBoxList())
	   mb.touchDown(screenX * getWidth() / Gdx.graphics.getWidth(), Math.abs(screenY * getHeight() / Gdx.graphics.getHeight() - getHeight()), pointer);
	  
	  for(ObjCreator oc : getObjCreatorList())
	   oc.touchDown(screenX * getWidth() / Gdx.graphics.getWidth(), Math.abs(screenY * getHeight() / Gdx.graphics.getHeight() - getHeight()), pointer);
	}
  }
  
  @Override
  public void touchUp(float screenX, float screenY, int pointer)
  {
	if(!box.isVisible() && !infoBox.isVisible() && !roomCreator.isVisible())
	{
	  super.touchUp(screenX, screenY, pointer);
	  manageRoomTouch(screenX, screenY, pointer, 1);
	}
    else
    {
	  for(MessageBox mb : getMBoxList())
	   mb.touchUp(screenX * getWidth() / Gdx.graphics.getWidth(), Math.abs(screenY * getHeight() / Gdx.graphics.getHeight() - getHeight()), pointer);  
	  
	  for(ObjCreator oc : getObjCreatorList())
		oc.touchUp(screenX * getWidth() / Gdx.graphics.getWidth(), Math.abs(screenY * getHeight() / Gdx.graphics.getHeight() - getHeight()), pointer); 
    }
  }
  
  @Override
  public void touchDrag(float screenX, float screenY, int pointer)
  {
	if(!box.isVisible() && !infoBox.isVisible() && !roomCreator.isVisible())
	{
	  super.touchDrag(screenX, screenY, pointer);
	  manageRoomTouch(screenX, screenY, pointer, 2);
	}
	else
	{
	  for(MessageBox mb : getMBoxList())
	   mb.touchDrag(screenX * getWidth() / Gdx.graphics.getWidth(), Math.abs(screenY * getHeight() / Gdx.graphics.getHeight() - getHeight()), pointer);
	  
	  for(ObjCreator oc : getObjCreatorList())
	   oc.touchDrag(screenX * getWidth() / Gdx.graphics.getWidth(), Math.abs(screenY * getHeight() / Gdx.graphics.getHeight() - getHeight()), pointer);
	}
  }
  
  /** metodo da chiamare quando si riceve il record del player*/
  public void onPlayerReceived(Player player)
  {
	this.player = player;  
  }
  
  /**metodo da chiamare quando il client cambia stanza*/
  public void onClientRoomChanged()
  {
	box.setVisible(false);  
  }
  
  /** metodo da chiamare quando viene negato l'accesso alla stanza al client*/
  public void onClientChangeNegated()
  {
	infoBox.setText("Error!");
	infoBox.setVisible(true);  
	box.setVisible(false);
  }
  
  @Override
  public void draw(SpriteBatch batch)
  {
	for(Image i : images)
	 i.draw(batch);
	
	drawRooms(batch);
	
	for(Button b : buttons)
	 b.draw(batch);
		
	for(Label l : labels)
	 l.draw(batch);
	
	if(player != null)
	 player.drawLobbyMenu(batch, playerLab, defWidth, defHeight);
		
	for(TextField f : textFields)
	 f.draw(batch);
		
	for(MessageBox mb : messagesBox)
	 mb.draw(batch);
	
	for(ObjCreator oc : objCreators)
	 oc.draw(batch);
  }
  
  protected void drawRooms(SpriteBatch batch) //stampa la lista delle stanze
  {
	int i = 0;
	for(Entry<Integer, RoomInfo> e : rooms.entrySet())
	{
	  if(i >= RPS * roomInd && i < ((RPS*roomInd+RPS > rooms.size()) ? rooms.size() : RPS*roomInd+RPS))
	  {
		roomButton.setY(defHeight - defHeight/10*(i - RPS * roomInd +2));
		roomButton.setText(e.getKey()+"");
		roomButton.setText2(e.getValue().size+"/"+e.getValue().maxSize);
		roomButton.setText3(e.getValue().descr);
		roomButton.setPointer(e.getValue().pointer);
	    roomButton.draw(batch);
	  } 
	  i++;	
	  if(i >= ((RPS*roomInd+RPS > rooms.size()) ? rooms.size() : RPS*roomInd+RPS))
	   break;
	}
  }
  
  protected boolean collideWithRoom(int index, float xp, float yp)
  {
	float y = defHeight - defHeight/10*(index - RPS * roomInd +2);
	float x = defWidth/2;
	return (xp >= x && xp <= defWidth && yp >= y && yp <= y + defHeight/10);
  }
  
  /**
   * @param touchType 0: touch down, 1: touch up, 2: touch drag
   */
  protected void manageRoomTouch(float screenX, float screenY, int pointer, int touchType)
  {
	screenX *= defWidth / Gdx.graphics.getWidth();
	screenY *= defHeight / Gdx.graphics.getHeight();
	screenY = Math.abs(screenY - defHeight);
	int i = 0;
	for(Entry<Integer, RoomInfo> e : rooms.entrySet())
	{
	  if(i >= RPS * roomInd && i < ((RPS*roomInd+RPS > rooms.size()) ? rooms.size() : RPS*roomInd+RPS))
	  {
		boolean collision = collideWithRoom(i, screenX, screenY);
        if(touchType <= 1) //down-up
        {
          if(collision)
           if(touchType == 0)
            e.getValue().touchDown(pointer);
           else
        	e.getValue().touchUp(pointer, e.getKey());
        }
        else
         e.getValue().touchDrag(pointer, collision);
      } 
	  i++;	
	  if(i >= ((RPS*roomInd+RPS > rooms.size()) ? rooms.size() : RPS*roomInd+RPS))
	   break;
	}
  }
  
  /** ricevute informazioni su stanza*/
  public void onRoomReceived(String roomInfo)
  {
	//room   room_size    room_max_size    room_description
	String data[] = roomInfo.split(" ");
	int room = Integer.parseInt(data[0]);
	int size = Integer.parseInt(data[1]);
	int maxSize = Integer.parseInt(data[2]);
	String descr = data[3];
	
	if(rooms.containsKey(room))
	 rooms.get(room).set(size, maxSize, descr);	
	else
	 rooms.put(room, new RoomInfo(size, maxSize, descr));
  }
  
  /** stanza rimossa*/
  public void onRoomRemoved(String roomInfo)
  {
	int room = Integer.parseInt(roomInfo);
	if(rooms.containsKey(room))
	 rooms.remove(room);  
  }
  
  /** dal giocatore crea il record, che potrà poi essere usato per l'aggiornamento nel db*/
  public String getPlayerRecord()
  {
	return player.toRecord();  
  }
  
  public void resetPlayer()
  {
	player = null;  
  }
  
  @Override
  public void dispose()
  {
	super.dispose();
    roomButton.dispose();
  }
  
  /** informazioni su una stanza*/
  private class RoomInfo
  {
	public int size;
	public int maxSize;
	public String descr;
	public int pointer;
	
	public RoomInfo(int size, int maxSize, String descr)
	{
      set(size, maxSize, descr);
      pointer = -1;
	}
	
	public void set(int size, int maxSize, String descr)
	{
      this.size = size;
      this.maxSize = maxSize;
      this.descr = descr;
	}
	
	public void touchDown(int pointer)
	{
	  if(this.pointer == -1) //bottone premuto
		this.pointer = pointer;
	}
	
	public boolean touchUp(int pointer, int roomId)
	{
	  if(this.pointer == pointer)
	  {
	    this.pointer = -1;	
    	if(!box.isVisible())
    	{
    	  client.changeRoom(roomId); //si richiede di entrare nella stanza
    	  box.setText("Entering in room...");
    	  box.setVisible(true);
    	  return true;
    	}
	  }
	  return false;
	}
	
	public void touchDrag(int pointer, boolean collision)
	{
	  if(!collision && pointer == this.pointer) 	
	   this.pointer = -1;
	  else
	   if(collision && this.pointer == -1)
		this.pointer = pointer;
	}
  }
}
