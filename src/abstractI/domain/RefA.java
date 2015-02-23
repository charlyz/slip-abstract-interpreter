package abstractI.domain;

/**
 * Abstract reference class
 * 
 * @author cdessart
 */
public class RefA
{
	int val;
	
	public RefA(int val)
	{
		this.val = val;
	}
	
	public int val()
	{
		return val;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + val;
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if(this == obj) return true;
		if(obj == null) return false;
		if(getClass() != obj.getClass()) return false;
		RefA other = (RefA) obj;
		
		if(val != other.val) return false;
		//System.out.println("val:" + val + " & other val: "+ other.val);
		return true;
	}
	
	public String toString()
	{
		return "RefA{"+val+"}";
	}
}
