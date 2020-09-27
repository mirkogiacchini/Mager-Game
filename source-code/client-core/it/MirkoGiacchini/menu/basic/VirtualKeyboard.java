package it.MirkoGiacchini.menu.basic;

import it.MirkoGiacchini.menu.Label;
import it.MirkoGiacchini.menu.Menu;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;

/**
 * tastierino virtuale
 * @author Mirko
 *
 */
public class VirtualKeyboard 
{
  boolean capslock = false;
  
  /**
   * 
   * @param x1 x left corner
   * @param y1 y left corner
   */
  public VirtualKeyboard(float x1, float y1, float width, float height, final Menu menu, Texture t1, Texture t2, float lsx, float lsy, Color bc1, Color bc2, String font)
  {
	//creo tastierino -> permette l'uso su android	
	for(int k=2; k>=0; k--) //numeri e comandi
	 for(int i=0; i<10; i++)
	 {
	   final int fi = i, fk = k;
	   if( (2-k) * 10 + fi <= 25 ) //lettere
	   {
	     menu.addButton(new FontButton(t1, t2, width/10*i + x1, k*(height/5) + y1, width/10, height/5, new Label(""+(char)((2-k)*10+i+'a'), lsx, lsy, 0, font), bc1, bc2)
	     {
		   @Override
	 	   public void onReleased(int pointer)
		   {   
		     menu.onKeyTyped((char)((2-fk)*10+fi+(capslock ? 'A' : 'a')));	
		   } 
	     });
	   }
	   else //comandi
	   {
		 final int keyP;
		 final String sim;
		 boolean keep = false;
		 
		 switch( (2-k) * 10 + fi )
		 {
		   case 26: keyP = 21; sim = "<"; break; //freccia sx
		   case 27: keyP = 22; sim = ">"; break; //freccia dx
		   case 28: keyP = 67; sim = "<-"; break; //canc
		   case 29: keyP = -1; sim = "CL"; keep = true; break; //block maiusc
		   default: keyP = 0; sim = ""; break;
		 }
		 
	     menu.addButton(new FontButton(t1, t2, width/10*i + x1, k*(height/5) + y1, width/10, height/5, new Label(sim, lsx, lsy, 0, font), bc1, bc2, keep)
	     {
	       @Override
	       public void onReleased(int pointer)
	       {
	    	 menu.keyReleased(keyP);  
	       }
	       
	       @Override
	       public void onPressed(int pointer)
	       {
	    	 if((2-fk) * 10 + fi == 29)  
	    	  capslock = this.isPressed();
	       }
	     });
	   }
	 }
	
	 //numeri
	 for(int i=0; i<10; i++)
	 {
	   final int fi = i;	
	   menu.addButton(new FontButton(t1, t2, width/10*i + x1, 3*(height/5) + y1, width/10, height/5, new Label(""+(char)(i+'0'), lsx, lsy, 0, font), bc1, bc2)
	   {
	     @Override
	     public void onReleased(int pointer)
	     {   
		   menu.onKeyTyped((char)(fi+'0'));	
	     } 
	   });
	 }
	 
	 //caratteri speciali
	 for(int i=0; i<2; i++)
	 {
		final char charc = i == 0 ? '@' : '.';
		
	    menu.addButton(new FontButton(t1, t2, width/10*i + x1, 4*(height/5) + y1, width/10, height/5, new Label(charc+"", lsx, lsy, 0, font), bc1, bc2)
		{
		  @Override
		  public void onReleased(int pointer)
		  {   
			menu.onKeyTyped(charc);	
		  } 
		}); 
	 }
  }
}
