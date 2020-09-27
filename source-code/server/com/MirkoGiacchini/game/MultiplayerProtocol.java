package com.MirkoGiacchini.game;

/**costanti del protocollo per gestire il multiplayer*/
public class MultiplayerProtocol 
{
  /**
   * messaggio inviato da un client a tutti gli altri client nella stanza per aggiornare il suo stato (inviato frequentemente, UDP). 
   * sintassi: playerId + PLAYER_UPDATE + posizione + direzione + roty + rotxz + firing + spellInd + firingDirection + hp + mana + playerName + kills + deaths
   **/	
  public static final String PLAYER_UPDATE = "@pupd";
  
  /**
   * messaggio inviato quando un client si disconnette della stanza e deve essere rimosso dal gioco (inviato solo alla disconnessione, TCP).
   * sintassi: REMOVE_PLAYER_FROM_GAME + playerId
   * */
  public static final String REMOVE_PLAYER_FROM_GAME = "@rpl";
  
  /**
   * messaggio inviato quando si prende un item (TCP)
   * sintassi: ITEM_TAKEN + itemId + playerId + last_item
   */
  public static final String ITEM_TAKEN = "@ittkn";
  
  /**
   * messaggio inviato a chi entra a partita iniziata per indicargli quali item sono già stati presi (TCP)
   * sintassi: RESET_ITEMS destPlayerId 0 lastitem0 ... N lastitemN
   */
  public static final String RESET_ITEMS = "@resitm";
  
  /**
   * messaggio inviato quando entra un nuovo player nella stanza... deve ricevere le stats di tutti gli altri giocatori (TCP)
   * sintassi: SEND_STATS + id + lista_delle_statistiche(16)
   */
  public static final String SEND_STATS = "@sndsts";
  
  /**
   * messaggio inviato quando un player viene colpito da un altro
   * sintassi: HIT + idPlayerCheHaSparato + idPlayerColpito + danno
   */
  public static final String HIT = "@hit";
  
  /**
   * messaggio inviato dal master per indicare l'inizio del gioco.
   * sintassi: START_GAME_TIME + idACuiInviare + inizioGioco
   */
  public static final String START_GAME_TIME = "@sgt";
  
  /**
   * messaggio inviato dal master a tutti gli altri per comunicare gli aggiornamenti del boss.
   * sintassi: playerId + BOSS_UPDATE + x, y, z + roty + direction + hp + lastD
   */
  public static final String BOSS_UPDATE = "@bupd";
  
  /**
   * messaggio inviato quando il boss riceve un hit.
   * sintassi: BOSS_HIT + playerId + damage
   */
  public static final String BOSS_HIT = "@hitbss";
  
  /**
   * messaggio inviato quando il boss attacca un player.
   * sintassi: BOSS_HIT_PLAYER + playerId + damage
   */
  public static final String BOSS_HIT_PLAYER = "@hitbp";
  
  /**
   * messaggio da inviare quando un player entra nella stanza: deve sapere i danni del boss.
   * sintassi: BOSS_INIT + id + N + player0 danni0 + ... + playerN-1 danniN-1
   */
  public static final String BOSS_INIT = "@initboss";
}
