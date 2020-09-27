package it.MirkoGiacchini.world3d;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

/**
 * entità dinamica
 * @author Mirko
 *
 */
public class DynamicEntity extends PhysicsEntity 
{
  Vector3 direction, //direzione verso la quale si sta muovendo 
          velocity; //vettore velocità
  float speed; //intensità della velocità con la quale si muove
  public float rotDegY; //rotazione in gradi intorno asse y
  public float rotDegXZ; //rotazione su piano XZ
  
  public float lastRotationY, lastRotationXZ;
  
  public DynamicEntity(Model model, int nControllers, Matrix4 transform, float mass, CollShape shape, float...shapeArgs) 
  {
    this(-1, model, nControllers, transform, mass, shape, shapeArgs);
  } 
  
  public DynamicEntity(int userValue, Model model, int nControllers, Matrix4 transform, float mass, CollShape shape, float...shapeArgs) 
  {
    super(userValue, model, nControllers, transform, mass, shape, shapeArgs);
    direction = new Vector3(0, 0, 0);
    velocity = new Vector3(0, 0, 0);
    speed = 0;
    rotDegY = 0;
    rotDegXZ = 0;
    lastRotationY = 0;
    lastRotationXZ = 0;
    body.setAngularFactor(0); //impedisce la rotazione del modello durante la gestione delle collisioni
  } 
  
  @Override
  public void update(float deltaTime)
  {
	velocity.set(direction.x * speed, direction.y * speed, direction.z * speed);
	rotDegY %= 360;
	rotDegXZ %= 360;
	body.setLinearVelocity(velocity);
	
	getInstance().transform.rotate(new Vector3(-1, 0, 0), -lastRotationXZ);
	getInstance().transform.rotate(Vector3.Y, -lastRotationY);
	getInstance().transform.rotate(Vector3.Y, rotDegY);
	getInstance().transform.rotate(new Vector3(-1, 0, 0), rotDegXZ);
	
	lastRotationXZ = rotDegXZ;
	lastRotationY = rotDegY;
	getInstance().calculateTransforms();
	super.update(deltaTime);
  }
  
  /**ruota intorno y*/
  public void rotateY(float degrees) 
  { 
	setRotation(rotDegY + degrees, rotDegXZ);
  }
  
  /**ruota su piano xz*/
  public void rotateXZ(float degrees)
  {
	setRotation(rotDegY, rotDegXZ + degrees);
  }
 
  /**
   * setta rotazione
   * @param ry rotazione su y
   * @param rxz rotazione su xz
   */
  public void setRotation(float ry, float rxz)
  {
	/*rotate(-rotDegXZ, Vector3.Z.cpy().crs(Vector3.Y));
	rotate(-rotDegY, Vector3.Y);
	rotate(ry, Vector3.Y);
	rotate(rxz, Vector3.Z.cpy().crs(Vector3.Y));*/
	rotDegY = ry;
	rotDegXZ = rxz;
  }
  
  public void setSpeed(float speed)
  {
	this.speed = speed;  
  }
  
  public float getSpeed()
  {
	return speed;  
  }
  
  public float getRotDegY()
  {
	return rotDegY;  
  }
  
  public float getRotDegXZ()
  {
	return rotDegXZ;  
  }
  
  public void setDirection(float x, float y, float z)
  {
	direction.set(x, y, z);  
  }
  
  public void setDirection(Vector3 v)
  {
	direction.set(v);  
  }
  
  public void setDirectionX(float x)
  {
	direction.set(x, direction.y, direction.z);  
  }
  
  public void setDirectionY(float y)
  {
	direction.set(direction.x, y, direction.z);  
  }
  
  public void setDirectionZ(float z)
  {
	direction.set(direction.x, direction.y, z);  
  }
  
  public Vector3 getDirection()
  {
	return direction;  
  }
}
