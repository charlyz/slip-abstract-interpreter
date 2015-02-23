package slip.internal ;

public class Cexpr extends Expr // arithmetic expression (expr1 aop expr2)
{

	Sexpr expr1; 
	Aop  aop;
	Sexpr expr2; 

  public Cexpr(Sexpr expr1, Aop aop, Sexpr expr2)
  { this.expr1 = expr1; this.aop = aop; this.expr2 = expr2; }

  public String toString()
  { return "" + expr1 + " " + aop
              + " " + expr2 ; }
  
  public Sexpr getExpr1() {
		return expr1;
	}

	public Aop getAop() {
		return aop;
	}

	public Sexpr getExpr2() {
		return expr2;
	}
}

