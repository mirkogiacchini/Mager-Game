package com.MirkoGiacchini.game;

import com.MirkoGiacchini.game.abstracted.GamePlayer;
import com.badlogic.gdx.graphics.Color;

public class AssetConstants 
{
   public static final String FONT_PATH = "font/font.fnt"; 
   
   public static final Color LOADING_MENU_COLOR = Color.PURPLE;
   public static final String LOADING_MENU_BACKGROUND = "loadingscreen.jpg";
   
   public static final String DEF_BUTTON = "bottone.png";
   public static final String DEF_BUTTON2 = "bottone2.png";
   public static final Color DEF_BUTTON_COLOR = Color.WHITE;
   public static final Color DEF_BUTTON2_COLOR = Color.GRAY;
   public static final float DEF_BUTTON_SX = 1.8f;
   public static final float DEF_BUTTON_SY = 1.8f;
   
   public static final String DEF_TEXTFIELD = "textfield.png";
   public static final String DEF_TEXTFIELD2 = "textfield2.png";
   public static final Color DEF_TEXTFIELD_COLOR = new Color(0.8f, 0.145f, 0.125f, 1);
   
   public static final String ERROR_MENU_BACKGROUND = "backbox.png";
   public static final Color ERROR_MENU_STRCOLOR = Color.GREEN;
   
   public static final String LOGIN_MENU_BACKGROUND = "background1.png";
   public static final String LOGIN_MENU_BACKBOX = "backbox.png";
   public static final Color LOGIN_MENU_BACKBOX_COLOR = Color.GREEN;
   
   public static final String MAIN_MENU_INFOBACK = "infoback.jpg";
   public static final String MAIN_MENU_ROOMBACK = "roomback.jpg";
   
   public static final String LARROW = "frecciasx.png";
   public static final String LARROW2 = "frecciasx2.png";
   public static final String RARROW = "frecciadx.png";
   public static final String RARROW2 = "frecciadx2.png";
   
   public static final String ROOM_BUTTON = "bottone12.png";
   public static final String ROOM_BUTTON2 = "bottone22.png";
   public static final Color ROOM_BUTTON_COLOR = DEF_BUTTON_COLOR;
   public static final Color ROOM_BUTTON2_COLOR = DEF_BUTTON2_COLOR;
   
   public static final String MAIN_MENU_BACKBOX = "backbox.png";
   public static final Color BACKBOX_COLOR = Color.ORANGE;
   public static final Color INFO_COLOR = Color.GREEN;
   
   public static final String[] ROOMS_INFO = {"Dorf", "Friedhof"};
   public static final String[] ROOMS_SIZES = {"2", "3", "4", "5", "6", "7", "8", "15", "30"};
   public static final String MAP_PATH = "maps/";
   
   public static final String SHOP_BACKGROUND = "background2.png";
   public static final Color SHOP_INFO_COLOR = Color.CYAN;
   public static final Color ITEM_ICONS_COLOR = new Color(26.f/255, 93.f/255, 210.f/255, 1);
   public static final String SHOP_BOX = "backbox.png";
   public static final Color SHOP_BOX_COLOR = Color.NAVY;
   
   public static final String IN_STICK = "intstick.png";
   public static final String EXT_STICK = "extstick.png";
   
   public static final String SP_ICON = "spicon.png";
   public static final Color SP_COLOR = Color.GREEN;
   public static final String HP_ICON = "hpicon.png";
   public static final Color HP_COLOR = Color.RED;
   public static final String MANA_ICON = "manaicon.png";
   public static final Color MANA_COLOR = Color.BLUE;
   
   public static final int INV_SENSIBILITY = 15;
   public static final int INV_SENSIBILITY_ANDROID = 10;
   public static final float CAMERA_OFF_HEIGHT = 0.5f;
   public static final float PLAYER_MASS = 2f;
   public static final float PLAYER_CAPSULE_ARGS[] = {4, 12};
   
   public static final Color EX_PAUSE_MENU_LABEL_COLOR = Color.YELLOW;
   
   public static final String TREE1_MODEL = "tree1/tree1.g3db";
   public static final String TOWER_MODEL = "tower/tower.g3db";
   public static final String PLAYER_ARMS = "Arms/Arms.g3db";
   public static final String ENEMY_PLAYER = "mage/mage.g3db";
   public static final String PLAYER_ARMS_TEXTURE = "Arms/ArmsDiffuse.jpg";
   public static final String CROSS1 = "cross/cross1.g3db";
   public static final String CROSS2 = "cross/cross2.g3db";
   public static final String CROSS3 = "cross/cross3.g3db";
   public static final String TOMBSTONE = "cross/tombstone.g3db";
   public static final String BARREL = "woodobj/barrel.g3db";
   public static final String HOUSE1 = "houses/house1.g3db";
   public static final String HOUSE2 = "houses/house2.g3db";
   public static final String BOSS1 = "boss1/boss1.g3db";
   public static final String BOSS2 = "boss2/boss2.g3db";
   public static final String POTION_ITEM = "potion/potion.g3db";
   public static final String SKYBOX1 = "skybox1/skybox1.g3db";
   public static final String PUMPKIN = "pumpkin/pumpkin.g3db";
   public static final String TOWER2 = "tower2/tower2.g3db";
   public static final String WELL = "woodobj/well.g3db";
   public static final String BUSH = "bush/bush.g3db";
   public static final String BANNER = "banner/banner.g3db";
   public static final String ROLLER = "training/roller.g3db"; //ArmatureAction -> animazione
   public static final String MONEYBAG = "gold/moneybag.g3db";
   public static final String GOLD = "gold/gold.g3db";
   public static final String DUMMY = "training/dummy.g3db";
   public static final String MOUNTAIN1 = "mountain1/mountain1.g3db";
   public static final String MOUNTAIN2 = "mountain1/mountain2.g3db";
   public static final String CEMSTONE = "cross/cemstone.g3db";
   public static final String WALL = "wall/wall.g3db";
   
   public static final String MANA_TEXT = "potion/mana.jpg";
   public static final String HP_TEXT = "potion/hp.jpg";
   
   public static final String DARK_ATK = "shopicons/darkatk.png";
   public static final String DARK_DEF = "shopicons/darkdef.png";
   public static final String LIGHT_ATK = "shopicons/lightatk.png";
   public static final String LIGHT_DEF = "shopicons/lightdef.png";
   public static final String CHAOS_ATK = "shopicons/chaosatk.png";
   public static final String CHAOS_DEF = "shopicons/chaosdef.png";
   public static final String LIGHTNING_ATK = "shopicons/lightningatk.png";
   public static final String LIGHTNING_DEF = "shopicons/lightningdef.png";
   public static final String FIRE_ATK = "shopicons/fireatk.png";
   public static final String FIRE_DEF = "shopicons/firedef.png";
   public static final String ICE_ATK = "shopicons/iceatk.png";
   public static final String ICE_DEF = "shopicons/icedef.png";
   public static final String HP_SH = "shopicons/hp.png";
   public static final String BOSS_SH = "shopicons/mob.png";
   public static final String CRIT_SH = "shopicons/crit.png";
   public static final String PERF_SH = "shopicons/perf.png";
   
   public static final Color SPELL_COLOR = Color.OLIVE;
   public static final Color TIME_LABEL_COLOR = Color.CYAN;
   
   public static final Color ENEMIES_LABEL_COLOR = Color.RED;
   public static final float OFFSET_Y_ENLAB = 35f;
   public static final float OFFSET_X_ENLAB = -20f;
   public static final float BASE_DIST = 82931.85f;
   
   public static final String DARK_SPELL_T = "spell/difDark.png";
   public static final String LIGHT_SPELL_T = "spell/difLight.png";
   public static final String FIRE_SPELL_T = "spell/difFire.png";
   public static final String ICE_SPELL_T = "spell/difIce.png";
   public static final String LIGHTNING_SPELL_T = "spell/difLightning.png";
   public static final String CHAOS_SPELL_T = "spell/difChaos.png";
   
   public static final String DEATH_SCREEN = "bloodyScreen.png";
   
   public static final String HEIGHTMAP1 = "heightmap1/heightmap1.png";
   public static final String HEIGHTMAP1TEXT = "heightmap1/grass2.png";
   public static final String HEIGHTMAP2 = "heightmap2/heightmap2.png";
   public static final String HEIGHTMAP2TEXT = "heightmap2/texture.jpg";
   
   public static final Color SCOREBOARD_LABEL_COLOR = Color.RED;
   public static final Color MY_SCOREBOARD_LABEL_COLOR = Color.GREEN;
   
   public static final Color MESSAGES_COLOR_NEUTRAL = Color.CYAN;
   public static final Color MESSAGES_COLOR_GOOD = Color.GREEN;
   public static final Color MESSAGES_COLOR_BAD = Color.RED;
   
   public static final String END_GAME_SCREEN = DEATH_SCREEN;
   public static final Color END_GAME_COLOR = Color.RED;
   public static final Color END_GAME_COLOR2 = Color.PURPLE;
   
   public static final String SPELL = "spell/spell.g3db";
   
   public static final String MUSICBUTTON1 = "music1.png";
   public static final String MUSICBUTTON2 = "music2.png";
   public static final String SFXBUTTON1 = "sfx1.png";
   public static final String SFXBUTTON2 = "sfx2.png";
   public static final String OPTIONSBACK = "optionback.jpg";
   
   public static final String MUSIC1 = "music/background1.mp3";
   public static final String MUSIC2 = "music/background2.mp3";
   public static final String FIRESFX = "music/fireEffect.mp3";
   public static final String HITSFX = "music/hitEffect.mp3";
   public static final String DEATHSFX = "music/deathEffect.mp3";
   public static final String BOSSATKSFX = "music/bossattack.mp3";
   
   public static final String CHAOS_GICON = "spellgameicons/chaosgicon.png";
   public static final String LIGHTNING_GICON = "spellgameicons/lightninggicon.png";
   public static final String FIRE_GICON = "spellgameicons/firegicon.png";
   public static final String ICE_GICON = "spellgameicons/icegicon.png";
   public static final String LIGHT_GICON = "spellgameicons/lightgicon.png";
   public static final String DARK_GICON = "spellgameicons/darkgicon.png";
   
   public static final String ICE_PFX = "spellEffect/iceEffect.p";
   public static final String FIRE_PFX = "spellEffect/fireEffect.p";
   public static final String LIGHT_PFX = "spellEffect/lightEffect.p";
   public static final String DARK_PFX = "spellEffect/darkEffect.p";
   public static final String LIGHTNING_PFX = "spellEffect/lightningEffect.p";
   public static final String CHAOS_PFX = "spellEffect/chaosEffect.p";
   
   public static String itemIcon(int index)
   {
	 switch(index)
	 {
	   case GamePlayer.DARK: return DARK_ATK;
	   case GamePlayer.DARK+1: return DARK_DEF;
	   case GamePlayer.LIGHT: return LIGHT_ATK;
	   case GamePlayer.LIGHT+1: return LIGHT_DEF;
	   case GamePlayer.CHAOS: return CHAOS_ATK;
	   case GamePlayer.CHAOS+1: return CHAOS_DEF;
	   case GamePlayer.LIGHTNING: return LIGHTNING_ATK;
	   case GamePlayer.LIGHTNING+1: return LIGHTNING_DEF;
	   case GamePlayer.FIRE: return FIRE_ATK;
	   case GamePlayer.FIRE+1: return FIRE_DEF;
	   case GamePlayer.ICE: return ICE_ATK;
	   case GamePlayer.ICE+1: return ICE_DEF;
	   case GamePlayer.HP: return HP_SH;
	   case GamePlayer.CRIT: return CRIT_SH;
	   case GamePlayer.PERF: return PERF_SH;
	   case GamePlayer.BOSS_DMG: return BOSS_SH;
	   default: return "bottone.png";
	 }
   }
}
