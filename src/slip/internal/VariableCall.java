package slip.internal ;

public class VariableCall extends Call
{
  int target; // target of the call (a variable)
  String m;   // Method name

  public VariableCall(int x, int target, String m, int[] lfp)
  { super(x, lfp); this.target = target; this.m = m; }

  String target(){ return varName(target) + "." + m ; }

public int getTarget()
{
	return target;
}

public String getM()
{
	return m;
}
}

