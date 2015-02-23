package slip.internal ;


public class CmdStmt extends Stmt // l cmd l
{
  // initial label == this
  Cmd  cmd;
  Stmt next; // final label

  

public CmdStmt(Cmd cmd){ this.cmd = cmd ; }

  public CmdStmt(Cmd cmd, Stmt next)
  { this(cmd); setLabel(next) ; }


  public void setLabel(Stmt l){ next = l ; }

  public String toString()
  { 
    timeStamp = magic ; // to generate it only once.
    
    String res = "  " + toComment() ;

    if (next.timeStamp < magic & !(next instanceof Method)) 
       res += ELN + next ;

    return res ;
    
  }

  public String toComment()
  {    
    String res = "[ " + this.label + " : " + cmd 
                + " ; go to " + next.label + "]" ;

    return res ; 
  }

public Cmd getCmd() {
	return cmd;
}
public Stmt getNext() {
	return next;
}
}

