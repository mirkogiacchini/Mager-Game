package com.MirkoGiacchini.game.gameplay.boss;

import java.io.BufferedReader;

import com.MirkoGiacchini.game.Game;
import com.MirkoGiacchini.game.gameplay.Gameplay;
import com.MirkoGiacchini.game.gameplay.SpawnMap;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

import it.MirkoGiacchini.world3d.CollShape;

/**
 * mappa contenente un boss
 * file boss: pathModel, x, y, z, radius, idleAnim, attackAnim, runAnim, dieAnim, collShape, args
 * @author Mirko
 *
 */
public class BossMap extends SpawnMap
{
  Boss boss;	
  Gameplay gameplay;
  
  public BossMap(FileHandle bossFile, FileHandle spawnFile, FileHandle heightmapFile, FileHandle mapFile, FileHandle itemFile, AssetManager asset, Game game, Gameplay gameplay) 
  {
	super(spawnFile, heightmapFile, mapFile, itemFile, asset);
	try
	{
	  BufferedReader br = new BufferedReader(bossFile.reader());
	  String str[] = br.readLine().split(" ");
	  Matrix4 transf = new Matrix4(new Vector3(Float.parseFloat(str[1]), Float.parseFloat(str[2]), Float.parseFloat(str[3])), new Quaternion(0, 0, 0, 0), new Vector3(1, 1, 1));
	  float args[] = new float[str.length - 10];
	  for(int i=0; i<args.length; i++)
	   args[i] = Float.parseFloat(str[i+10]);
	  boss = new Boss(game, asset.get(str[0], Model.class), 1, transf, Float.parseFloat(str[4]), str[5], str[6], str[7], str[8], CollShape.getShape(Integer.parseInt(str[9])), args);
	  this.gameplay = gameplay;
	  br.close();
	}catch(Exception e){ e.printStackTrace(); System.out.println(e.getMessage());}
	entities.add(boss);
  }
  
  @Override
  public void render(ModelBatch batch)
  {
	super.render(batch);
	boss.render(batch);
  }
  
  @Override
  public void update(float deltaTime)
  {
	super.update(deltaTime);
	boss.update(deltaTime, gameplay.getPlayer(), gameplay.getOtherPlayers());
  }
  
  public Boss getBoss()
  {
	return boss;  
  }
  
  @Override
  public void dispose()
  {
	super.dispose();
	boss.dispose();
  }
}
