package it.MirkoGiacchini.world3d;

import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Vector3;

/**
 * camera in prima persona
 * @author Mirko
 *
 */
public class EntityCamera extends PerspectiveCamera
{
  DynamicEntity entity;
  float offsetHeight;
  float yRot;
  float xzRot;
  
  public EntityCamera(float width, float height)
  {
	this(width, height, 0);  
  }
  
  public EntityCamera(float width, float height, float offsetHeight)
  {
	super(67, width, height);  
	yRot = 0;
	xzRot = 0;
	this.offsetHeight = offsetHeight;
  }
  
  public void setEntity(DynamicEntity entity)
  {
	this.entity = entity;  
  }
  
  public DynamicEntity getEntity()
  {
	return entity;  
  }
  
  @Override
  public void update()
  {
	if(entity != null)
	{
	  Vector3 tmp = entity.getTranslation(); //muove telecamera
  	  position.set(tmp.x, tmp.y + offsetHeight, tmp.z); 
	  super.rotate(Vector3.Y, entity.getRotDegY() - yRot); //ruota y
	  yRot = entity.getRotDegY();
	  rotate(direction.cpy().crs(up), entity.getRotDegXZ() - xzRot); //ruota xz
	  xzRot = entity.getRotDegXZ();
	}
	super.update();  
  }
}
