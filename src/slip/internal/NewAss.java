package slip.internal;

public class NewAss extends Ass// x = new/i ;
{
  int x ; // where to put the reference to the object
  int i ; // level of the object to be created

  public NewAss(int x, int i){ this.x = x; this.i = i ;}

  public String toString()
  { 
    return varName(x) + " := new/" + i  ; 
  }

/**
 * @return the x
 */
public int getX() {
	return x;
}

/**
 * @return the i
 */
public int getI() {
	return i;
}
}

