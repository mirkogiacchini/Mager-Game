package com.MirkoGiacchini.game.gameplay;

import com.MirkoGiacchini.game.abstracted.GamePlayer;
import com.MirkoGiacchini.game.gameplay.items.IngameItem;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController.AnimationDesc;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController.AnimationListener;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

import it.MirkoGiacchini.world3d.CollShape;
import it.MirkoGiacchini.world3d.DynamicEntity;

/**
 * giocatore 3d
 * @author Mirko
 *
 */
public class Player3D extends DynamicEntity 
{
  private int angleOffset; //angolo in più generato da tasti o da analogStick per muoversi
  
  public static final float MAX_SP = 100;
  private static final float SP_USE_PER_SECOND = 35; //30;
  private static final float SP_REC_PER_SECOND = 15;
  private static final float FLY_SPEED = 1.8f;
  
  private static final float MAX_XZ_ROT = 65;
  private static final float MIN_XZ_ROT = -65;
  
  private float offHeight = 0;
  
  private float sp; //sp, necessari per volare
  private boolean flying; //sto volando?
  
  Spell spells[] = new Spell[6]; //6 magie
  public static final int DARK = 0, LIGHT = 1, FIRE = 2, ICE = 3, LIGHTNING = 4, CHAOS = 5;
  
  private Vector3 ptmp = new Vector3();
  
  private int hp, mana;
  private float perf, crit, boss;
  private float def[] = new float[6]; //difese alle magie
  public int DEF_HP = 100, DEF_MANA = 100;
  
  private static final float FIRE_DIST = 10;
  
  public static final float MIN_DIST = 15;
  
  Vector3 firingDirection;
  
  Gameplay gameplay;
  
  int id = 0;
  
  int actualSpell = 0;
  
  protected String idleAnim, fireAnim, dieAnim;
  
  boolean firing = false;
  
  /**incremento statistiche dato dal boss*/
  float boostBoss;
  
  private float fireAnimSpeed;
  private static final float TIME_FIRE_ANIM = .6f;
  private static final float TIME_TO_FIRE = .4f;
  
  private float dieAnimSpeed;
  private static final float TIME_DIE_ANIM = 1.f;
  
  int numKilled, numDeath;
  
  public static final float SPEED = 50f;//35f;
  public static final float PLAYER_OFF_HEIGHT = 3.f;
  
  String name;

  public Player3D(String idleA, String fireA, String dieA, int id, Model model, int nControllers, Matrix4 transform, float mass, int stats[], Gameplay gameplay, CollShape shape, float... shapeArgs) 
  {
	super(CollisionObjValue.getPlayerValue(id), model, nControllers, transform, mass, shape, shapeArgs);
	angleOffset = Integer.MAX_VALUE;
	sp = MAX_SP;
	flying = false;
	
	idleAnim = idleA;
	fireAnim = fireA;
	dieAnim = dieA;
	
	computeStats(stats);
	hp = DEF_HP;
	mana = DEF_MANA;
	
	this.gameplay = gameplay;
	
	this.id = id;
	
	setAnimation(fireAnim);
	fireAnimSpeed = controllers[0].current.duration / TIME_FIRE_ANIM;
	setAnimation(dieAnim);
	dieAnimSpeed = controllers[0].current.duration / TIME_DIE_ANIM;
	setAnimation(null);

	boostBoss = 1;
	
	numDeath = 0;
	numKilled = 0;
	
	setSpeed(SPEED);
 	if(!(this instanceof EnemyPlayer3D))
 	 setOffHeight(PLAYER_OFF_HEIGHT);
 	
	/*System.out.println(hp+" "+mana+" "+perf+" "+crit+" "+boss+" ");
	for(int i=0; i<6; i++) System.out.print(def[i]+" ");
	System.out.println();
	for(int i=0; i<6; i++)
	 System.out.println(spells[i].damage+" "+spells[i].crit+" "+spells[i].perf+" "+spells[i].manaNeed+" "+spells[i].recNeed+" "+spells[i].bossDmg);*/
  }
  
  public Player3D(int id, Model model, int nControllers, Matrix4 transform, float mass, int stats[], Gameplay gameplay, CollShape shape, float... shapeArgs)
  {
	this("Idle", "Swing02", "IdleWithWeapon", id, model, nControllers, transform, mass, stats, gameplay, shape, shapeArgs);  
  }
  
  public void computeStats(int stats[])
  {
	DEF_HP = (int)(100 * ((float)stats[GamePlayer.HP]/100+1));  
	perf = (float)stats[GamePlayer.PERF]/100+1;
	crit = (float)stats[GamePlayer.CRIT]/100+1;
	boss = (float)stats[GamePlayer.BOSS_DMG]/100+1;
	
	def[DARK] = (float)stats[GamePlayer.DARK+1]/100+1;
	def[LIGHT] = (float)stats[GamePlayer.LIGHT+1]/100+1;
	def[FIRE] = (float)stats[GamePlayer.FIRE+1]/100+1;
	def[ICE] = (float)stats[GamePlayer.ICE+1]/100+1;
	def[LIGHTNING] = (float)stats[GamePlayer.LIGHTNING+1]/100+1;
	def[CHAOS] = (float)stats[GamePlayer.CHAOS+1]/100+1;
	
	if(spells[DARK] == null) spells[DARK] = new Spell(); spells[DARK].computeStats(GamePlayer.DARK, stats);
	if(spells[LIGHT] == null) spells[LIGHT] = new Spell(); spells[LIGHT].computeStats(GamePlayer.LIGHT, stats);
	if(spells[FIRE] == null) spells[FIRE] = new Spell(); spells[FIRE].computeStats(GamePlayer.FIRE, stats);
	if(spells[ICE] == null) spells[ICE] = new Spell(); spells[ICE].computeStats(GamePlayer.ICE, stats);
	if(spells[LIGHTNING] == null) spells[LIGHTNING] = new Spell(); spells[LIGHTNING].computeStats(GamePlayer.LIGHTNING, stats);
	if(spells[CHAOS] == null) spells[CHAOS] = new Spell(); spells[CHAOS].computeStats(GamePlayer.CHAOS, stats);
  } 
  
  public void setOffHeight(float f)
  {
	offHeight = f;  
  }

  @Override
  public void update(float deltaTime)
  {
	if(getHp() > 0)
	{
	  if(flying) //se sto volando perdo SP
	  { 
	    sp = Math.max(0, sp - SP_USE_PER_SECOND * deltaTime);
	    if(sp == 0) stopFlying(); //sp finiti, non posso più volare
	  }
	  else //altrimenti ricarico sp
	   sp = Math.min(MAX_SP, sp + SP_REC_PER_SECOND * deltaTime);
	
	  if(firing && canFire())	  
	   changeAnimation(fireAnim, fireAnimSpeed);
	  
	  if(controllers[0].current != null && controllers[0].current.animation.id.equals(fireAnim))
	  {
		float currentTime = TIME_FIRE_ANIM * controllers[0].current.time / controllers[0].current.duration; //duration : TOT = ctime : x -> x = TOT * ctime / duration  
		if(currentTime >= TIME_TO_FIRE && canFire())
		{
		  fire();
		  gameplay.game.getMusicManager().playFire();
		}
		else
		 if(currentTime < TIME_TO_FIRE && !canFire())
		  setAnimation(idleAnim);
	  }
	  
	  if(!firing) changeAnimation(idleAnim);
	}
	else
	{
	  changeAnimation(dieAnim, dieAnimSpeed, 1, new AnimationListener()
	  {
		 @Override
		 public void onEnd(AnimationDesc animation) 
		 {
		   respawn(gameplay.map.getSpawnPoint());	 
		 }

		 @Override
	 	 public void onLoop(AnimationDesc animation) 
		 {
	  	 } 
	  });	
	  setDirection(0, 0, 0);
	}
	
    super.setRotation(super.getRotDegY(), Math.min(MAX_XZ_ROT, Math.max(MIN_XZ_ROT, super.getRotDegXZ()))); //limite alla rotazione su asse xz
	super.update(deltaTime);
	getInstance().calculateTransforms();
    ptmp = this.getTranslation();
	instance.transform.setTranslation(ptmp.x, ptmp.y + offHeight, ptmp.z);
  }
  
  public void startFlying()
  {
	setDirectionY(FLY_SPEED);
	flying = true;
  }
  
  public void stopFlying()
  {
	setDirectionY(0);
	flying = false;
  }
  
  public void setAngleOffset(int aof)
  {
	angleOffset = aof;  
  }
  
  public void recomputeDirectionXZ()
  {
	if(angleOffset != Integer.MAX_VALUE)  
	 setDirection(-(float)Math.sin((getRotDegY()+angleOffset)*3.14f/180), getDirection().y, -(float)Math.cos((getRotDegY()+angleOffset)*3.14f/180));  
	else
	 setDirection(0, getDirection().y, 0);
  }
  
  public boolean collides(IngameItem ii)
  {
	return (ii.getTranslation().dst2(getTranslation()) <= MIN_DIST * MIN_DIST);
  }
  
  /**
   * spara
   * return true se ha sparato, false altrimenti
   */
  public boolean fire()
  {
	if(canFire()) 
	{
      Matrix4 mtrx = getTransform().cpy();
      ptmp = getTranslation();
      mtrx.setTranslation(ptmp.x + firingDirection.x * FIRE_DIST, ptmp.y + firingDirection.y * FIRE_DIST, ptmp.z + firingDirection.z * FIRE_DIST); //trovo spawn della magia
      
      int sz = gameplay.gsPool.toDispose.size();
      GraphicsSpell newSpell = gameplay.gsPool.obtain();
      newSpell.init(mtrx, firingDirection, spells, id, actualSpell, gameplay);
	  gameplay.bspells.put(newSpell.usrv, newSpell); //aggiungo magia alla mappa 	
	  if(sz != gameplay.gsPool.toDispose.size()) gameplay.world.add(newSpell, (short)2, (short)1); //aggiungo magia al mondo
	  mana -= spells[actualSpell].manaNeed;	//decremento mana
	  spells[actualSpell].lastFired = System.currentTimeMillis(); //aggiorno ultimo sparo
	  return true;	
	}
	return false;
  }
  
  public boolean canFire()
  {
	return mana >= spells[actualSpell].manaNeed && System.currentTimeMillis() - spells[actualSpell].lastFired >= spells[actualSpell].recNeed;  
  }
  
  public void respawn(Vector3 spawnPoint)
  {
	Matrix4 tmp = body.getWorldTransform();
	tmp.setTranslation(spawnPoint);
	body.setWorldTransform(tmp);
	hp = DEF_HP;
	mana = DEF_MANA;
	sp = MAX_SP;
	boostBoss = 1;
  }
  
  public void setFiring(boolean f)
  {
	firing = f;  
  }
  
  public void setFiringDirection(Vector3 d)
  {
	firingDirection = d.cpy();  
  }
  
  /**incrementa la spell attuale da usare*/
  public void incrementSpell()
  {
	actualSpell = (actualSpell+1)%spells.length;
  }
  
  /**decrementa spell attuale da usare*/
  public void decrementSpell()
  {
	actualSpell = (actualSpell-1+spells.length)%spells.length;  
  }
  
  /** aumenta il boost del boss*/
  public void incBoostBoss()
  {
	boostBoss *= 2.f;  
  }
  
  public void setActualSpell(int sp)
  {
	actualSpell = sp;  
  }
  
  public int getActualSpell()
  {
	return actualSpell;  
  }
  
  public void setHp(int hp)
  {
	this.hp = hp;  
  }
  
  public void setMana(int mana)
  {
	this.mana = mana;  
  }
  
  public int getHp()
  {
	return hp;  
  }
  
  public int getMana()
  {
	return mana;  
  }
  
  public float getSp()
  {
	return sp;  
  }
  
  public float getBoostBoss()
  {
	return boostBoss;  
  }
  
  public void setBoostBoss(float bb)
  {
	boostBoss = bb;  
  }
  
  public int getDamage()
  {
	return spells[actualSpell].damage;  
  }
  
  public int getSpCrit()
  {
	return spells[actualSpell].crit;  
  }
  
  public int getSpPerf()
  {
	return spells[actualSpell].perf;  
  }
  
  public int getSpBossDmg()
  {
	return spells[actualSpell].bossDmg;  
  }
  
  public float getCrit()
  {
	return crit;  
  }
  
  public float getPerf()
  {
	return perf;  
  }
  
  public float getBossDmg()
  {
	return boss;  
  }
  
  public float getDef(int type)
  {
	return def[type];  
  }
  
  public int getTimeNeeded()
  {
	return spells[actualSpell].recNeed;  
  }
  
  public long getLastShoot()
  {
	return spells[actualSpell].lastFired;  
  }
  
  public void incKilled()
  {
	numKilled++;  
  }
  
  public void incDeath()
  {
	numDeath++;  
  }
  
  public int getNumKilled()
  {
	return numKilled;  
  }
  
  public int getNumDeath()
  {
	return numDeath;  
  }
  
  public void setNumKilled(int nk)
  {
	this.numKilled = nk;  
  }
  
  public void setNumDeaths(int nd)
  {
	numDeath = nd;  
  }
  
  public void setName(String s)
  {
	name = s;  
  }
  
  public String getName()
  {
	return name;  
  }
}
