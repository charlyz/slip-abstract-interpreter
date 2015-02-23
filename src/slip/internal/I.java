package slip.internal;


public class I extends Sexpr // An integral value
{
  int i; // this integral value

  
public I(int i){ this.i = i; }

  public String toString(){ return "" + i ; }
  
  public int getI() {
		return i;
	}


}

