package slip.internal;

public abstract class Des extends Sexpr 
// left expression x | x.i | this.i
{
  int x; // the variable index (x) or 0 (this)
		int i; // the field number (x.i | this.i)	or 0 (x)
		
  public int getNumber() { return x ; }

  public Des(int x, int i){ this.x = x; this.i = i;}

  public String toString()
		{ 
			 String r ;
			 if (x==0 &  i!=0) r = "this" ; 
				     else r = varName(x) ;
			 if (i!=0) r += "." + i ; 
		
		  return r ;
		}

public int getX() {
	return x;
}

public int getI() {
	return i;
}

}

