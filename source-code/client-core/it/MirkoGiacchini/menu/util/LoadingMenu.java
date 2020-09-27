package it.MirkoGiacchini.menu.util;

import it.MirkoGiacchini.GameSocket.Client.Client;
import it.MirkoGiacchini.menu.Label;
import it.MirkoGiacchini.menu.Menu;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;

/**
 * menu di caricamento
 * @author Mirko
 */
public class LoadingMenu implements Disposable
{
  Label label; 
  AssetManager asset; //asset da caricare
  Client client; //client da connettere
  
  boolean success, updateAnim; //connesso con successo?, devo aggiornare animazione?
  
  String progress, loadingStr; //progresso dell'asset, stringa loading
  
  int animCount;
  
  Menu menu; //menu da usare sotto il loading menu
  
  public LoadingMenu(Label label, Menu menu, AssetManager asset, Client client)
  {
	this.label = label;
	this.menu = menu;
	
	this.menu.addLabel(this.label);
	
	animCount = 0;
	updateAnim = true;
	
	progress = "0%";
	loadingStr = "Loading";
	
	this.label.setText(loadingStr+" "+progress);
	
	this.asset = asset;
	this.client = client;
	success = true;
	clientConnectionThread.start();
	animUpdater();
  }
  
  /** true se il caricamento è completo, false altrimenti (se true non è sicuro che la connessione abbia avuto un buon successo, usare connectionStatus)*/
  public boolean update()
  {
	progress = Integer.toString((int)Math.floor(asset.getProgress()*100));
	progress+="%";
	label.setText(loadingStr+" "+progress);
	
	if(!clientConnectionThread.isAlive() && 
	   (asset.update() || !success))
	{
	  updateAnim = false;
	  return true;	
	}
	
	return false;  
  }
  
  /** stampa il menu*/
  public void draw(SpriteBatch batch)
  {
	menu.draw(batch);  
  }
  
  /** da usare dopo la fine di update, se è true la connessione è avvenuta
   * altrimenti no*/
  public boolean connectionStatus()
  {
	return success;  
  }
  
  //thread che connette il client al server
  private Thread clientConnectionThread = new Thread()
  {
	@Override
	public void run()
	{
	  if(!client.connect())	
	   success = false;
	}
  };
 
  private void animUpdater()
  {
	Timer.schedule(new Task()
	{
	  @Override
	  public void run()
	  {
		if(animCount == 3)
		{
		  loadingStr = loadingStr.substring(0, loadingStr.length()-3);
		  label.setText(loadingStr+" "+progress);
		  animCount = 0;
		}
		else
		{
		  loadingStr+=".";
		  label.setText(loadingStr+" "+progress);
		  animCount++;
		}	
		
		if(updateAnim)
		 animUpdater();
	  }
	}, 0.5f);
  }
  
  public Label getLabel()
  {
	return label;  
  }

  @Override
  public void dispose() 
  {
	menu.dispose();
  }
}
