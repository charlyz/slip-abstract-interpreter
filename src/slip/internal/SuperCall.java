package slip.internal;

public class SuperCall extends Call // x = super.m(x,..., x)
{
  String m; // Method name

  public SuperCall(int x,  String m, int[] lfp)
  { super(x, lfp); this.m = m; }

  String target(){ return "super." + m ; }

public String getM()
{
	return m;
}
}

