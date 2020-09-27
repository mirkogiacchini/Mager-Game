package com.MirkoGiacchini.game.gameplay;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Pool;

public class PFXPool extends Pool<ParticleEffect> implements Disposable
{
  ParticleEffect original;
  ArrayList<ParticleEffect> copies;
  
  public PFXPool(ParticleEffect original)
  {
	this.original = original;  
	copies = new ArrayList<ParticleEffect>();
  }
  
  @Override
  protected ParticleEffect newObject() 
  {
	ParticleEffect newp = original.copy();
	copies.add(newp);
	return newp;
  }
  
  @Override
  public void dispose() 
  {
	for(ParticleEffect p : copies)
	 p.dispose();
  }
}
