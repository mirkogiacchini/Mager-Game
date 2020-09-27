package com.MirkoGiacchini.game.gameplay;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Pool;

public class GSpellPool extends Pool<GraphicsSpell> implements Disposable
{
  ArrayList<GraphicsSpell> toDispose;
  Model model;
  int actSpell;
  
  public GSpellPool(Model model)
  {
	this.model = model;
	toDispose = new ArrayList<GraphicsSpell>();
	actSpell = 0;
  }
   
  @Override
  protected GraphicsSpell newObject() 
  {
	GraphicsSpell newSpell = new GraphicsSpell(model, CollisionObjValue.getSpellValue(actSpell));
	actSpell = (actSpell+1)%Integer.MAX_VALUE;
	toDispose.add(newSpell);
	return newSpell;
  }

  @Override
  public void dispose() 
  {
	for(GraphicsSpell gs : toDispose)
	 gs.dispose();
  }
}
