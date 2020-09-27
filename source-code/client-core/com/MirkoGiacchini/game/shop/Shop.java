package com.MirkoGiacchini.game.shop;

import java.util.ArrayList;

import com.MirkoGiacchini.game.AssetConstants;
import com.MirkoGiacchini.game.abstracted.GamePlayer;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import it.MirkoGiacchini.menu.Image;
import it.MirkoGiacchini.menu.InformationBox;
import it.MirkoGiacchini.menu.Label;
import it.MirkoGiacchini.menu.basic.CollageMenu;
import it.MirkoGiacchini.menu.basic.TextureButton;
import it.MirkoGiacchini.pairing.Pair;

/**
 * menu shop
 * @author Mirko
 *
 */
public class Shop extends CollageMenu
{
  GamePlayer player;
  Label playerLabel;
  ItemIcon items[];
  int nPage = 0;
  int maxPage;
  TextureButton left, right;
  InformationBox box;
  
  /**
   * @param defWidth
   * @param defHeight
   */
  public Shop(float defWidth, float defHeight, AssetManager asset) 
  {
	super(defWidth, defHeight);
	super.addImage(new Image(asset.get(AssetConstants.SHOP_BACKGROUND, Texture.class), 0, 0, defWidth, defHeight)); //sfondo shop
	playerLabel = new Label("", 0, 0, AssetConstants.SHOP_INFO_COLOR, 1.5f*defWidth/640, 1.5f*defHeight/480, defWidth/3, AssetConstants.FONT_PATH, .5f);
	Texture itemIcons[] = getItemIcons(asset);
	items = new ItemIcon[itemIcons.length];
	maxPage = (int)Math.ceil((float)itemIcons.length/4);
	
	for(int i=0; i<items.length; i++)
	{
	  final int id = i;
	  
	  items[i] = new ItemIcon(defWidth/3, 9.f/20.f*defHeight, itemIcons[i], getIconX(i), getIconY(i), new Label(getItemDescr(i), AssetConstants.ITEM_ICONS_COLOR, 
			                  AssetConstants.FONT_PATH, .5f), getCost(i), asset.get(AssetConstants.DEF_BUTTON, Texture.class), asset.get(AssetConstants.DEF_BUTTON2, Texture.class), 
			                  AssetConstants.DEF_BUTTON_COLOR, AssetConstants.DEF_BUTTON2_COLOR, AssetConstants.FONT_PATH, .5f, AssetConstants.DEF_BUTTON_SX, AssetConstants.DEF_BUTTON_SY, 
			                  AssetConstants.DEF_BUTTON_SX * 1.5f, AssetConstants.DEF_BUTTON_SY * 1.5f)
	  {
	   	 @Override
	  	 public void onBuyPressed()
	  	 {
	  	   if(nPage * 4 <= id && nPage * 4 + 4 > id && !player.getItems().contains(id))
	  	   {
	  	 	 if(player.getPen() >= getCost(id))   
	  	     {   
	  		   player.setPen(player.getPen() - getCost(id));
	  		   player.addNewItem(id);
	  		   super.onBought();
	  		   box.setText("Bought!");
	  	     }
	  	 	 else
	  	 	  box.setText("You need more money!");
	  	 	 box.setVisible(true);
	  	   }
	  	 }
	  };
	}
	
	left = new TextureButton(asset.get(AssetConstants.LARROW, Texture.class), asset.get(AssetConstants.LARROW2, Texture.class), getWidth()/3, 0, getWidth()/3, defHeight/12, false)
	{
	  @Override
	  public void onReleased(int p)
	  {
		nPage--;
		if(nPage < 0)
		 nPage = maxPage - 1;
	  }
	};
	
	right = new TextureButton(asset.get(AssetConstants.RARROW, Texture.class), asset.get(AssetConstants.RARROW2, Texture.class), 2*getWidth()/3, 0, getWidth()/3, defHeight/12, false)
	{
	  @Override
	  public void onReleased(int p)
	  {
		nPage++;
		if(nPage >= maxPage)
		 nPage = 0;
	  }
	};
	
	box = new InformationBox(asset.get(AssetConstants.SHOP_BOX, Texture.class), getWidth()/2, getHeight()/2, getWidth()/2 - getWidth()/4, getHeight()/2 - getHeight()/4, "", 
			                 AssetConstants.SHOP_BOX_COLOR, new Texture(AssetConstants.DEF_BUTTON), asset.get(AssetConstants.DEF_BUTTON2, Texture.class), AssetConstants.DEF_BUTTON_COLOR,
			                 AssetConstants.DEF_BUTTON2_COLOR, AssetConstants.DEF_BUTTON_SX*defWidth/640, AssetConstants.DEF_BUTTON_SY*defHeight/480, AssetConstants.FONT_PATH, 
			                 AssetConstants.DEF_BUTTON_SX/3, AssetConstants.DEF_BUTTON_SY/3);
	
	add(left);
	add(right);
  }

  /** metodo da chiamare quando si riceve il player*/
  public void setPlayer(GamePlayer player)
  {
	this.player = player;
	for(int i=0; i<items.length; i++)
	 items[i].sell();
	
	for(int i : player.getItems())
	 items[i].onBought();
  }
  
  @Override
  public void draw(SpriteBatch batch)
  {
	super.draw(batch);  
	if(player != null)
	 player.drawOnShop(batch, playerLabel, defWidth, defHeight);
	for(int i = nPage*4; i < nPage*4+4; i++)
	 items[i].draw(batch);
	box.draw(batch);
  }
  
  private float getIconX(int i)
  {
	if(i%4 == 0 || i%4 == 1) //4 icone a pagina
	 return defWidth/3;
	return 2*defWidth/3;
  }
  
  private float getIconY(int i)
  {
	if(i%4 == 0 || i%4 == 2)
	 return 9.f/20.f*defHeight + defHeight/10;
    return defHeight/10;
  }
  
  private int getCost(int i) //costo degli item
  {
	switch(i)
	{
	  case GamePlayer.HP:
	  case GamePlayer.CRIT:
	  case GamePlayer.PERF:
	  case GamePlayer.BOSS_DMG: return 500;
	  default: return 400;
	}
  }
  
  //gli item corrispondono alle caratteristiche del player
  private String getItemDescr(int i) //descrizione degli item
  {
	return GamePlayer.getDescr(i);  
  }
  
  private Texture[] getItemIcons(AssetManager asset)
  {
	Texture textures[] = new Texture[16];
	for(int i=0; i<16; i++)
	 textures[i] = asset.get(AssetConstants.itemIcon(i), Texture.class);
	return textures;
  }
  
  /**a = indice dei bonus, b = incremento dei bonus*/
  public static ArrayList<Pair<Integer, Integer>> getItemInc(int i)
  {
	ArrayList<Pair<Integer, Integer>>res = new ArrayList<Pair<Integer, Integer>>();
	
	if(i == 0 || (i >= 4 && i <= 15)) //HP o Skill+ o SkillRes+ -> 20
	 res.add(new Pair<Integer, Integer>(i, 20));
	
	if(i == GamePlayer.BOSS_DMG) //boss damage
	 res.add(new Pair<Integer, Integer>(i, 10));
	 
	if(i == 1 || i == 2) //perf/crit
	 res.add(new Pair<Integer, Integer>(i, 5));
	
	return res;
  }
  
  @Override
  public void touchDown(float x, float y, int p)
  {
	float x2 = x * getWidth() / Gdx.graphics.getWidth();
	float y2 = Math.abs(y * getHeight() / Gdx.graphics.getHeight() - getHeight());  
	
	if(!box.isVisible())
	{
	  super.touchDown(x, y, p);
	  for(int i=nPage*4; i<nPage*4+4; i++)
	   items[i].onTouchDown(x2, y2, p);
	}
	else
	 box.touchDown(x2, y2, p);
  }
  
  @Override
  public void touchUp(float x, float y, int p)
  {
    float x2 = x * getWidth() / Gdx.graphics.getWidth();
	float y2 = Math.abs(y * getHeight() / Gdx.graphics.getHeight() - getHeight());   
	
	if(!box.isVisible())
	{
	  super.touchUp(x, y, p);
	  for(int i=nPage*4; i<nPage*4+4; i++)
	   items[i].onTouchUp(x2, y2, p);
	}
	else
	 box.touchUp(x2, y2, p);
  }
  
  @Override
  public void touchDrag(float x, float y, int p)
  {
	float x2 = x * getWidth() / Gdx.graphics.getWidth();
	float y2 = Math.abs(y * getHeight() / Gdx.graphics.getHeight() - getHeight());   
	
	if(!box.isVisible())
	{
	  super.touchDrag(x, y, p);
	  for(int i=nPage*4; i<nPage*4+4; i++)
	   items[i].onTouchDrag(x2, y2, p);
	}
	else
	 box.touchDrag(x2, y2, p);
  }
  
  @Override
  public float getHeight()
  {
	return defHeight + defHeight/9;  
  }
  
  @Override
  public void dispose()
  {
	super.dispose();  
	playerLabel.dispose();  
	box.dispose();
	for(ItemIcon i : items)
	 i.dispose();
  }
}
