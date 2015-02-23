package slip.internal;

public abstract class Stmt extends AbstractNode
{
  static private int count = 0 ; // Counter of Stmt labels.
  public final int labelInt ; // The label of this statement.
  public final String label ; // The label of this statement.
  int timeStamp = 0 ; // to avoid looping in toString() ;
  public Annotation status = Annotation.NR;

  Stmt(){ 
	  count++ ;
	  labelInt = count ;
	  label = "lab" + count ; 
	  }
		
  abstract String toComment() ;
  
  public static int getLastNumLabel()
  {
	  return count;
  }
}

