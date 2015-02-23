package abstractI.domain;

/**
 * Class representing abstract value.
 * 
 * @author cdessart
 *
 */
public final class ValA 
{
	private int valsZa;
	private PRefA valsRefA;
	private boolean noInitA;
	private boolean nullA;
	
	public ValA()
	{
		//this.valsZa = new PZa();
		this.valsZa = PZa.EMPTY;
		this.valsRefA = new PRefA();
		this.noInitA = false;
		this.nullA = false;
	}
	
	public ValA(int za, PRefA refa, boolean noInitA, boolean nullA)
	{
		this.valsZa = za;
		this.valsRefA = refa;
		this.noInitA = noInitA;
		this.nullA = nullA;
	}
	
	/**
	 * @return a new abstract value corresponding to a non initialize value.
	 */
	public static ValA newValAWithNoInit()
	{
		ValA va = new ValA();
		va.setNoInitA(true);
		return va;
	}
	
	/**
	 * @return a new abstract integer value corresponding to za
	 */
	public static ValA newValAWithZa(int za)
	{
		ValA va = new ValA();
		va.setZa(za);
		return va;
	}
	
	/**
	 * check whether the only possible value for an abstract value is an abstract integer value.
	 */
	public boolean containsOnlyZa()
	{
		return containsZa() && !containsRefA() && !containsNullA();
	}
	
	/**
	 * check whether the only possible value for an abstract value is an abstract reference value.
	 */
	public boolean containsOnlyRefA()
	{
		return !containsZa() && containsRefA() && !containsNullA();
	}
	
	/**
	 * check whether the only possible value for an abstract value is the null element.
	 */
	public boolean containsOnlyNullA()
	{
		return !containsZa() && !containsRefA() && containsNullA();
	}

	/**
	 * @return a new abstract reference value corresponding to refa
	 */
	public static ValA newValAWithRefA(RefA refa)
	{
		ValA va = new ValA();
		PRefA prefa = new PRefA();
		prefa.addRef(refa);
		va.setRefA(prefa);
		return va;
	}
	
	/**
	 * update the abstract integer part of an abstract value with za.
	 */
	public void setZa(int za)
	{
		valsZa = za;
	}
	
	/**
	 * update the abstract reference part of an abstract value with refa.
	 */
	public void setRefA(PRefA refa)
	{
		valsRefA = refa;
	}
	
	/**
	 * @return integer part of the abstract value.
	 */
	public int Za()
	{
		return valsZa;
	}
	
	/**
	 * @return the references part of the abstract value.
	 */
	public PRefA RefA()
	{
		return valsRefA;
	}
	
	/**
	 * check whether this abstract value corresponds to a non initialized value.
	 */
	public boolean noInitA()
	{
		return noInitA;
	}
	
	/**
	 * check whether this abstract value corresponds to null value.
	 */
	public boolean nullA()
	{
		return nullA;
	}
	
	/**
	 * check whether this abstract value is less or equals than another abstract value according
	 * to the well-founded relation of the polyvariant algorithm.
	 */
	public boolean leq(ValA vala, StoreA sA1, StoreA sA2)
	{
		return 		PZa.leq(valsZa,vala.Za())
				&& 	this.valsRefA.leq(vala.RefA(), sA1, sA2)
				&&	(!this.noInitA || vala.noInitA())
				&&  (!this.nullA || vala.nullA());
	}
	
	public ValA ub(ValA va)
	{
		return new ValA( 	PZa.ub(valsZa,va.Za()),
							valsRefA.ub(va.RefA()),
							noInitA || va.noInitA,
							nullA || va.nullA());
	}
	
	/**
	 * compute the intersection of two abstract values
	 */
	public ValA intersection(ValA va)
	{
		return new ValA( 	PZa.intersection(valsZa,va.Za()),
							valsRefA.intersection(va.RefA()),
							noInitA && va.noInitA,
							nullA && va.nullA());
	}
	
	/**
	 * return this abstract value excepted element included in va.
	 */
	public ValA except(ValA va)
	{
		return new ValA( 	PZa.except(valsZa,va.Za()),
							valsRefA.except(va.RefA()),
							noInitA && !va.noInitA,
							nullA && !va.nullA());
	}
	
	/**
	 * update the non-initialize part of the abstract value.
	 */
	public void setNoInitA(boolean b)
	{
		this.noInitA = b;
	}
	
	/**
	 * update the null part of the abstract value.
	 */
	public void setNullA(boolean b)
	{
		this.nullA = b;
	}
	
	/**
	 * check whether an abstract value contains some integer values.
	 */
	public boolean containsZa()
	{
		return PZa.containsVals(valsZa);
	}
	
	/**
	 * check whether an abstract value contains some reference values.
	 */
	public boolean containsRefA()
	{
		return valsRefA.containsVals();
	}
	
	
	public boolean containsNoInitA()
	{
		return this.noInitA;
	}
	
	/**
	 * check whether this abstract value may be at least one element out of 
	 * (integer value, reference, null or non-init)
	 */
	public boolean containsElements()
	{
		return containsNoInitA() || containsNullA() || containsRefA() || containsZa();
	}
	
	public boolean containsNullA()
	{
		return this.nullA;
	}
	
	/**
	 * check whether this abstract value contains all elements of another abstract value.
	 */
	public boolean contains(ValA va)
	{
		return 		PZa.contains(valsZa,va.Za())
				&&	valsRefA.contains(va.RefA())
				&&	(va.noInitA()?this.noInitA:true)
				&&	(va.nullA()?this.nullA:true);
	}
	
	public String toString() {
		String res = "{";
		if (this.noInitA)
			res += "noInitA, ";
		if (this.nullA)
			res += "nullA, ";
		res+= PZa.toString(valsZa)+", ";
		res+= this.valsRefA.toString()+", ";
		return res;
	}
	
	public ValA clone()
	{
		return new ValA(valsZa, valsRefA.clone(), noInitA, nullA);
	}
	
	/**
	 * reset this abstract value to an empty abstract value.
	 */
	public void reset()
	{
		this.valsZa = PZa.EMPTY;
		this.valsRefA = new PRefA();
		this.noInitA = false;
		this.nullA = false;
	}
}