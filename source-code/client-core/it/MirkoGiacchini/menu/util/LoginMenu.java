package it.MirkoGiacchini.menu.util;

import it.MirkoGiacchini.GameSocket.Client.DBClient;
import it.MirkoGiacchini.crypto.Cipher;
import it.MirkoGiacchini.menu.InformationBox;
import it.MirkoGiacchini.menu.Label;
import it.MirkoGiacchini.menu.MessageBox;
import it.MirkoGiacchini.menu.TextFilter;
import it.MirkoGiacchini.menu.basic.FontButton;
import it.MirkoGiacchini.menu.basic.TextureField;
import it.MirkoGiacchini.menu.basic.TextureMenu;
import it.MirkoGiacchini.menu.basic.VirtualKeyboard;
import it.MirkoGiacchini.util.Util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * menu di login
 * @author Mirko
 *
 */
public class LoginMenu implements InputProcessor
{
  protected TextureMenu menu;
  
  /** client nel loginmenu */
  DBClient client;
  
  VirtualKeyboard keyboard = null;
  
  TextureField username, password;
  
  InformationBox infoBox;
  MessageBox mexBox;
  
  /**
   * @param menuTex texture di background del menu
   * @param buttonTex1 texture premuta dei bottoni principali: register/login/delete
   * @param buttonTex2 texture non premuta dei bottoni principali: register/login/delete
   * @param buttonc1 colore premuto dei bottoni principali 
   * @param buttonc2 colore non premuto dei bottoni principali
   * @param fieldTex1 texture premuta dei field 
   * @param fieldTex2 texture non premuta dei field
   * @param labelC colore delle scritte nei text field
   * @param button2Tex1 texture premuta del tastierino
   * @param button2Tex2 texture non premuta del tastierino
   * @param bc1 colore premuto del tastierino
   * @param bc2 colore non premuto del tastierino
   * @param client client connesso
   * @param boxt texture del message box
   * @param boxc colore del message box
   * @param boxbt1 texture premuta del bottone del message box
   * @param boxbt2 texture non premuta del bottone del message box
   * @param boxbc1 colore premuto scritte bottone message box
   * @param boxbc2 colore non premuto scritte bottone message box
   * @param fontPath percorso del font
   * @param offset offset del font passato come parametro rispetto al font ideale
   * @param cipher cifrario usato per criptare le password
   * @param tsx size x font testo
   * @param tsy size y font testo
   * @param bsx size x font bottoni
   * @param bsy size y font bottoni
   */
  public LoginMenu(Texture menuTex, Texture buttonTex1, Texture buttonTex2, Color buttonc1, Color buttonc2, Texture fieldTex1, Texture fieldTex2, Color labelC, Texture button2Tex1, Texture button2Tex2, 
		           Color bc1, Color bc2, DBClient client, Texture boxt, Color boxc, Texture boxbt1, Texture boxbt2, Color boxbc1, Color boxbc2, String fontPath, float offset, final Cipher cipher, float tsx, float tsy,
		           float bsx, float bsy, float width, float height)
  {
	this.client = client;
	float labelSizeX = width * tsx / 640;
	float labelSizeY = height * tsy / 480;
	menu = new TextureMenu(menuTex, width, height);
	
	infoBox = new InformationBox(boxt, width/2, height/2, width/2-width/4, height/2-height/4, "", boxc, boxbt1, boxbt2, boxbc1, boxbc2, fontPath);
	mexBox = new MessageBox(boxt, width/2, height/2, width/2-width/4, height/2-height/4, "", boxc, fontPath);
	menu.addMessageBox(infoBox);
	menu.addMessageBox(mexBox);
	
	BitmapFont tmpFont = new BitmapFont(Util.getHandle(fontPath));
	tmpFont.getData().setScale(labelSizeX * offset, labelSizeY * offset);
	Label.glyph.setText(tmpFont, "Username");
	float w = Label.glyph.width;
	float h = Label.glyph.height;
	
    menu.addLabel(new Label("Username", w/4, height - h + 1, labelC, labelSizeX, labelSizeY, Label.INFW, fontPath));
    username = new TextureField(fieldTex1, fieldTex2, w + w/4 + 1, height - 2 * h, width - w - w/2 - 2, h * 1.5f, labelSizeX, labelSizeY, labelC, false, true, TextFilter.PSW_FILTER, 10, fontPath);
	menu.addLabel(new Label("Password", w/4, height - 3 * h + 1, labelC, labelSizeX, labelSizeY, Label.INFW, fontPath));
	menu.addTextField(username); //text field username
	password = new TextureField(fieldTex1, fieldTex2, w + w/4 + 1, height - 4 * h, width - w - w/2 - 2, h * 1.5f, labelSizeX, labelSizeY, labelC, true, true, TextFilter.PSW_FILTER, 10, fontPath);
	menu.addTextField(password); //text field psw
	
	//bottone per login
	menu.addButton(new FontButton(buttonTex1, buttonTex2, width/2 - (width/4 + width/8), height - 6.5f * h, width/4, h * 1.4f, new Label("Login", 0, 0, buttonc1, bsx*width/640, bsy*height/480, 0, fontPath), buttonc1, buttonc2, false) 
	{
	  @Override
	  public void onReleased(int pointer)
	  {
		if(username.getText().trim().length() >= 4 && password.getText().trim().length() >= 4)
		{
		  LoginMenu.this.client.connectToDB(username.getText().trim(), cipher.encrypt(password.getText().trim()));  
		  mexBox.setText("Logging...");
		  mexBox.setVisible(true);
		}
		else
		{
		  infoBox.setText("Error! Insert at least 4 characters!");
		  infoBox.setVisible(true);
		}
	  }
	});
	
	//bottone per registrazione
	menu.addButton(new FontButton(buttonTex1, buttonTex2, width/2 - width/8 + 1, height - 6.5f * h, width/4, h * 1.4f, new Label("Register", 0, 0, buttonc1, bsx*width/640, bsy*height/480, 0, fontPath), buttonc1, buttonc2, false) 
	{
	  @Override
	  public void onReleased(int pointer)
	  {
		if(username.getText().trim().length() >= 4 && password.getText().trim().length() >= 4)  
		{
		  LoginMenu.this.client.registerOnDB(username.getText().trim(), cipher.encrypt(password.getText().trim()));
		  mexBox.setText("Registering...");
		  mexBox.setVisible(true);
		}
		else
		{
		  infoBox.setText("Error! Insert at least 4 characters!");
		  infoBox.setVisible(true);
		}
	  }
	});
	
	//bottone per eliminazione
	menu.addButton(new FontButton(buttonTex1, buttonTex2, width/2 + width/8 + 2, height - 6.5f * h, width/4, h * 1.4f, new Label("Delete", 0, 0, buttonc1, bsy*width/640, bsx*height/480, 0, fontPath), buttonc1, buttonc2, false) 
	{
	  @Override
	  public void onReleased(int pointer)
	  {
		if(username.getText().trim().length() >= 4 && password.getText().trim().length() >= 4)  
		{
		  LoginMenu.this.client.deleteFromDB(username.getText().trim(), cipher.encrypt(password.getText().trim()));
		  mexBox.setText("Deleting...");
		  mexBox.setVisible(true);
		}
		else
		{
		  infoBox.setText("Error! Insert at least 4 characters!");
		  infoBox.setVisible(true);
		}
	  }
	});
   
	if(Gdx.app.getType() == ApplicationType.Android)
	 keyboard = new VirtualKeyboard(0, 0, width, 1.5f*height/3, menu, button2Tex1, button2Tex2, labelSizeX, labelSizeY, bc1, bc2, fontPath);

	tmpFont.dispose();
	onCreating();
  }
  
  public void giveController()
  {
    Gdx.input.setInputProcessor(this);  
  }
	
  public void draw(SpriteBatch batch)
  {
	menu.draw(batch);  
  }
  
  /** record ricevuto */
  public void onDbConnection()
  {
	mexBox.setVisible(false); 
	infoBox.setVisible(false);
  }
  
  /** connessione fallita al db*/
  public void onDbConnectionFailed()
  {
	mexBox.setVisible(false); 
    infoBox.setText("Error logging");
	infoBox.setVisible(true);  
  }
  
  /** registrazione avvenuta */
  public void onRegistration()
  {
	mexBox.setVisible(false); 
	infoBox.setText("Registered");
	infoBox.setVisible(true);  
  }
  
  /** registrazione fallita */
  public void onRegistrationFailed()
  {
	mexBox.setVisible(false); 
	infoBox.setText("Error registering");
	infoBox.setVisible(true);  
  }
  
  /**account eliminato*/
  public void onAccountDeleted()
  {
	mexBox.setVisible(false); 
	infoBox.setText("Account deleted");
	infoBox.setVisible(true);  
  }
  
  /**eliminazione fallita*/
  public void onAccountDelFailed()
  {
    mexBox.setVisible(false); 
	infoBox.setText("Error deleting account");
	infoBox.setVisible(true);	  
  }
  
  public void showInformation(String text)
  {
	infoBox.setText(text);
	infoBox.setVisible(true);
  }
  
  @Override
  public boolean keyDown(int keycode) 
  {
	if(!mexBox.isVisible() && !infoBox.isVisible())  
	 menu.keyPressed(keycode);  
	
	if(keycode == Keys.BACK)
	{
	  
	  Gdx.app.exit();	
	}
	return false;
  }

  @Override
  public boolean keyUp(int keycode) 
  {
	if(!mexBox.isVisible() && !infoBox.isVisible())   
	 menu.keyReleased(keycode);
 	return false;
  }

  @Override
  public boolean keyTyped(char character) 
  {
	if(!mexBox.isVisible() && !infoBox.isVisible())    
	 menu.onKeyTyped(character);
 	return false;
  }

  @Override
  public boolean touchDown(int screenX, int screenY, int pointer, int button) 
  {
	if(!mexBox.isVisible() && !infoBox.isVisible()) //non si possono usare altri bottoni durante i messaggi
	 menu.touchDown(screenX, screenY, pointer);
	else
	 if(infoBox.isVisible()) //se è messaggio di informazione bisogna però permettere la possibilà di chiuderlo
	  for(MessageBox mb : menu.getMBoxList())
	   mb.touchDown(screenX * menu.getWidth() / Gdx.graphics.getWidth(), Math.abs(screenY * menu.getHeight() / Gdx.graphics.getHeight() - menu.getHeight()), pointer);
  	return false;
  }

  @Override
  public boolean touchUp(int screenX, int screenY, int pointer, int button) 
  {
	if(!mexBox.isVisible() && !infoBox.isVisible())    
	 menu.touchUp(screenX, screenY, pointer); 
	else
	 if(infoBox.isVisible())
	  for(MessageBox mb : menu.getMBoxList())
	   mb.touchUp(screenX * menu.getWidth() / Gdx.graphics.getWidth(), Math.abs(screenY * menu.getHeight() / Gdx.graphics.getHeight() - menu.getHeight()), pointer);
  	return false;
  }

  @Override
  public boolean touchDragged(int screenX, int screenY, int pointer) 
  {
	if(!mexBox.isVisible() && !infoBox.isVisible()) 
	 menu.touchDrag(screenX, screenY, pointer);
	else
	 if(infoBox.isVisible())
	  for(MessageBox mb : menu.getMBoxList())
	   mb.touchDrag(screenX * menu.getWidth() / Gdx.graphics.getWidth(), Math.abs(screenY * menu.getHeight() / Gdx.graphics.getHeight() - menu.getHeight()), pointer);  
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
  
  /** metodo chiamato alla creazione del menu*/
  public void onCreating()
  {
	  
  }
     
  public void dispose()
  {
	menu.dispose();  
  }
}
