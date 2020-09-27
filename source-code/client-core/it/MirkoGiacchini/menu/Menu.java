package it.MirkoGiacchini.menu;

import it.MirkoGiacchini.menu.basic.ObjCreator;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;

/**
 * menu generale
 * @author Mirko
 *
 */
public class Menu implements Disposable
{ 
  /** larghezza stardard */
  protected float defWidth;
  
  /** altezza standard */
  protected float defHeight;
  
  /** lista dei bottoni presenti */
  protected ArrayList<Button> buttons;
  
  /** lista delle etichette presenti */
  protected ArrayList<Label> labels;
  
  /** lista delle textfield */
  protected ArrayList<TextField> textFields;
  
  /** lista di message box*/
  protected ArrayList<MessageBox> messagesBox;
  
  /** lista dei creatori di oggetti*/
  protected ArrayList<ObjCreator> objCreators;
  
  public Menu(float width, float height)
  {
	defWidth = width;
	defHeight = height;
	buttons = new ArrayList<Button>();
	labels = new ArrayList<Label>();
	textFields = new ArrayList<TextField>();
	messagesBox = new ArrayList<MessageBox>();
	objCreators = new ArrayList<ObjCreator>();
	onCreating();
  }
  
  /** stampa a video il menu */
  public void draw(SpriteBatch batch) 
  {
	for(Button b : buttons)
     b.draw(batch);
	
	for(Label l : labels)
	 l.draw(batch);
	
	for(TextField f : textFields)
	 f.draw(batch);
	
	for(MessageBox mb : messagesBox)
	 mb.draw(batch);
	
	for(ObjCreator oc : objCreators)
	 oc.draw(batch);
  }
  
  /** routine click (mouse o tocco) */
  public void touchDown(float x, float y, int pointer)
  {
	 //ricalcolo posizione coordinate sullo schermo di default
	 x *= defWidth / getActualWidth();
 	 y *= defHeight / getActualHeight();
 	 y = Math.abs(y - defHeight);
 	
	 for(Button b : buttons) //gestisco bottoni
	  b.touchDown(x, y, pointer);
		 
	 for(TextField tf : textFields) //gestisco text field
	  tf.touchDown(x, y, pointer);
	 
	 for(MessageBox mb : messagesBox)
	  mb.touchDown(x, y, pointer);
	 
	 for(ObjCreator oc : objCreators)
	  oc.touchDown(x, y, pointer);
  }
  
  /** routine rilasciamento (mouse o tocco) */
  public void touchUp(float x, float y, int pointer)
  {
	 //ricalcolo posizione coordinate sullo schermo di default
	 x *= defWidth / getActualWidth();
	 y *= defHeight / getActualHeight();	
	 y = Math.abs(y - defHeight);
		
	 for(Button b : buttons) //clicco bottone
	  b.touchUp(x, y, pointer);
	 
	 for(MessageBox mb : messagesBox)
	  mb.touchUp(x, y, pointer);
	 
	 for(ObjCreator oc : objCreators)
	  oc.touchUp(x, y, pointer);
  }
  
  /** routine drag (mouse o tocco) */
  public void touchDrag(float x, float y, int pointer)
  {
	//ricalcolo posizione coordinate sullo schermo di default
	x *= defWidth / getActualWidth();
	y *= defHeight / getActualHeight();	
	y = Math.abs(y - defHeight);
		
	for(Button b : buttons) //muovo mouse su bottoni
	 b.touchDrag(x, y, pointer);
		
	for(TextField tf : textFields) //muovo mouse su text field
	 tf.touchDrag(x, y, pointer);
	
	for(MessageBox mb : messagesBox)
	 mb.touchDrag(x, y, pointer);
	
	for(ObjCreator oc : objCreators)
	 oc.touchDrag(x, y, pointer);
  }
  
  /** tasto digitato */
  public void onKeyTyped(char c)
  {
	for(TextField tf : textFields)
	 tf.keyTyped(c);
  }
  
  /** tasto rilasciato */
  public void keyReleased(int key)
  {
	for(TextField tf : textFields)
	 tf.keyReleased(key);
  }
  
  //deseleziona tutti i text field eccetto quello passato
  protected void deselectFieldFiltered(TextField filter)
  {
	for(TextField tf : textFields)
	 if(!tf.equals(filter))
	  tf.setPointer(-1);
  }
  
  /** tasto premuto */
  public void keyPressed(int key)
  {
	  
  }
  
  /** larghezza menu */
  public float getWidth()
  {
	return defWidth;  
  }
  
  /** altezza menu */
  public float getHeight()
  {
	return defHeight;   
  }
  
  public void setDefWidth(float w)
  {
	defWidth = w;  
  }
  
  public void setDefHeight(float h)
  {
	defHeight = h;  
  }
  
  /** aggiunge un bottone al menu */
  public void addButton(Button b) 
  {
	b.menu = this;  
	buttons.add(b);	
  }

  /** aggiunge etichetta al menu */
  public void addLabel(Label l)
  {
	l.menu = this;
	labels.add(l);
  }
 
  /** aggiunge text field al menu */
  public void addTextField(TextField tf)
  {
	tf.menu = this;
	textFields.add(tf);
  }
  
  public void addMessageBox(MessageBox mb)
  {
	mb.menu = this;
	messagesBox.add(mb);
  }
  
  public void addObjCreator(ObjCreator obj)
  {
	objCreators.add(obj);  
  }
  
  /** aggiunge widget qualsiasi al menu*/
  public void add(Object obj)
  {
	if(obj instanceof Button)
	 addButton((Button)obj);
	
	if(obj instanceof Label)
	 addLabel((Label)obj);
	
	if(obj instanceof TextField)
	 addTextField((TextField)obj);
	
	if(obj instanceof MessageBox)
	 addMessageBox((MessageBox)obj);
	
	if(obj instanceof ObjCreator)
	 addObjCreator((ObjCreator)obj);
  }
  
  public ArrayList<MessageBox> getMBoxList()
  {
	return messagesBox;  
  }

  public ArrayList<ObjCreator> getObjCreatorList()
  {
	return objCreators;  
  }
  
  public ArrayList<Button> getButtons()
  {
	return buttons;  
  }
  
  @Override 
  public void dispose() 
  {
	for(Button b : buttons)
	 b.dispose();
			
	for(Label l : labels)
	 l.dispose();
			
	for(TextField f : textFields)
	 f.dispose();
	
	for(MessageBox mb : messagesBox)
	 mb.dispose();
	
	for(ObjCreator oc : objCreators)
	 oc.dispose();
  }
  
  /** metodo chiamato alla creazione del menu*/
  protected void onCreating()
  {
	  
  }
  
  /**larghezza reale del menu*/
  protected float getActualWidth()
  {
	return Gdx.graphics.getWidth();  
  }
  
  /**altezza reale del menu*/
  protected float getActualHeight()
  {
	return Gdx.graphics.getHeight();  
  }
}
