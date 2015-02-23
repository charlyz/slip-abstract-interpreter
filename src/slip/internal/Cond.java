package slip.internal;

public class Cond extends AbstractNode
{
  Sexpr expr1;
  Cop  cop;
  Sexpr expr2;

  public Cond(Sexpr expr1, Cop cop, Sexpr expr2)
  { this(cop) ; setExprs(expr1, expr2) ; }

  public Cond(Cop cop)
  { this.cop = cop ; }

  public void setExprs(Sexpr e1, Sexpr e2)
  { expr1 = e1 ; expr2 = e2 ; }

  public String toString()
  {
    return expr1 + " " + cop + " " + expr2 ;
  }

/**
 * @return the expr1
 */
public Sexpr getExpr1() {
	return expr1;
}

/**
 * @return the cop
 */
public Cop getCop() {
	return cop;
}

/**
 * @return the expr2
 */
public Sexpr getExpr2() {
	return expr2;
}

}

