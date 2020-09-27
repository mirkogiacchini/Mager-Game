package com.MirkoGiacchini.game.gameplay.maps;

import com.MirkoGiacchini.game.Game;
import com.MirkoGiacchini.game.gameplay.Gameplay;
import com.MirkoGiacchini.game.gameplay.boss.BossMap;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;


/**
 * mappa0
 * @author Mirko
 *
 */
public class Map0 extends BossMap
{
	public Map0(FileHandle bossFile, FileHandle spawnFile, FileHandle heightmapFile, FileHandle mapFile,
			    FileHandle itemFile, AssetManager asset, Game game, Gameplay gameplay) 
	{
	   super(bossFile, spawnFile, heightmapFile, mapFile, itemFile, asset, game, gameplay);
	   for(int i=1; i<=13; i++)
	    for(Material m : getDecorativeEntities().get(i).getMaterials())
		 m.set(new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA));
	   getEntities().get(0).setAnimation("ArmatureAction");
	}
}
