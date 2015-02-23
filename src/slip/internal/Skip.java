package slip.internal;

public class Skip extends Cmd // skip
{
  static int count = 0 ; 

  public Skip(){ count ++ ; }

  public String toString()
  { 
    return "skip" ; 
  }
}

