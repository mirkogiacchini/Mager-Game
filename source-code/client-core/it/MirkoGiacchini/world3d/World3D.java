package it.MirkoGiacchini.world3d;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btBroadphaseInterface;
import com.badlogic.gdx.physics.bullet.collision.btCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.collision.btCollisionDispatcher;
import com.badlogic.gdx.physics.bullet.collision.btDbvtBroadphase;
import com.badlogic.gdx.physics.bullet.collision.btDefaultCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.collision.btDispatcher;
import com.badlogic.gdx.physics.bullet.dynamics.btConstraintSolver;
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btSequentialImpulseConstraintSolver;

/**
 * mondo fisico
 * @author Mirko
 *
 */
public class World3D 
{
  btCollisionConfiguration collisionConfig; //configurazione per collisioni
  btDispatcher dispatcher; 
  btBroadphaseInterface broadphase;
  btDynamicsWorld dynamicsWorld; //mondo dinamico
  btConstraintSolver constraintSolver; //classe usata per risolvere vincoli
	  
  public World3D(Vector3 gravity)
  {
	collisionConfig = new btDefaultCollisionConfiguration();  
	dispatcher = new btCollisionDispatcher(collisionConfig);
	broadphase = new btDbvtBroadphase();
	constraintSolver = new btSequentialImpulseConstraintSolver();
	dynamicsWorld = new btDiscreteDynamicsWorld(dispatcher, broadphase, constraintSolver, collisionConfig);
	dynamicsWorld.setGravity(gravity);
  }
  	  
  public void update(float deltaTime)
  {
	float minDelta = Math.min(deltaTime, 1/30f);  
	dynamicsWorld.stepSimulation(minDelta, 2, 1/60f); //aggiorna fisica con 1/60 (60 fps) 
  }
	  
  public void dispose()
  {
	dynamicsWorld.dispose();  
	collisionConfig.dispose();
	dispatcher.dispose();
	broadphase.dispose();
	constraintSolver.dispose();
  }
	
  public void add(PhysicsEntity entity, short flag, short collideWith)
  {
	dynamicsWorld.addRigidBody(entity.getBody(), flag, collideWith);
  }
  
  public void add(PhysicsEntity entity)
  {
	add(entity, (short)1, Short.MAX_VALUE);  
  }
  
  public void add(Map3D map)
  {
	for(PhysicsEntity e : map.entities)
	 add(e);
  }
  
  public void remove(PhysicsEntity entity)
  {
	dynamicsWorld.removeRigidBody(entity.getBody());  
  }
  
  public void remove(Map3D map)
  {
	for(PhysicsEntity e : map.entities)
	 remove(e);
  }
  
  public btDynamicsWorld getWorld()
  {
	return dynamicsWorld;  
  }
}
