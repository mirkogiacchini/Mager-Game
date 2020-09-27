package com.MirkoGiacchini.game.gameplay;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import com.MirkoGiacchini.game.AssetConstants;
import com.MirkoGiacchini.game.Game;
import com.MirkoGiacchini.game.GameState;
import com.MirkoGiacchini.game.MultiplayerProtocol;
import com.MirkoGiacchini.game.abstracted.GamePlayer;
import com.MirkoGiacchini.game.gameplay.boss.BossMap;
import com.MirkoGiacchini.game.gameplay.gr2d.ExPauseMenu;
import com.MirkoGiacchini.game.gameplay.gr2d.Hud;
import com.MirkoGiacchini.game.gameplay.items.IngameItem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.particles.ParticleEffect;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.ContactListener;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObjectWrapper;
import com.badlogic.gdx.physics.bullet.collision.btManifoldPoint;

import it.MirkoGiacchini.GameSocket.Server.Server;
import it.MirkoGiacchini.pairing.Triple;
import it.MirkoGiacchini.util.Util;
import it.MirkoGiacchini.world3d.CollShape;
import it.MirkoGiacchini.world3d.EntityCamera;
import it.MirkoGiacchini.world3d.World3D;

/**
 * gameplay del gioco
 * @author Mirko
 *
 */
public class Gameplay 
{
  Game game;
  AssetManager asset;
  
  /**stato del gameplay*/
  GameplayState state = GameplayState.PLAYING;
  
  /**mappa 3d del gameplay*/
  BossMap map;
  
  /**mondo fisico*/
  World3D world;
  
  /**giocatore controllato*/
  Player3D player;
  
  /**giocatori nemici*/
  ConcurrentHashMap<Integer, EnemyPlayer3D> enemies = new ConcurrentHashMap<Integer, EnemyPlayer3D>(); //key: id del client, obj: giocatore
  
  /**batch per modelli*/
  ModelBatch batch;
  
  /**camera per modelli*/
  EntityCamera camera3D;
  
  /**batch per sprite*/
  SpriteBatch batch2D;
  
  /**camera per sprite*/
  OrthographicCamera camera2D;
  
  /**hud del gioco (grafica 2d durante il gioco)*/
  Hud hud;
  
  /**menu per uscire e pausa*/
  ExPauseMenu expamenu;
  
  /**gestore dell'input*/
  GameplayInputProcessor inputProcessor;
  
  float defWidth, defHeight;
  
  /**quando è stato inviato l'ultimo update?*/
  long lastUpdSent = 0;
  
  /**verifica collisioni tra oggetti fisici*/
  GContactListener contactListener;
  
  /**update inviati ogni 20ms*/
  private static final int UPD_FRQ = 20;
  /**se un nemico si è spostato più di 2.5f dall'ultima posizione, viene teletrasportato*/
  private static final float DELTA_POS = 2.5f;
  
  /**magie nel gioco*/
  HashMap<Integer, GraphicsSpell> bspells = new HashMap<Integer, GraphicsSpell>();
  GSpellPool gsPool;
  
  private BitmapFont tmpFont;
  
  String myStats;
  
  static Random rand = new Random();
  Texture deathScreen;
  
  long startGame;
  public static final int GAME_DURATION = 5 * 60; //5 minuti (in secondi)
  
  long myStartTime;
  
  /**ultimi update ricevuti per i client*/
  HashMap<Integer, Long> updateTimes;
  /**ultimo update ricevuto per il boss*/
  long updateTimeBoss = -1;
  
  PFXPool pfxPool[] = new PFXPool[6];
  
  public Gameplay(Game game, float defWidth, float defHeight)
  {
	this.game = game;
	this.defWidth = defWidth;
	this.defHeight = defHeight;
	asset = game.getAsset();
	batch = new ModelBatch();
	camera3D = new EntityCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), AssetConstants.CAMERA_OFF_HEIGHT + Player3D.PLAYER_OFF_HEIGHT);
    camera3D.far = 2000;
    camera3D.near = 0.1f;

    batch2D = new SpriteBatch();
    camera2D = new OrthographicCamera();
    camera2D.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    
    tmpFont = new BitmapFont(Util.getHandle(AssetConstants.FONT_PATH)); tmpFont.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
    
    updateTimes = new HashMap<Integer, Long>();
  }
  
  /**metodo da chiamare quando inizia il gameplay*/
  public void begin(String mapdsc, GamePlayer gplayer)
  {
	if(world != null) world.dispose();  	
	if(map != null) map.dispose();
	if(player != null) player.dispose();
	if(contactListener != null) contactListener.dispose();
    game.getParticleSystem().removeAll();
	for(PFXPool p : pfxPool) if(p != null) p.dispose();
	if(gsPool != null) gsPool.dispose();
	
	for(Entry<Integer, GraphicsSpell>e : bspells.entrySet()) e.getValue().dispose();
	bspells.clear();
	
	for(Entry<Integer, EnemyPlayer3D>e : enemies.entrySet()) e.getValue().dispose();
	enemies.clear();
    
	createPools();
	
	world = new World3D(new Vector3(0, -1500f, 0));	
    setState(GameplayState.PLAYING);
    
    deathScreen = asset.get(AssetConstants.DEATH_SCREEN, Texture.class);
	contactListener = new GContactListener(); //considerato come una funzione globale (C++) 

	for(int i=0; i<AssetConstants.ROOMS_INFO.length; i++)
	 if(mapdsc.equals(AssetConstants.ROOMS_INFO[i]))
	 {
	   try //vedo se è una mappa "speciale" che richiede una classe a se (prendo la classe dal suo nome)
	   {
	     @SuppressWarnings("unchecked")
		 Class<? extends BossMap> c = (Class<? extends BossMap>)Class.forName("com.MirkoGiacchini.game.gameplay.maps.Map"+i);
	     map = c.getConstructor(FileHandle.class, FileHandle.class, FileHandle.class, FileHandle.class, FileHandle.class, AssetManager.class, Game.class, Gameplay.class).
	    	   newInstance(Util.getHandle(AssetConstants.MAP_PATH+"boss"+i+".bs"), Util.getHandle(AssetConstants.MAP_PATH+"spawn"+i+".swn"), 
	    			       Util.getHandle(AssetConstants.MAP_PATH+"heightmap"+i+".hmap"),
	    			       Util.getHandle(AssetConstants.MAP_PATH+"map"+i+".map"), Util.getHandle(AssetConstants.MAP_PATH+"item"+i+".itm"), asset, game, this);
	   }
	   catch(Exception e) //se non lo è allora uso quella standard
	   {	
	     map = new BossMap(Util.getHandle(AssetConstants.MAP_PATH+"boss"+i+".bs"), Util.getHandle(AssetConstants.MAP_PATH+"spawn"+i+".swn"),
	    		           Util.getHandle(AssetConstants.MAP_PATH+"heightmap"+i+".hmap"),
	    		           Util.getHandle(AssetConstants.MAP_PATH+"map"+i+".map"), Util.getHandle(AssetConstants.MAP_PATH+"item"+i+".itm"), asset, game, this);		
	   }
	   world.add(map);
	 }
    
	int iStats[] = new int[16];
	myStats = "";
	for(int i=0; i<16; i++) 
	{
	  iStats[i] = gplayer.stats[i].value;
	  myStats += iStats[i]+" ";
	}
	myStats = myStats.trim();
	
	Matrix4 transform = new Matrix4(new Vector3(25, 25, 25), new Quaternion(0, 1, 0, 0), new Vector3(1, 1, 1));
	player = new Player3D(game.getClient().getId(), asset.get(AssetConstants.PLAYER_ARMS, Model.class), 1, transform, AssetConstants.PLAYER_MASS, iStats, this, CollShape.CAPSULE, AssetConstants.PLAYER_CAPSULE_ARGS);
	player.setName(game.getMenuController().getPlayer().getName());
	player.setTexture(asset.get(AssetConstants.PLAYER_ARMS_TEXTURE, Texture.class));

	hud = new Hud(asset, defWidth, defHeight, tmpFont, player, this)
	{
	  @Override
	  public void onFlyButtonPressed() //premuto tasto per volare su android
	  {
		player.startFlying();
	  }
	  
	  @Override
	  public void onFlyButtonReleased()
	  {
	    player.stopFlying();  
	  }
	  
	  @Override
	  public void onExitButtonReleased()
	  {
		Gameplay.this.setState(GameplayState.EX_PAUSE_MENU);  
	  }
	};
	
	world.add(player);
	player.respawn(map.getSpawnPoint());
	camera3D.setEntity(player);
	startGame = -1;
	myStartTime = System.currentTimeMillis();
	if(game.getClient().isMaster()) setStartTime(myStartTime);
	game.getMenuController().getEndGameMenu().reset();
	Gdx.input.setInputProcessor((inputProcessor = new GameplayInputProcessor(this, AssetConstants.INV_SENSIBILITY, AssetConstants.INV_SENSIBILITY_ANDROID)));
  }
  
  /**update gameplay*/
  public void update()
  {	  
	player.setFiringDirection(camera3D.direction);
	player.update(Gdx.graphics.getDeltaTime());
	
	map.update(Gdx.graphics.getDeltaTime());
	for(Entry<Integer, EnemyPlayer3D> e : enemies.entrySet())
	 e.getValue().update(Gdx.graphics.getDeltaTime());
    for(Entry<Integer, GraphicsSpell> e : bspells.entrySet())
     e.getValue().update(Gdx.graphics.getDeltaTime());   
	hud.update(player, enemies, map.getBoss(), camera3D);
    camera3D.update();
    camera2D.update();
    
    if(game.getClient().isMaster()) 
    {
      for(Entry<Integer, IngameItem> e : map.getItems().entrySet()) //invio aggiornamenti degli item
      {
        if(player.collides(e.getValue()) && !e.getValue().isTaken() && player.getHp() > 0)
        {
          e.getValue().onTaken(player);	
          game.getClient().sendObjectTcp(MultiplayerProtocol.ITEM_TAKEN+" "+e.getKey()+" "+game.getClient().getId()+" "+e.getValue().getLast());
          hud.addItemMessage(e.getKey(), game.getClient().getId());
        }
        
        for(Entry<Integer, EnemyPlayer3D> ep : enemies.entrySet())
         if(ep.getValue().collides(e.getValue()) && !e.getValue().isTaken() && ep.getValue().getHp() > 0)
         {
           e.getValue().onTaken(ep.getValue());	 
           game.getClient().sendObjectTcp(MultiplayerProtocol.ITEM_TAKEN+" "+e.getKey()+" "+ep.getKey()+" "+e.getValue().getLast());
           hud.addItemMessage(e.getKey(), ep.getKey());
         }
      }
    }

    //System.out.println(player.getTranslation());
    // System.gc(); //forza garbage-collector (SOLO DEBUG!!!!)
    
    if(System.currentTimeMillis() - lastUpdSent >= UPD_FRQ) //devo rinviare l'update del mio stato
    {
      game.getClient().sendDataUdp(new String(game.getClient().getId()+" "+MultiplayerProtocol.PLAYER_UPDATE+" "+
                                              Util.round(player.getTranslation().x, 2)+" "+Util.round(player.getTranslation().y, 2)+" "+Util.round(player.getTranslation().z, 2)+" "+
                                              player.getDirection().x+" "+player.getDirection().y+" "+player.getDirection().z+" "+
                                              Util.round(player.getRotDegY(), 2)+" "+Util.round(player.getRotDegXZ(), 2)+" "+player.firing+" "+player.actualSpell+" "+
                                              Util.round(player.firingDirection.x, 2)+" "+Util.round(player.firingDirection.y, 2)+" "+Util.round(player.firingDirection.z, 2)+" "+
                                              player.getHp()+" "+player.getMana()+" "+player.getName()+" "+player.getNumKilled()+" "+player.getNumDeath()+" "+System.currentTimeMillis()).getBytes()); 	
      if(game.getClient().isMaster()) //sono master... devo inviare anche gli aggiornamenti del boss
      {
    	if(map.getBoss().canRespawn() && map.getBoss().getHp() <= 0) map.getBoss().respawn();  
        game.getClient().sendDataUdp(new String(game.getClient().getId()+" "+MultiplayerProtocol.BOSS_UPDATE+" "+
    	 	                                    Util.round(map.getBoss().getX(), 2)+" "+Util.round(map.getBoss().getY(), 2)+" "+Util.round(map.getBoss().getZ(), 2)+" "+
    	 	                                    map.getBoss().getRotDegY()+" "+ Util.round(map.getBoss().getDirection().x, 2)+" "+
    		                                    Util.round(map.getBoss().getDirection().y, 2)+" "+Util.round(map.getBoss().getDirection().z, 2)+" "+
    	 	                                    map.getBoss().getHp()+" "+map.getBoss().getLastD()+" "+System.currentTimeMillis()).getBytes());
      }
      lastUpdSent = System.currentTimeMillis(); 	
    }  
    
    game.getParticleSystem().update();
    world.update(Gdx.graphics.getDeltaTime());
    
	if(getState() == GameplayState.EX_PAUSE_MENU)
	 if(expamenu == null) 
	  expamenu = new ExPauseMenu(defWidth, defHeight, asset, game);
      
	if(startGame != -1 && (System.currentTimeMillis() - startGame)/1000 >= GAME_DURATION) //gioco finito
	{
	  game.getClient().changeRoom(Server.DEFAULT_ROOM);    
	  game.setState(GameState.END_GAME);	
	  game.getMenuController().getEndGameMenu().start(player.getNumKilled(), player.getNumDeath(), GAME_DURATION - ((myStartTime - startGame)/1000), game);
	}
  }
  
  /**stampa a video*/
  public void render()
  {
	Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	Gdx.gl.glClearColor(0f, 0f, 0f, 1.f);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT); 
    
    game.getParticleSystem().begin();
    game.getParticleSystem().draw();
    game.getParticleSystem().end();
    
    batch.begin(camera3D);
     map.render(batch);
     player.render(batch);
     for(Entry<Integer, EnemyPlayer3D> e : enemies.entrySet())
      e.getValue().render(batch);
     for(Entry<Integer, GraphicsSpell> e : bspells.entrySet())
      e.getValue().render(batch);
     batch.render(game.getParticleSystem());
    batch.end();
    
    batch2D.setProjectionMatrix(camera2D.combined);
    batch2D.begin();
     if(getState() == GameplayState.PLAYING)
      hud.render(player, enemies, map.getBoss(), camera3D, batch2D);
     else
      if(expamenu != null)	 
       expamenu.draw(batch2D);
     if(player.getHp() <= 0)
      batch2D.draw(deathScreen, 0, 0, defWidth, defHeight);
     
    batch2D.end();
  }
  
  public synchronized void udpReceived(byte[]data) //ricevuto messaggio udp non standard -> serve per gameplay
  {
	String str = new String(data).trim();
	String strs[] = str.split(" ");
	
	synchronized(enemies)
	{
	  synchronized(world)
	  {
	    if(strs[1].equals(MultiplayerProtocol.PLAYER_UPDATE)) //devo aggiornare il player
  	  	{ 
		  int id = Integer.parseInt(strs[0]); 	
		  long time = Long.parseLong(strs[strs.length-1]);
		  if(updateTimes.get(id) != null && updateTimes.get(id) > time) return; //arrivato messaggio vecchio... lo scarto
		  updateTimes.put(id, time);
		  
	      Vector3 pos = new Vector3(Float.parseFloat(strs[2]), Float.parseFloat(strs[3]), Float.parseFloat(strs[4]));
	      Vector3 dir = new Vector3(Float.parseFloat(strs[5]), Float.parseFloat(strs[6]), Float.parseFloat(strs[7]));
	      Vector3 firingDir = new Vector3(Float.parseFloat(strs[12]), Float.parseFloat(strs[13]), Float.parseFloat(strs[14]));
	      if(enemies.containsKey(id)) //l'avevo già aggiunto precedentemente...
	      {
	    	if(pos.dst2(enemies.get(id).getTranslation()) > DELTA_POS * DELTA_POS) //distanze troppo ampie... conviene teletrasportare
	    	 enemies.get(id).setTranslation(pos);
	      }
	      else //devo aggiungerlo
	      {
	    	game.getClient().sendObjectTcp(MultiplayerProtocol.SEND_STATS+" "+game.getClient().getId()+" "+myStats); //è entrato un nuovo player... gli invio le mie statistiche  
	        Matrix4 transform = new Matrix4(pos, new Quaternion(0, 0, 0, 0), new Vector3(1, 1, 1)); //stato iniziale
	        int stats[] = new int[16];
	        for(int i=0; i<16; i++) stats[i] = 0;
	        enemies.put(id, new EnemyPlayer3D(id, asset.get(AssetConstants.ENEMY_PLAYER, Model.class), 1, transform, 
				                              AssetConstants.PLAYER_MASS, stats, this, CollShape.CAPSULE, AssetConstants.PLAYER_CAPSULE_ARGS)); 
	        world.add(enemies.get(id));
	        
	        if(game.getClient().isMaster()) //è entrato un nuovo giocatore... devo mandargli lo stato attuale degli item, il tempo di inizio gioco e lo stato del boss
	        {
	          String itmupd = MultiplayerProtocol.RESET_ITEMS+" "+id;
	          for(Entry<Integer, IngameItem>e : map.getItems().entrySet())
	           itmupd += (" "+e.getKey()+" "+e.getValue().getLast());
	          game.getClient().sendObjectTcp(itmupd);  
	          game.getClient().sendObjectTcp(MultiplayerProtocol.START_GAME_TIME+" "+id+" "+(System.currentTimeMillis() - startGame));
	          String bossState = ""+map.getBoss().getPlayerDamages().size();
	          for(Entry<Integer, Integer> e : map.getBoss().getPlayerDamages().entrySet())
	           bossState += " "+e.getKey()+" "+e.getValue();
	          game.getClient().sendObjectTcp(MultiplayerProtocol.BOSS_INIT + " " + id + " " + bossState);
	        }
		    
	        enemies.get(id).setName(strs[17]);
	      }
	      enemies.get(id).setDirection(dir);
	      enemies.get(id).setRotY(Float.parseFloat(strs[8]));
	      enemies.get(id).setRotXZ(-Float.parseFloat(strs[9]));
	      enemies.get(id).setFiring(Boolean.parseBoolean(strs[10]));
	      enemies.get(id).setActualSpell(Integer.parseInt(strs[11]));
	      enemies.get(id).setFiringDirection(firingDir);
	      enemies.get(id).setHp(Integer.parseInt(strs[15]));
	      enemies.get(id).setMana(Integer.parseInt(strs[16]));
	      enemies.get(id).setNumKilled(Integer.parseInt(strs[18]));
	      enemies.get(id).setNumDeaths(Integer.parseInt(strs[19]));
	      return;
  	  	}
	    
	    if(strs[1].equals(MultiplayerProtocol.BOSS_UPDATE)) //aggiorno boss
	    {
	      long time = Long.parseLong(strs[strs.length-1]);
	      if(updateTimeBoss > time) return; //update vecchio... lo scarto
	      time = updateTimeBoss;
	      
	      Vector3 pos = new Vector3(Float.parseFloat(strs[2]), Float.parseFloat(strs[3]), Float.parseFloat(strs[4]));	
	      Vector3 dir = new Vector3(Float.parseFloat(strs[6]), Float.parseFloat(strs[7]), Float.parseFloat(strs[8]));
	      if(pos.dst2(map.getBoss().getTranslation()) > DELTA_POS * DELTA_POS) //troppo lontani... meglio teletrasportare
	       map.getBoss().setTranslation(pos);
	      map.getBoss().setDirection(dir);
	      map.getBoss().setRotation(Float.parseFloat(strs[5]), map.getBoss().getRotDegXZ());
	      map.getBoss().setHp(Integer.parseInt(strs[9]));
	      map.getBoss().setLastD(Long.parseLong(strs[10]));
	      if(map.getBoss().getHp() < 0 && map.getBoss().canRespawn()) map.getBoss().respawn();
	      return;
	    }
	  }
	}
  }
  
  public synchronized void tcpReceived(Object obj) //ricevuto messaggio tcp non standard -> serve per gameplay
  {
	if(obj instanceof String)
	{
	  String strs[] = ((String)obj).split(" ");
	  if(strs[0].equals(MultiplayerProtocol.REMOVE_PLAYER_FROM_GAME)) //devo rimuovere un client dal gioco
	  {
		world.remove(enemies.get(Integer.parseInt(strs[1])));  
	    enemies.remove(Integer.parseInt(strs[1]));
	  }
	  
	  if(strs[0].equals(MultiplayerProtocol.ITEM_TAKEN)) //qualcuno ha preso un item
	  {  
		int id = Integer.parseInt(strs[2]);  
		map.getItems().get(Integer.parseInt(strs[1])).onTaken( (id == game.getClient().getId() ? player : enemies.get(id)) );
		map.getItems().get(Integer.parseInt(strs[1])).setLast(Long.parseLong(strs[3]));
		
		hud.addItemMessage(Integer.parseInt(strs[1]), id);
	  }
	  
	  if(strs[0].equals(MultiplayerProtocol.RESET_ITEMS)) //mi viene comunicato lo stato dei vari item
	  {
		for(int i=2; i<strs.length; i+=2)
		{
		  map.getItems().get(Integer.parseInt(strs[i])).setLast(Long.parseLong(strs[i+1]));
		  map.getItems().get(Integer.parseInt(strs[i])).setTaken(true);
		}
		game.getClient().sendObjectTcp(MultiplayerProtocol.SEND_STATS+" "+game.getClient().getId()+" "+myStats); //invio statistiche... mi hanno aggiunto
	  }
	  
	  if(strs[0].equals(MultiplayerProtocol.SEND_STATS)) //mi hanno comunicato le statistiche di un giocatore... le ricalcolo
	  {
		int stats[] = new int[16];
		for(int i=0; i<16; i++) stats[i] = Integer.parseInt(strs[i+2]);
		enemies.get(Integer.parseInt(strs[1])).computeStats(stats);   
	  }
	  
	  if(strs[0].equals(MultiplayerProtocol.START_GAME_TIME)) //quando è iniziata la partita?
	   setStartTime(System.currentTimeMillis() - Long.parseLong(strs[2]));

	  if(strs[0].equals(MultiplayerProtocol.HIT)) //qualcuno è stato colpito
	   onHit(Integer.parseInt(strs[1]), Integer.parseInt(strs[2]), Integer.parseInt(strs[3]));
	  
	  if(strs[0].equals(MultiplayerProtocol.BOSS_HIT)) //boss colpito
	   onBossHit(Integer.parseInt(strs[1]), Integer.parseInt(strs[2]));
	  
	  if(strs[0].equals(MultiplayerProtocol.BOSS_HIT_PLAYER)) //player colpito dal boss
	   onPlayerHitByBoss(Integer.parseInt(strs[1]), Integer.parseInt(strs[2]));
		
	  if(strs[0].equals(MultiplayerProtocol.BOSS_INIT)) //inizializzo boss
	  {
		int N = Integer.parseInt(strs[2]);
		for(int i=0; i<N; i++)
		{
		  int pid = Integer.parseInt(strs[3+2*i]);
		  int dmg = Integer.parseInt(strs[4+2*i]);
		  map.getBoss().addDamagePlayer(pid, dmg);
		}
	  }
	  
	  if(strs[1].equals(MultiplayerProtocol.PLAYER_UPDATE)) //update arrivato da tcp... 
	   udpReceived(((String)obj).getBytes());
	  
	  if(strs[1].equals(MultiplayerProtocol.BOSS_UPDATE)) //update boss arrivato tramite tcp..
	   udpReceived(((String)obj).getBytes());
	}
  }
  
  public void setState(GameplayState s)
  {
	switch(s)
	{
	  case PLAYING: Gdx.input.setCursorCatched(true); break;
	  case EX_PAUSE_MENU: Gdx.input.setCursorCatched(false); break;
	}
	state = s;  
  }
  
  public void setStartTime(long l)
  {
	startGame = l;
	hud.setStartTime(l);
  }
  
  public GameplayState getState()
  {
	return state;  
  }
  
  public ExPauseMenu getExPauseMenu()
  {
	return expamenu;  
  }
  
  public Player3D getPlayer()
  {
	return player;  
  }
  
  class GContactListener extends ContactListener //Bullet considera il metodo come una funzione globale, non serve passarla a nessuno
  {
	@Override
	public boolean onContactAdded(btManifoldPoint cp, btCollisionObjectWrapper obj0, int partId0, int index0, btCollisionObjectWrapper obj1, int partId1, int index1)
	{
      int usr0 = obj0.getCollisionObject().getUserValue();
      int usr1 = obj1.getCollisionObject().getUserValue();
      
	  if(bspells != null)
	  {
		if(usr0 >= 0 && !bspells.containsKey(usr0)) return true; 
		if(usr1 >= 0 && !bspells.containsKey(usr1)) return true;
		
		//c'è una spell e un giocatore... verifico collisioni (devo essere il master della stanza)
		if( (usr0 >= 0 || usr1 >= 0) && (CollisionObjValue.isPlayerValue(usr0) || CollisionObjValue.isPlayerValue(usr1)) && game.getClient().isMaster() ) 
		{	
		  int playerId = usr0 >= 0 ? CollisionObjValue.getPlayerId(usr1) : CollisionObjValue.getPlayerId(usr0); //id giocatore colpito
		  int spellId = usr0 >= 0 ? CollisionObjValue.getSpellId(usr0) : CollisionObjValue.getSpellId(usr1); //id spell
		  int fplayerId = bspells.get(spellId).playerId; //id del giocatore che ha sparato
		  
		  if(fplayerId != playerId) //collisione con un altro giocatore... bisogna calcolare i danni
		  {
			int damage = getDamage(fplayerId, playerId, false);
			game.getClient().sendObjectTcp(MultiplayerProtocol.HIT+" "+fplayerId+" "+playerId+" "+damage); //invio a tutti il danno
			onHit(fplayerId, playerId, damage); //a me non arriva il broadcast... devo gestire subito la situazione
		  }
		  else
		   return true;
		}
		
		//collisione tra spell e boss
		if( (usr0 >= 0 || usr1 >= 0) && (CollisionObjValue.isBossValue(usr0) || CollisionObjValue.isBossValue(usr1)) && game.getClient().isMaster() && map.getBoss().getHp() > 0 )
		{
		  int spellId = usr0 >= 0 ? CollisionObjValue.getSpellId(usr0) : CollisionObjValue.getSpellId(usr1); //id spell
		  int fplayerId = bspells.get(spellId).playerId;
		  int damage = getDamage(fplayerId, -1, true);
		  game.getClient().sendObjectTcp(MultiplayerProtocol.BOSS_HIT+" "+fplayerId+" "+damage);
		  onBossHit(fplayerId, damage);
		  if(map.getBoss().getHp() <= 0) map.getBoss().setLastD(System.currentTimeMillis());
		}
		
		//tolgo le spell
		if(usr0 >= 0 && bspells.get(CollisionObjValue.getSpellId(usr0)).playerId != CollisionObjValue.getPlayerId(usr1)) 
	    {
		  game.getParticleSystem().remove(bspells.get(usr0).pfx);
		  pfxPool[bspells.get(usr0).type].free(bspells.get(usr0).pfx);
		  gsPool.free(bspells.get(usr0));
		  bspells.remove(usr0); 
		}
		
		if(usr1 >= 0 && bspells.get(CollisionObjValue.getSpellId(usr1)).playerId != CollisionObjValue.getPlayerId(usr0)) 
		{
		  game.getParticleSystem().remove(bspells.get(usr1).pfx);
		  pfxPool[bspells.get(usr1).type].free(bspells.get(usr1).pfx);
		  gsPool.free(bspells.get(usr1));
		  bspells.remove(usr1); 
		}
	  }
	  return true;
	}
  }
  
  public Player3D getPlayer(int id)
  {
	if(id == game.getClient().getId())
	 return player;
	if(!enemies.containsKey(id)) return null;
	return enemies.get(id);
  }
  
  /**
   * da chiamare quando un player colpisce un altro player
   * @param fid id di chi ha sparato
   * @param cid id di chi è stato colpito
   * @param dmg danno
   */
  private void onHit(int fid, int cid, int dmg)
  { 
	if(getPlayer(cid).getHp() > 0) //non ho colpito uno già morto
	{
	  getPlayer(cid).setHp(getPlayer(cid).getHp() - dmg); 
	  
	  if(getPlayer(cid).getHp() <= 0)
	  {
	    getPlayer(fid).incKilled(); 	 
	    getPlayer(cid).incDeath();	 
	    game.getMusicManager().playDeath();
	    Color c = (fid == player.id) ? AssetConstants.MESSAGES_COLOR_GOOD : (cid == player.id) ? AssetConstants.MESSAGES_COLOR_BAD : AssetConstants.MESSAGES_COLOR_NEUTRAL;  
	    hud.messages.add(new Triple<String, Color, Long>(getPlayer(fid).getName()+" fragged "+getPlayer(cid).getName(), c, System.currentTimeMillis()));
	  }
	  else
	   game.getMusicManager().playHit();
	}
  }
  
  /**
   * da chiamare quando il player colpisce il boss
   * @param pid id del player
   * @param dmg danni fatti al boss
   */
  private void onBossHit(int pid, int dmg)
  {
	 map.getBoss().setHp(map.getBoss().getHp() - dmg);
	 map.getBoss().addDamagePlayer(pid, dmg);
	 if(map.getBoss().getHp() <= 0)
	 {
	   int indb = map.getBoss().getPlayerToBoost();
	   getPlayer(indb).incBoostBoss();   
	   game.getMusicManager().playDeath();   
	   Color c = (indb == player.id) ? AssetConstants.MESSAGES_COLOR_GOOD : AssetConstants.MESSAGES_COLOR_NEUTRAL;
	   hud.messages.add(new Triple<String, Color, Long>(getPlayer(indb).getName()+" killed the boss! boost acquired!", c, System.currentTimeMillis()));
	 }	  
	 else
	  game.getMusicManager().playHit();
  }
  
  /**
   * da chiamare quando il player viene colpisco dal boss
   * @param pid id del player
   * @param dmg danni fatti al player
   */
  public void onPlayerHitByBoss(int pid, int dmg)
  {
	getPlayer(pid).setHp(getPlayer(pid).getHp() - dmg);
	if(getPlayer(pid).getHp() <= 0) //player ucciso da boss
	{
	  getPlayer(pid).incDeath();
	  map.getBoss().removeDamagePlayer(pid); //rimuovo tutti i danni che mi aveva fatto
	  game.getMusicManager().playDeath();
	}
	else
	 game.getMusicManager().playBossAttack();
	if(game.getClient().isMaster())
     game.getClient().sendObjectTcp(MultiplayerProtocol.BOSS_HIT_PLAYER+" "+pid+" "+dmg);
  }
  
  private int getDamage(int fplayerId, int playerId, boolean boss)
  {
	float critDmg = (rand.nextInt(Math.max(getPlayer(fplayerId).getSpCrit(), 1))+1) * getPlayer(fplayerId).getCrit(); //critico = danno critico * potenza critica
	float perfDmg = (rand.nextInt(Math.max(getPlayer(fplayerId).getSpPerf(), 1))+1) * getPlayer(fplayerId).getPerf(); //perforante = danno perf * potenza perf
	float dmg = getPlayer(fplayerId).getDamage() * getPlayer(fplayerId).getBoostBoss() * critDmg; //danno = dannoSpell * boostBoss * critico
	float res = 1;

	if(!boss) //solo i giocatori hanno resistenza
	 res = Math.max(getPlayer(playerId).getDef(getPlayer(fplayerId).getActualSpell()) / perfDmg, 0.4f); //resistenza = difesa / perforanti (può essere al minimo 0.4f)
	else
	 dmg += (getPlayer(fplayerId).getBossDmg() * getPlayer(fplayerId).getSpBossDmg()); //se ho colpito il boss considero anche i danni boss
	
	int damage = Util.round(dmg / res); //danno totale = danno / resistenza  
	
	//System.out.println(critDmg+" "+perfDmg+" "+dmg+" "+res+" "+damage);
	return damage;
  }
  
  public ConcurrentHashMap<Integer, EnemyPlayer3D> getOtherPlayers()
  {
	return enemies;  
  }
  
  public World3D getWorld()
  {
	return world;  
  }
  
  public BossMap getMap()
  {
	return map;  
  }
  
  public Game getGame()
  {
	return game;  
  }
  
  public void createPools()
  {
    pfxPool[Player3D.DARK] = new PFXPool(asset.get(AssetConstants.DARK_PFX, ParticleEffect.class));
    pfxPool[Player3D.LIGHT] = new PFXPool(asset.get(AssetConstants.LIGHT_PFX, ParticleEffect.class));
    pfxPool[Player3D.FIRE] = new PFXPool(asset.get(AssetConstants.FIRE_PFX, ParticleEffect.class));
    pfxPool[Player3D.ICE] = new PFXPool(asset.get(AssetConstants.ICE_PFX, ParticleEffect.class));
    pfxPool[Player3D.LIGHTNING] = new PFXPool(asset.get(AssetConstants.LIGHTNING_PFX, ParticleEffect.class));
    pfxPool[Player3D.CHAOS] = new PFXPool(asset.get(AssetConstants.CHAOS_PFX, ParticleEffect.class));
    
    gsPool = new GSpellPool(asset.get(AssetConstants.SPELL, Model.class));
  }
  
  public void dispose()
  {
	batch2D.dispose();
	batch.dispose();
	if(world != null)
	 world.dispose();
	if(hud != null)
	 hud.dispose();
	if(expamenu != null)
	 expamenu.dispose();
	if(player != null)
	 player.dispose();
	if(enemies != null)
	 for(Entry<Integer, EnemyPlayer3D> e : enemies.entrySet())
	  e.getValue().dispose();
	if(bspells != null)
	 for(Entry<Integer, GraphicsSpell> e : bspells.entrySet())
	  e.getValue().dispose();
	if(map != null)
	 map.dispose();
	if(contactListener != null)
	 contactListener.dispose();
	for(PFXPool p : pfxPool) 
	 if(p != null) 
	  p.dispose();
	if(gsPool != null)
	 gsPool.dispose();
  }
}
