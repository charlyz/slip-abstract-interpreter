package slip.internal;

public class In extends Cmd // read(x)
{
  private int x; 

  public In(int x){ this.setX(x); }

  public String toString()
  { return "read(" + varName(getX()) + ")" ; }

public void setX(int x) {
	this.x = x;
}

public int getX() {
	return x;
}
                
}

