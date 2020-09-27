package it.MirkoGiacchini.world3d;

import java.io.BufferedReader;
import java.util.ArrayList;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;

import it.MirkoGiacchini.world3d.heightmap.Heightmap;

/**
 * mappa 3d statica
 * @author Mirko
 *
 */
public class Map3D 
{
  /** entità nella mappa */
  protected ArrayList<PhysicsEntity> entities = new ArrayList<PhysicsEntity>();
  /**entità non fisiche*/
  protected ArrayList<Entity> decorativeEntities = new ArrayList<Entity>();
  
  /**
   * la mappa viene presa in input da un file.map
   * 
   * struttura file.map:
   * 
   * N //numero elementi nella mappa
   * pathModello1 x1 y1 z1 rotx1 roty1 rotz1 gradirot1 collshape1 [parametri_collshape1] 
   * ...
   * ...
   * pathModello_n x_n y_n z_n rotx_n roty_n rotz_n gradirot_n collshape_n [parametri_collshape_n] 
   * 
   * M //numero elementi non fisici nella mappa
   * pathModello1 x1 y1 z1 rotx1 rotx1 roty1 rotz1 gradirot1
   * ...
   * ...
   * pathModello_n x_n y_n z_n rotx_n roty_n rotz_n gradirot_n
   * 
   * Xdsx(invWall) Ydsx(invWall) Zdsx(invWall) Xtdx(invWall) Ytdx(invWall) Ztdx(invWall) 
   * 
   * @param mapFile handle del file da cui leggere la mappa
   * @param assets assetmanager da cui prendere le varie risorse necessarie alla creazione della mappa
   */
  public Map3D(FileHandle mapFile, AssetManager asset)
  {
	try
	{
	  BufferedReader reader = new BufferedReader(mapFile.reader());
	  int N = Integer.parseInt(reader.readLine());

	  for(int c=0; c<N; c++)
	  {
		String edata[] = reader.readLine().split(" "); //leggo dati dell'entità
		
		float x = Float.parseFloat(edata[1]), y = Float.parseFloat(edata[2]), z = Float.parseFloat(edata[3]); //coordinate
		Matrix4 matrix = new Matrix4(new Vector3(x, y, z), new Quaternion(0, 0, 0, 0), new Vector3(1, 1, 1));
		
		float collArgs[] = new float[edata.length - 9]; //parametri del collshape
		for(int i=9; i<edata.length; i++)
		 collArgs[i - 9] = Float.parseFloat(edata[i]);
			
		PhysicsEntity ent = new PhysicsEntity(asset.get(edata[0], Model.class), 1, matrix, 0, CollShape.getShape(Integer.parseInt(edata[8])), collArgs); //creo entità
		
		float rx = Integer.parseInt(edata[4]), ry = Integer.parseInt(edata[5]), rz = Integer.parseInt(edata[6]); //applico rotazione
		ent.rotate(Integer.parseInt(edata[7]), new Vector3(rx, ry, rz));
		ent.body.setWorldTransform(ent.getTransform());
		
		entities.add(ent); //aggiungo entità
	  }
	  
	  int M = Integer.parseInt(reader.readLine());
	  for(int c=0; c<M; c++)
	  {
		String edata[] = reader.readLine().split(" ");
		float x = Float.parseFloat(edata[1]), y = Float.parseFloat(edata[2]), z = Float.parseFloat(edata[3]); //coordinate
		Matrix4 matrix = new Matrix4(new Vector3(x, y, z), new Quaternion(0, 0, 0, 0), new Vector3(1, 1, 1));
		Entity ent = new Entity(asset.get(edata[0], Model.class), 1, matrix);
		float rx = Integer.parseInt(edata[4]), ry = Integer.parseInt(edata[5]), rz = Integer.parseInt(edata[6]); //applico rotazione
		ent.rotate(Integer.parseInt(edata[7]), new Vector3(rx, ry, rz));
		decorativeEntities.add(ent);
	  }
	  
	  //crea muri invisibili
	  String iw[] = reader.readLine().split(" ");
	  float x1 = Float.parseFloat(iw[0]), y1 = Float.parseFloat(iw[1]), z1 = Float.parseFloat(iw[2]), x2 = Float.parseFloat(iw[3]), y2 = Float.parseFloat(iw[4]), z2 = Float.parseFloat(iw[5]);
	  float len = Math.abs(x1 - x2);
	  float depth = Math.abs(z1 - z2);
	  float height = Math.abs(y1 - y2);
	  
	  entities.add(new PhysicsEntity(null, 0, new Matrix4(new Vector3(x1 + len/2, y1 + height/2, z1), new Quaternion(0,0,0,0), new Vector3(1,1,1)), 
			                         new btBoxShape(new Vector3(len/2, height/2, 5)), 0));
	  entities.add(new PhysicsEntity(null, 0, new Matrix4(new Vector3(x1 + len/2, y1 + height/2, z2), new Quaternion(0,0,0,0), new Vector3(1,1,1)), 
                                     new btBoxShape(new Vector3(len/2, height/2, 5)), 0));
	  entities.add(new PhysicsEntity(null, 0, new Matrix4(new Vector3(x1, y1 + height/2, z1 + depth/2), new Quaternion(0,0,0,0), new Vector3(1,1,1)), 
                                     new btBoxShape(new Vector3(5, height/2, depth/2)), 0));
	  entities.add(new PhysicsEntity(null, 0, new Matrix4(new Vector3(x2, y1 + height/2, z1 + depth/2), new Quaternion(0,0,0,0), new Vector3(1,1,1)), 
                                     new btBoxShape(new Vector3(5, height/2, depth/2)), 0));
	  entities.add(new PhysicsEntity(null, 0, new Matrix4(new Vector3(x1+len/2, y2, z1 + depth/2), new Quaternion(0,0,0,0), new Vector3(1,1,1)), 
                                     new btBoxShape(new Vector3(len/2, 5, depth/2)), 0));
	  
	  reader.close();
	}catch(Exception e){}
  }
  
  public void update(float deltaTime)
  {
	for(Entity e : decorativeEntities)
	 e.update(deltaTime);
	for(PhysicsEntity e : entities)
	 e.update(deltaTime);
  }
  
  public void render(ModelBatch batch)
  {
	for(PhysicsEntity e : entities)
	 e.render(batch);
	for(Entity e : decorativeEntities)
	 e.render(batch);
  }
  
  public ArrayList<PhysicsEntity> getEntities()
  {
	return entities;  
  }
  
  public ArrayList<Entity> getDecorativeEntities()
  {
	return decorativeEntities;  
  }
  
  public void dispose()
  {
	for(PhysicsEntity e : entities)
	 if(!(e instanceof Heightmap))	
	  e.dispose();
	for(Entity e : decorativeEntities)
	 e.dispose();
  }
}
