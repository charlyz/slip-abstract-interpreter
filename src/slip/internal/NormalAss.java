package slip.internal;

public class NormalAss extends Ass
{
  Des left ; // left expression
  Expr right ; // right expression

  public NormalAss(Des left, Expr right)
  { this.left = left ; this.right = right ; }

  public void setExpr(Expr right)
  { this.right = right ; }

  public String toString()
  { 
    return left + " := " + right  ; 
  }

public Des getLeft() {
	return left;
}

public Expr getRight() {
	return right;
}
}

