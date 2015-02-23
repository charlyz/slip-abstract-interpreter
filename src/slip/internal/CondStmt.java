package slip.internal;

public class CondStmt extends Stmt // l if cond then l else l
{
  // initial label == this
  Cond cond;
  Stmt ltrue;  // final label if true
  Stmt lfalse; // final label if false

  public CondStmt(Cond cond){ this.cond = cond; }
  public void setTrueLabel(Stmt l) { ltrue = l; }
  public void setFalseLabel(Stmt l){ lfalse = l; }

  public CondStmt(Cond cond, Stmt t, Stmt f)
  { this(cond) ; ltrue = t ; lfalse = f ; }

  public String toString()
  { 
    timeStamp = magic ; // to generate it only once.

    String res = "  " + toComment() ;

    if (ltrue.timeStamp  < magic & !(ltrue instanceof Method)) 
       res += ELN + ltrue ;
    if (lfalse.timeStamp < magic & !(lfalse instanceof Method))
       res += ELN + lfalse ;

    return res ;
  }

  public String toComment() 
  { 
    String res = "[ " + this.label + " : if " + cond 
                  + " then go to " + ltrue.label 
                  + " else go to " + lfalse.label 
                  + "]" ;

    return res ;
  }
/**
 * @return the cond
 */
public Cond getCond() {
	return cond;
}
/**
 * @return the ltrue
 */
public Stmt getLtrue() {
	return ltrue;
}
/**
 * @return the lfalse
 */
public Stmt getLfalse() {
	return lfalse;
}

}

