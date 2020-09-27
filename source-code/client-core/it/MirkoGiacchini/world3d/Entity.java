package it.MirkoGiacchini.world3d;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController.AnimationListener;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;

/**
 * entità 3d generica
 * @author Mirko
 *
 */
public class Entity 
{
  protected ModelInstance instance; //istanza
  protected AnimationController [] controllers; //controller per animazioni
  
  private Matrix4 transformTmp;
  
  public Entity(Model model, int nControllers, Matrix4 transform)
  {
	if(model != null)  
	 instance = new ModelInstance(model);
	
	if(transform != null)
	 if(instance != null) 	
	  instance.transform.set(transform);
	 else
	  transformTmp = transform;
	
	controllers = new AnimationController[nControllers];
	for(int i=0; i < nControllers; i++)
	{
	  controllers[i] = new AnimationController(instance);
	  controllers[i].setAnimation(null);
	}
  }
  
  public Entity(){}
  
  public void render(ModelBatch batch)
  {
	if(instance != null)  
 	 batch.render(instance); 
  }
  
  public void update(float deltaTime)
  {
	if(controllers != null)  
	 for(int i=0; i<controllers.length; i++)
	  controllers[i].update(deltaTime);
  }
  
  public void setAnimation(String animation)
  {
	setAnimation(animation, 1, -1, null, 0);  
  }
  
  /** animazione da nome e velocità, viene ripetuta in loop automaticamente sul primo controller*/
  public void setAnimation(String animation, float speed)
  {
	setAnimation(animation, speed, -1, null, 0);  
  }
  
  public void setAnimation(String animation, float speed, int loop)
  {
	setAnimation(animation, speed, loop, null, 0);  
  }
  
  public void setAnimation(String animation, float speed, int loop, AnimationListener listener)
  {
	setAnimation(animation, speed, loop, listener, 0);  
  }
  
  public void setAnimation(String animation, float speed, int loop, AnimationListener listener, int controller)
  {
	controllers[controller].setAnimation(animation, loop, speed, listener);  
  }
  
  public void changeAnimation(String animation)
  {
	changeAnimation(animation, 1, -1, null, 0);  
  }
  
  public void changeAnimation(String animation, float speed)
  {
	changeAnimation(animation, speed, -1, null, 0);  
  }
  
  public void changeAnimation(String animation, float speed, int loop, AnimationListener listener)
  {
	changeAnimation(animation, speed, loop, listener, 0);  
  }
  
  public void changeAnimation(String animation, float speed, int loop)
  {
	changeAnimation(animation, speed, loop, null, 0);  
  }
  
  public void changeAnimation(String animation, float speed, int loop, AnimationListener listener, int controller)
  {
	if(controllers[controller].current == null || controllers[controller].current.animation.id != animation)
	 setAnimation(animation, speed, loop, listener, controller);	
  }
  
  public void setEndAnimation(String animation)
  {
	setEndAnimation(animation, 0);  
  }
  
  public void setEndAnimation(String animation, int controller)
  {
	setAnimation(animation);
	controllers[controller].setAnimation(animation, controllers[controller].current.duration, -1, 1, 99999999, null);  
  }
  
  /**setta texture principale del modello*/
  public void setTexture(Texture texture)
  {
	if(instance.materials.size == 0)
	 addMaterial();
    instance.materials.get(0).set(TextureAttribute.createDiffuse(texture));  
  }
  
  public void addMaterial()
  {
	instance.materials.add(new Material());  
  }
  
  /**ruota un nodo del modello*/
  public void setNodeRotation(String node, float degrees, Vector3 axis)
  {
	instance.getNode(node).isAnimated = false;
	instance.getNode(node).rotation.set(axis, degrees);  
  }
  
  /**ruota il modello intero*/
  public void rotate(float degrees, Vector3 axis)
  {
	instance.transform.rotate(axis, degrees);  
	//instance.calculateTransforms();
  }
  
  /***
   * @return dimensioni bounding box
   */
  public Vector3 getDimensions()
  {
	BoundingBox b = new BoundingBox();  
	instance.calculateBoundingBox(b);
	Vector3 app = new Vector3();
	b.getDimensions(app);
	return app;  
  }
  
  public Vector3 getTranslation()
  {
	Vector3 vec = new Vector3();
	instance.transform.getTranslation(vec);
	return vec;
  }
  
  public Matrix4 getTransform()
  {
	if(instance != null)  
 	 return instance.transform;
	return transformTmp;
  }
  
  public ModelInstance getInstance()
  {
	return instance;  
  }
  
  public float getX()
  {
	return getTranslation().x;  
  }
  
  public float getY()
  {
	return getTranslation().y;  
  }
  
  public float getZ()
  {
	return getTranslation().z;  
  }
  
  public Array<Material> getMaterials()
  {
	return instance.materials;  
  }
  
  /**
   * distanza 2D tra due punti, quadrata
   */
  public float dst2D2(Vector3 p)
  {  
	Vector3 v = getTranslation().cpy();
	v.y = 0;
	return v.dst2(p.x, 0, p.z);  
  }
  
  public float dst2(Entity e)
  {
	return getTranslation().dst2(e.getTranslation());  
  }
  
  public void dispose()
  {
  }
}
