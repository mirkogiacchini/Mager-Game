package com.MirkoGiacchini.gameserver;

/**
 * protocollo dei comandi che si possono dare al server
 * @author Mirko
 *
 */
public class CmdProtocol 
{
  /**abilita la modalità solo tcp*/	
  public static final String ENABLE_ONLY_TCP = "only tcp on";
  
  /**disabilita modalità solo tcp*/
  public static final String DISABLE_ONLY_TCP = "only tcp off";
  
  /**abilito debug*/
  public static final String ENABLE_DEBUG = "debug on";
  
  /**disabilito debug*/
  public static final String DISABLE_DEBUG = "debug off";
}
