package slip.internal;

public abstract class Aop extends AbstractNode // arithmetic operator
{
  final char aop; // '+', '*', '-', '/', '%'

  public char getAop() {
	return aop;
}

Aop(char aop)
  { this.aop = aop; }

  public String toString(){ return "" + aop ; }
}

