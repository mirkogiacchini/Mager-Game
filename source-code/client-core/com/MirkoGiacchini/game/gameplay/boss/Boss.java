package com.MirkoGiacchini.game.gameplay.boss;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import com.MirkoGiacchini.game.Game;
import com.MirkoGiacchini.game.gameplay.CollisionObjValue;
import com.MirkoGiacchini.game.gameplay.EnemyPlayer3D;
import com.MirkoGiacchini.game.gameplay.Player3D;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

import it.MirkoGiacchini.pairing.Pair;
import it.MirkoGiacchini.world3d.CollShape;
import it.MirkoGiacchini.world3d.DynamicEntity;

/**
 * boss presente nella mappa
 * @author Mirko
 *
 */
public class Boss extends DynamicEntity
{
  Game game;
  
  int hp;
  public static final int MAX_HP = 500;
  float radius; //raggio entro il quale si può spostare per attaccare
  Matrix4 spawn;
  Vector3 spawnPoint;
  
  String idleAnim, attackAnim, runAnim, dieAnim;
  float atkSpeed;
  public static final float SPEED = 40f;//25; 
  public static final float ATK_ANIM_DUR = 0.7f;
  public static final float DAMAGE_MOMENT = 0.4f;
  public static final float DIST_TO_ATK = 15;
  public static final float DIST_TO_SPAWNP = 10;
  public static final int DAMAGE = 35;
  
  public static final int RESPAWN_TIME = 60;
  
  public static final float MASS = 2f;
  
  float lastR = 0;
  
  float dieAnimDur;
  
  long lastD = -1;
  
  boolean canDamage;
  
  /**danni subiti da ogni player*/
  HashMap<Integer, Integer> playerDamages; 
  
  public Boss(Game game, Model model, int nControllers, Matrix4 transform, float rad, String ia, String aa, String ra, String da, CollShape shape, float[] shapeArgs) 
  {
	super(CollisionObjValue.getBossValue(), model, nControllers, transform, MASS, shape, shapeArgs);
	this.game = game;
	spawn = transform;
	spawnPoint = new Vector3();
	spawn.getTranslation(spawnPoint);
	playerDamages = new HashMap<Integer, Integer>();
	respawn();
	radius = rad;
	idleAnim = ia;
	attackAnim = aa;
	runAnim = ra;
	dieAnim = da;
	
	setAnimation(attackAnim);
	atkSpeed = controllers[0].current.duration / ATK_ANIM_DUR;
	setAnimation(dieAnim);
	dieAnimDur = controllers[0].current.duration;
	setAnimation(null);
	
	setSpeed(SPEED);
  }
  
  public void respawn()
  {
	hp = MAX_HP;
	super.body.setWorldTransform(spawn);
    //rotDegY = rotDegXZ = 0;
	//lastRotationY = lastRotationXZ = 0;
	canDamage = true;
	//game.getGameplay().getWorld().add(this);
	playerDamages.clear();
  }
  
  public void update(float deltaTime, Player3D player, ConcurrentHashMap<Integer, EnemyPlayer3D> players)
  {
	super.update(deltaTime);   
	if(hp > 0)
	{
	  float min = Float.MAX_VALUE;

	  Pair<Integer, Float> ris = getClosest(min, -1, game.getClient().getId());
	  min = ris.b;
	  int idClosest = ris.a;
	
	  for(Entry<Integer, EnemyPlayer3D> e : players.entrySet())
	  {
        ris = getClosest(min, idClosest, e.getKey());
        idClosest = ris.a;
        min = ris.b;
	  } 
	
	  if(idClosest != -1) //c'è un player verso cui dirigersi
	  {	
	    if(min <= DIST_TO_ATK * DIST_TO_ATK) //posso attaccare
	    {
		  changeAnimation(attackAnim, atkSpeed);
		  float currentTime = controllers[0].current != null ? ATK_ANIM_DUR * controllers[0].current.time / controllers[0].current.duration : 0;
		  if(currentTime >= DAMAGE_MOMENT) //player riceve danni
		  {
			if(canDamage && game.getClient().isMaster())  
			 game.getGameplay().onPlayerHitByBoss(idClosest, Boss.DAMAGE);
			canDamage = false;
		  }
		  else
		   canDamage = true;
	    }
	    else //insegui il giocatore
  		 changeAnimation(runAnim);
	  
	    if(game.getGameplay().getPlayer(idClosest).dst2D2(this.getTranslation()) > DIST_TO_SPAWNP * DIST_TO_SPAWNP)
	     computeDirection(game.getGameplay().getPlayer(idClosest).getTranslation());
	    else
	     setDirection(0, 0, 0);
	  }
	  else 
	   if(spawnPoint.dst2(this.getTranslation()) > DIST_TO_SPAWNP * DIST_TO_SPAWNP)	//torno verso spawn point
	   {
	     computeDirection(spawnPoint);
	     changeAnimation(runAnim);
	   }
	   else //fermo
	   {
	     setDirection(0, 0, 0);
		 changeAnimation(idleAnim);
	   }
	}
	else
	{
	  /*if((System.currentTimeMillis() - lastD)/1000 <= dieAnimDur)	
	   changeAnimation(dieAnim, 1, 1);
	  else
	  {
		if(controllers[0].current != null && !controllers[0].current.animation.id.equals(dieAnim))  
	     setEndAnimation(dieAnim);
	    //game.getGameplay().getWorld().remove(this);
	  }*/
	  changeAnimation(dieAnim, 1, 1);
	  setDirection(0, 0, 0);
	}
  }
  
  private Pair<Integer, Float> getClosest(float min, int id1, int id2)
  {
	if(game.getGameplay().getPlayer(id2).dst2D2(spawnPoint) <= radius * radius && game.getGameplay().getPlayer(id2).getHp() > 0)
	{
	  float d = game.getGameplay().getPlayer(id2).getTranslation().dst2(this.getTranslation());	
	  if(d < min)
	   return new Pair<Integer, Float>(id2, d);
	}
	return new Pair<Integer, Float>(id1, min);
  }
  
  private void computeDirection(Vector3 p1)
  {
	Vector3 p2 = this.getTranslation();
	float d = (float)Math.sqrt(this.dst2D2(p1)); 
	float sin = (p1.z - p2.z) / d;
   	float cos = (p1.x - p2.x) / d;
	setDirection(cos, 0, sin);
	if(cos == 0) cos = 0.0000001f;
	float angle = ((float)Math.atan2(sin, cos)) * 180 / 3.14f;
  	rotateY(lastR - angle);
    lastR = angle;
  }
  
  public int getHp()
  {
	return hp;  
  }
  
  public void setHp(int hp)
  {
	this.hp = hp;  
  }
  
  public void setLastD(long lastD)
  {
	this.lastD = lastD;  
  }
  
  public long getLastD()
  {
	return lastD;  
  }
  
  /**aggiungo danni subiti da un player*/
  public void addDamagePlayer(int player, int damage)
  {
	if(!playerDamages.containsKey(player)) playerDamages.put(player, 0);
	playerDamages.put(player, playerDamages.get(player) + damage);
  }
  
  /**rimuovo i danni subiti dal player, avviene quando muore*/
  public void removeDamagePlayer(int player)
  {
	if(playerDamages.containsKey(player))
	 playerDamages.put(player, 0);
  }
  
  /**chi è il player che mi ha fatto più danni?*/
  public int getPlayerToBoost()
  {
	int min = -1;
	for(Entry<Integer, Integer> e : playerDamages.entrySet())
	 if(min == -1 || e.getValue() > playerDamages.get(min))
	  min = e.getKey();
    return min;
  }
  
  public HashMap<Integer, Integer> getPlayerDamages()
  {
	return playerDamages;  
  }
  
  public boolean canRespawn()
  {
	return (System.currentTimeMillis() - lastD)/1000 >= RESPAWN_TIME;  
  }
}
