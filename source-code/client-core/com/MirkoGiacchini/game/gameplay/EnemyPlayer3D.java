package com.MirkoGiacchini.game.gameplay;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.math.Matrix4;

import it.MirkoGiacchini.world3d.CollShape;

/**
 * player nemico
 * @author Mirko
 *
 */
public class EnemyPlayer3D extends Player3D
{ 
  float tmpRotY = 0, tmpRotXZ = 0; 
  
  public EnemyPlayer3D(int id, Model model, int nControllers, Matrix4 transform, float mass, int stats[], Gameplay gameplay, CollShape shape, float[] shapeArgs) 
  {
	super("Idle3", "MagicHand2", "deatch", id, model, nControllers, transform, mass, stats, gameplay, shape, shapeArgs);
  }
  
  @Override
  public void update(float deltaTime)
  {
	super.update(deltaTime);
	setRotation(tmpRotY, tmpRotXZ);
  }
  
  public void setRotY(float ry)
  {
	tmpRotY = ry;  
  }
  
  public void setRotXZ(float rxz)
  {
	tmpRotXZ = rxz;  
  }
}
