package slip.internal;

public class SimpleCall extends Call // x = m(x, ..., x);
{
  String m;   // Name of the method 

  public SimpleCall(int x,  String m, int[] lfp)
  { super(x, lfp); this.m = m; }

  public String target(){ return m ; }

public String getM()
{
	return m;
}
}

