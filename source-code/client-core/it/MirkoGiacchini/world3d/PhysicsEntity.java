package it.MirkoGiacchini.world3d;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btCapsuleShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.collision.btSphereShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody.btRigidBodyConstructionInfo;

/**
 * entità fisica
 * @author Mirko
 *
 */
public class PhysicsEntity extends Entity
{
  protected btRigidBody body;
  
  protected btCollisionShape shape;
  protected btRigidBodyConstructionInfo info;
  protected Vector3 inertia;
  
  protected Vector3 tmp = new Vector3();
  protected Matrix4 mtmp = new Matrix4();
  
  public int usrv;
  
  public static final int DEF_VALUE = -1;
  
  public PhysicsEntity(Model model, int nControllers, Matrix4 transform, btCollisionShape shape, float mass) 
  {
	this(DEF_VALUE, model, nControllers, transform, shape, mass);
  }
  
  public PhysicsEntity(int userValue, Model model, int nControllers, Matrix4 transform, btCollisionShape shape, float mass) 
  {
	super(model, nControllers, transform);
	this.shape = shape;
	createBody(userValue, this.shape, mass);
  }
  
  public PhysicsEntity(Model model, int nControllers, Matrix4 transform, float mass, CollShape shape, float...shapeArgs)
  {
	this(DEF_VALUE, model, nControllers, transform, mass, shape, shapeArgs);
  }
  
  public PhysicsEntity(int userValue, Model model, int nControllers, Matrix4 transform, float mass, CollShape shape, float...shapeArgs)
  {
	super(model, nControllers, transform);
	this.shape = getCollisionShape(shape, shapeArgs);
    createBody(userValue, this.shape, mass);
  }
  
  public PhysicsEntity(){}
  
  @Override
  public void update(float deltaTime)
  {
	super.update(deltaTime);
	body.getWorldTransform().getTranslation(tmp);	
	if(instance != null)
	 instance.transform.setTranslation(tmp);
	//body.getWorldTransform(instance.transform);
  }
  
  public btRigidBody getBody()
  {
	return body;  
  }
  
  protected void createBody(int userValue, btCollisionShape shape, float mass)
  {
	inertia = new Vector3(0,0,0);
	if(mass > 0)
	 shape.calculateLocalInertia(mass, inertia);
	info = new btRigidBodyConstructionInfo(mass, null, shape, inertia);
	info.setLinearSleepingThreshold(0); //evita che gli oggetti si blocchino dopo un certo tempo
	body = new btRigidBody(info);
	
	body.setUserValue(userValue);
    body.setCollisionFlags(body.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK);
	
    body.setWorldTransform(getTransform());
    
	usrv = userValue;
  }
  
  @Override
  public Vector3 getTranslation()
  {
	body.getWorldTransform().getTranslation(tmp);
	return tmp;
  }
  
  /**imposta traslazione del rigid body e quindi anche del modello*/
  public void setTranslation(Vector3 translation)
  {
	body.getWorldTransform(mtmp);
	mtmp.setTranslation(translation);
	body.setWorldTransform(mtmp);
  }
  
  public void setTransform(Matrix4 transform)
  {
	body.setWorldTransform(transform);
	if(instance != null) body.getWorldTransform(instance.transform);
  }
  
  private btCollisionShape getCollisionShape(CollShape shape, float...shapeArgs) //crea shape desiderato
  {
	Vector3 dim;  
	switch(shape)
	{
	  case MESH: //mesh collider... creo shape dal modello
	   return Bullet.obtainStaticNodeShape(instance.nodes); //non funziona con tutti i modelli
	  
	  case BOX: //box collider
	  {
	    if(shapeArgs.length == 0) //se non sono stati specificati parametri si prende metà del bounding box
	     dim = getDimensions();
	    else //altrimenti si usano i parametri passati
	 	 dim = new Vector3(shapeArgs[0], shapeArgs[1], shapeArgs[2]);
	    return new btBoxShape(new Vector3(dim.x/2, dim.y/2, dim.z/2));
	  }
	  
	  case CAPSULE: //capsula
	  {
	    float radius, height;
	    if(shapeArgs.length == 0) //se non ho parametri li prendo dal bounding box
	    {
		  dim = getDimensions();
		  radius = (dim.x + dim.z)/2;
		  height = dim.y;
	    }
	    else //altrimenti uso parametri
	    {
		  radius = shapeArgs[0];
		  height = shapeArgs[1];
	    }
	    return new btCapsuleShape(radius, height);
	  }
	  
	  case SPHERE: //sfera
	  {  
		float r;
		if(shapeArgs.length == 0)
		{
		  dim = getDimensions();
		  r = Math.max(dim.x, Math.max(dim.y, dim.z)) / 2;
		}
		else
		 r = shapeArgs[0];
		return new btSphereShape(r);
	  }
	}
	
	return null;
  }
  
  public void dispose()
  {
	body.dispose();  
	info.dispose();
	if(shape != null) shape.dispose();
  }
}
