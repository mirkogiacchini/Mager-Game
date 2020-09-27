package it.MirkoGiacchini.world3d.heightmap;

import java.io.BufferedReader;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.math.Vector3;

import it.MirkoGiacchini.world3d.Map3D;

/**
 * mappa con una heightmap.
 * heightmapFile: pathHandle pathTexture
 * @author Mirko
 *
 */
public class HeightmapMap extends Map3D
{
  Heightmap heightmap;
  
  public HeightmapMap(FileHandle heightmapFile, FileHandle mapFile, AssetManager asset) 
  {
	 super(mapFile, asset);
	 try
	 {
	   BufferedReader reader = new BufferedReader(heightmapFile.reader());
	   String data[] = reader.readLine().split(" ");
	   heightmap = asset.get(data[0], Heightmap.class);
	   heightmap.setTranslation(new Vector3(Float.parseFloat(data[1]), Float.parseFloat(data[2]), Float.parseFloat(data[3])));
	   heightmap.update(0);
	   reader.close();
	   entities.add(heightmap);
	 }catch(Exception e){e.printStackTrace(); System.out.println(e.getMessage());}
  }
  
  @Override
  public void render(ModelBatch batch)
  {
	super.render(batch);
	if(heightmap != null)
	 heightmap.render(batch); 
  }
}
