package com.MirkoGiacchini.game.gameplay.items;

import java.io.BufferedReader;
import java.util.HashMap;
import java.util.Map.Entry;

import com.MirkoGiacchini.game.AssetConstants;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

import it.MirkoGiacchini.world3d.heightmap.HeightmapMap;

/**
 * mappa in cui sono presenti item
 * @author Mirko
 *
 */
public class ItemMap extends HeightmapMap
{
  /**
   *  key: id dell'item
   *  value: item 
   */
  HashMap<Integer, IngameItem> items = new HashMap<Integer, IngameItem>(); 
  
  /**
   * item file:
   * N
   * path1 x1 y1 z1 type1 val1 time1
   * ..
   * ..
   * pathN xN yN zN typeN valN timeN
   */
  public ItemMap(FileHandle heightmapFile, FileHandle mapFile, FileHandle itemFile, AssetManager asset) 
  {
	super(heightmapFile, mapFile, asset);
	try
	{
	  BufferedReader reader = new BufferedReader(itemFile.reader());
	  int N = Integer.parseInt(reader.readLine());
	  
	  for(int c=0; c<N; c++)
	  {
		String edata[] = reader.readLine().split(" "); //leggo dati dell'item
		float x = Float.parseFloat(edata[1]), y = Float.parseFloat(edata[2]), z = Float.parseFloat(edata[3]);
		Matrix4 transf = new Matrix4(new Vector3(x, y, z), new Quaternion(0, 0, 0, 0), new Vector3(1, 1, 1));
		ItemType type = toType(Integer.parseInt(edata[4]));
		IngameItem ii = new IngameItem(asset.get(edata[0], Model.class), transf, type, Integer.parseInt(edata[5]), Integer.parseInt(edata[6]));
		if(type == ItemType.HP)
		 ii.setTexture(asset.get(AssetConstants.HP_TEXT, Texture.class));
		else
		 ii.setTexture(asset.get(AssetConstants.MANA_TEXT, Texture.class));
		items.put(c, ii);
	  }
	  reader.close();
	}catch(Exception e){}
  }
  
  public ItemType toType(int i)
  {
	switch(i)
	{
	  case 0: return ItemType.HP;
	  case 1: return ItemType.MANA;
	}
	return ItemType.HP;
  }
  
  public HashMap<Integer, IngameItem> getItems()
  {
	return items;  
  }
  
  @Override
  public void update(float deltaTime)
  {
	super.update(deltaTime);
	for(Entry<Integer, IngameItem> e : items.entrySet())
	 e.getValue().update(deltaTime);
  }
  
  @Override
  public void render(ModelBatch batch)
  {
	super.render(batch);
	for(Entry<Integer, IngameItem> e : items.entrySet())
	 if(!e.getValue().isTaken())
	  e.getValue().render(batch);
  }
  
  @Override
  public void dispose()
  {
	super.dispose();
	for(Entry<Integer, IngameItem> e : items.entrySet())
	 e.getValue().dispose();
  }
}
