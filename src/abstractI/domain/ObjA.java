package abstractI.domain;

/**
 * Abstract object class
 * 
 * @author cdessart
 */
public final class ObjA
{
	private ValA[] fields;
	private int ref;
	
	public ObjA(int ref)
	{
		this.ref = ref;
		this.fields = new ValA[ref];
		
		// definit les champs � noninit
		for(int i=0; i<ref; i++)
		{
			ValA va = new ValA();
			va.setNoInitA(true);
			fields[i] = va;
		}
			
	}
	
	/**
	 * renvoie le champs i d'un objet abstrait.
	 * 
	 * @preconditions : i est inferieur ou egale a la valeur fields[0] c-a-d le
	 * nombre de champs de l'objet abstrait.
	 * @return la valeur abstraite du champs i-1 de l'objet.
	 */
	public ValA getField(int i)
	{
		return fields[i-1];
	}
	
	/** 
	 * met a jour un champs d'un objet abstrait.
	 * 
	 * @preconditions : i est inferieur ou egale a la valeur fields[0], va est non null.
	 * @postconditions : va est inchange.
	 * @postconditions : le champs i de l'objet abstrait est mis a jour a la valeur va.
	 */
	public void setField(int i, ValA va)
	{
		fields[i-1] = va;
	}
	
	/**
	 * @return la reference qui identie l'objet abstrait.
	 */
	public int getRef()
	{
		return ref;
	}
	
	/**
	 * teste si un objet est inferieur ou egale a un autre objet 
	 * suivant la relation bien fondee.
	 * 
	 * @preconditions : obja, sA1, sA2 sont non null.
	 * @preconditions : sA1 est le store abstrait associe a this
	 * et sA2 est le store abstrait associe a obja.
	 * @postconditions : obja, sA1, sA2 sont inchanges.
	 * @return true si this est inferieur ou egale a obja suivant la relation bien fondee. 
	 * false sinon.
	 */
	public boolean leq(ObjA obja, StoreA sA1, StoreA sA2) 
	{
		if (this.ref!=obja.getRef())
			throw new IllegalArgumentException("Object has not the same ref.");
		
		boolean res = true;
		for(int i = 0; i<this.ref; i++)
			res &= fields[i].leq(obja.getField(i+1), sA1, sA2);

		return res;
	}
	
	/**
	 * calcul la borne superieur de deux objets abstraits.
	 * 
	 * @preconditions : obja est non null.
	 * @preconditions : obja et this ont la meme reference 
	 * (cette precondition est teste dans la methode)
	 * @postconditions : obja est inchange.
	 * return un objet abstrait qui est la borne superieur de this et obja.
	 */
	public ObjA ub(ObjA obja) 
	{
		if (this.ref!=obja.getRef())
			throw new IllegalArgumentException("Object has not the same ref.");
		
		ObjA res = new ObjA(ref);
		for(int i = 0; i<this.ref; i++)
			res.setField(i+1, this.fields[i].ub(obja.getField(i+1)));
		return res;
	}
	
	/**
	 * calcul l'intersection de deux objets abstraits.
	 * 
	 * @preconditions : obja est non null.
	 * @preconditions : obja et this ont la meme reference
	 * @postconditions : obja est inchange.
	 * @return un objet abstrait qui est l'intersection de this et obja.
	 */
	public ObjA intersection(ObjA obja) 
	{
		if (this.ref!=obja.getRef())
			throw new IllegalArgumentException("Object has not the same ref.");
		
		ObjA res = new ObjA(ref);
		for(int i = 0; i<this.ref; i++)
			res.setField(i, this.fields[i].intersection(obja.getField(i)));
		return res;
	}
	
	/**
	 * calcul un sous-ensemble d'un objet abstrait.
	 * @preconditions : obja est non null.
	 * @preconditions : obja et this ont la meme reference
	 * @postconditions : obja est inchange.
	 * @return un objet abstrait correspondant aux valeurs de this 
	 * otee de tout les valeurs de obja.
	 */
	public ObjA except(ObjA obja) 
	{
		if (this.ref!=obja.getRef())
			throw new IllegalArgumentException("Object has not the same ref.");
		
		ObjA res = new ObjA(ref);
		for(int i = 0; i<this.ref; i++)
			res.setField(i, this.fields[i].except(obja.getField(i)));
		return res;
	}
	
	public String toString() {
		String res = "";
		res+=this.ref+"\n";
		for (int i=0; i<this.fields.length; i++)
			res+=+i+":"+this.fields[i]+"\n";
		return res;
		
	}
	
	public ObjA clone()
	{
		ObjA res = new ObjA(ref);
		res.fields = fields.clone();
		
		for(int i = 0; i < res.fields.length; i++)
			res.fields[i] = res.fields[i].clone();
		
		return res;
	}
}
