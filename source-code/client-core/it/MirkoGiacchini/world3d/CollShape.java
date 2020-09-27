package it.MirkoGiacchini.world3d;

/**
 * enumerazione dei possibili shape per le collisioni
 * @author Mirko
 *
 */
public enum CollShape
{
  MESH,
  BOX,
  CAPSULE,
  SPHERE;
  
  /**
   * restituisce lo shape associato all'indice passato: 0=mesh 1=box 2=capsule
   */
  public static CollShape getShape(int ind)
  {
	switch(ind)
	{
	  case 0: return MESH;
	  case 1: return BOX;
	  case 2: return CAPSULE;
	  case 3: return SPHERE;
	  default: return MESH;
	}
  }
}
