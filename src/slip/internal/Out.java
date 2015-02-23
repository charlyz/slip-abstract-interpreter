package slip.internal;

public class Out extends Cmd // write(x)
{
  int x; 

  public Out(int x){ this.x = x; }

  public String toString()
  { return "write(" + varName(x) + ")" ; }

public int getX() {
	return x;
}
}

