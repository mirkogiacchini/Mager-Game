package com.MirkoGiacchini.game.gameplay;

import java.io.BufferedReader;

import com.MirkoGiacchini.game.gameplay.items.ItemMap;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector3;

/**
 * spawn file: coordinate spawn point
 * @author Mirko
 *
 */
public class SpawnMap extends ItemMap
{
  Vector3 spawnPoint;
  
  public SpawnMap(FileHandle spawnFile, FileHandle heightmapFile, FileHandle mapFile, FileHandle itemFile, AssetManager asset) 
  {
    super(heightmapFile, mapFile, itemFile, asset);
    try
    {
      BufferedReader breader = new BufferedReader(spawnFile.reader());	
      String str[] = breader.readLine().split(" ");
      spawnPoint = new Vector3(Float.parseFloat(str[0]), Float.parseFloat(str[1]), Float.parseFloat(str[2]));
      breader.close();
    }catch(Exception e){ spawnPoint = new Vector3(); }
  }
  
  public Vector3 getSpawnPoint()
  {
	return spawnPoint;  
  }
}
