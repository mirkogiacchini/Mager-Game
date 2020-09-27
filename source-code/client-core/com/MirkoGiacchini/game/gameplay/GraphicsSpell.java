package com.MirkoGiacchini.game.gameplay;

import com.MirkoGiacchini.game.AssetConstants;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Pool.Poolable;

import it.MirkoGiacchini.world3d.CollShape;
import it.MirkoGiacchini.world3d.DynamicEntity;

/**
 * magia grafica
 * @author Mirko
 *
 */
public class GraphicsSpell extends DynamicEntity implements Poolable
{ 	
  public static final float SPEED = 215;//200; //velocità 
  private static final float INF = 999999999f;
  Spell crt; //caratteristiche della spell
  int playerId; //id del player che l'ha effettuata
  
  ParticleEffect pfx;
  int type;
  
  private static final float MIN_MASS = 0.0000000001f;
	
  public GraphicsSpell(Model model, int usrv) 
  {
	super(usrv, model, 0, new Matrix4().idt(), MIN_MASS, CollShape.SPHERE);
	setSpeed(SPEED);
  }
  
  public void init(Matrix4 transform, Vector3 direction, Spell spell[], int id, int type, Gameplay gameplay)
  {
	setTransform(transform);
	setDirection(direction.cpy());
	crt = spell[type];
	playerId = id;  
	this.type = type;
	
	switch(type) //dò la giusta texture
	{
	  case Player3D.DARK: this.setTexture(gameplay.asset.get(AssetConstants.DARK_SPELL_T, Texture.class)); break;
	  case Player3D.LIGHT: this.setTexture(gameplay.asset.get(AssetConstants.LIGHT_SPELL_T, Texture.class)); break;
	  case Player3D.FIRE: this.setTexture(gameplay.asset.get(AssetConstants.FIRE_SPELL_T, Texture.class)); break;
	  case Player3D.ICE: this.setTexture(gameplay.asset.get(AssetConstants.ICE_SPELL_T, Texture.class)); break;
	  case Player3D.LIGHTNING: this.setTexture(gameplay.asset.get(AssetConstants.LIGHTNING_SPELL_T, Texture.class)); break;
	  case Player3D.CHAOS: this.setTexture(gameplay.asset.get(AssetConstants.CHAOS_SPELL_T, Texture.class)); break;
	}  
	    
	this.pfx = gameplay.pfxPool[type].obtain();
	this.pfx.init();
	gameplay.game.getParticleSystem().add(this.pfx);
  }
  
  @Override
  public void update(float delta)
  {
	super.update(delta);  
	pfx.setTransform(this.getTransform());
  }

  @Override
  public void reset() 
  {
	Matrix4 out = new Matrix4();
	out.idt();
	out.translate(-INF, -INF, -INF);
	this.setTransform(out);
  }
}
