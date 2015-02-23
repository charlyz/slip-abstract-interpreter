package abstractI.domain;

import java.util.ArrayList;

/**
 * Set of abstract references
 * 
 * @author cdessart
 */
public final class PRefA
{
	private ArrayList<RefA> refs;
	
	public PRefA()
	{
		this.refs = new ArrayList<RefA>(4);
	}
	
	public PRefA(PRefA refa)
	{
		this.refs = (ArrayList<RefA>) refa.getRefs().clone();
	}
	
	public ArrayList<RefA> getRefs()
	{
		return refs;
	}
	
	/**
	 * check whether a an abstract reference is in a set of abstract references.
	 */
	public boolean contains(RefA i)
	{
		return refs.contains(i);
	}
	
	/**
	 * add a new reference into a set of reference
	 */
	public void addRef(RefA i)
	{
		this.refs.add(i);
	}
	
	/**
	 * compute the upper bound of two sets of abstract references.
	 */
	public PRefA ub(PRefA refa)
	{
		PRefA res = new PRefA(this);
		for(RefA i : this.refs)
		{
			if(!refa.contains(i))
				res.addRef(i);
		}
		return res;
	}
	
	/**
	 * computes the intersection of two set of abstract references.
	 */
	public PRefA intersection(PRefA refa)
	{
		PRefA res = new PRefA();
		for(RefA i : this.refs)
		{
			if(refa.contains(i))
				res.addRef(i);
		}
		return res;
	}
	
	/**
	 * return this set of references except references present in refa.
	 */
	public PRefA except(PRefA refa)
	{
		PRefA res = new PRefA();
		for(RefA i : this.refs)
		{
			if(!refa.contains(i))
				res.addRef(i);
		}
		return res;
	}
	
	/**
	 * test whether this set of references with a store sA1 is less or equals 
	 * to another set of references with his store sA2 according the well-founded relation
	 * of polyvariant algorithm.
	 */
	public boolean leq(PRefA refa, StoreA sA1, StoreA sA2) 
	{
		boolean res = true;
		for(RefA ref1 : this.getRefs())
		{
			boolean found = false;
			for(RefA ref2 : refa.getRefs())
			{
				if(ref1.val()==ref2.val())
				{
					found = true;
					ObjA o1 = sA1.getObjA(ref1);
					ObjA o2 = sA2.getObjA(ref2);
					res &= o1.leq(o2, sA1, sA2);
					if(!res) break;
				}
				if(!found) return false;
			}
		}
		
		return res;
	}
	
	/**
	 * check whether this set contains at least one reference.
	 */
	public boolean containsVals()
	{
		return refs.size()>0;
	}
	
	/**
	 * check whether this set of references contains a certain set of references.
	 */
	public boolean contains(PRefA refa)
	{
		boolean res = true;
		for(RefA i : refa.getRefs())
		{
			if(!this.contains(i))
			{
				res = false;
				break;
			}
		}
		return res;
	}
	
	public String toString() {
		String res = "Ref:{";
		for (RefA refa: refs)
			res+="["+refa.val()+"] ";
		return res.trim()+"}";
	}
	
	public PRefA clone()
	{
		return new PRefA(this);
	}
}
