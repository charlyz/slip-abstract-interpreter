package abstractI.domain;

import java.util.HashMap;

import slip.interpreter.Identifier;

/**
 * Class that defines an abstract domain (pair of environment and store)
 * 
 * @author Jian Lian
 * @author Cedric Vanderperren
 */
public class AbstractDomain 
{
	private EnvA envA;
	private StoreA sA;
	
	
	public AbstractDomain(EnvA envA, StoreA sA) 
	{
		this.envA = envA;
		this.sA = sA;
	}
	
	public AbstractDomain(AbstractDomain da)
	{
		this.envA = new EnvA(da.envA);
		this.sA = new StoreA(da.sA);
	}
	
	public AbstractDomain()
	{
		this.envA = new EnvA();
		this.sA = new StoreA();
	}
	
	public StoreA getStoreA()
	{
		return sA;
	}
	
	public AbstractDomain(StoreA s)
	{
		this.envA = new EnvA();
		this.sA = s;
	}
	
	/**
	 * ajoute une nouvelle valeur dans l'environnement.
	 * 
	 * @preconditions : i et va sont non null.
	 * @postconditions : le couplet (i -> va) est ajoute aux elements du domaine
	 * @param i
	 * @param va
	 */
	public void addVal(Identifier i, ValA va)
	{
		envA.addVal(i, va);
	}
	
	/**
	 * 
	 * permet de recuperer une valeur dans l'environnement abstrait.
	 * 
	 * @preconditions : i est non null.
	 * @postconditions : i est inchange.
	 * @return : la valeur abstraite correspondant a l'identicateur i si i est
	 * present dans l'environnement abstrait.
	 * @return : une valeur abstraite correspondant a noninit sinon.
	 */
	public ValA getValA(Identifier i)
	{
		ValA va = envA.getValA(i);
		if(va==null)
			va = ValA.newValAWithNoInit();
		return va;
	}
	
	/**
	 * permet de retrouve un objet abstrait du store abstrait 
	 * suivant une reference abstraite.
	 * @preconditions : ref est non null.
	 * @postconditions : ref est inchange.
	 * @return : l'objet abstrait correspondant a ref s'il y a une telle 
	 * reference
	 * dans le store abstrait.
	 * @return : null sinon.
	 */
	public ObjA getObjA(RefA ref)
	{
		return sA.getObjA(ref);
	}
	
	/**
	 * permet d'ajouter un nouvel
	 * objet au store.
	 * @preconditions : refA et objA sont non null.
	 * @postconditions : refA et objA sont inchanges.
	 * @postconditions : le couple (refA ->objA) est ajoute au store abstrait.
	 */
	public void addObjA(RefA refA, ObjA objA)
	{
		sA.addObjA(refA, objA);
	}
	

	/**
	 * permet de modier un champ d'un objet abstrait.
	 * @preconditions : refA, i et vA sont non null.
	 * @postconditions : refA, i et vA sont inchanges.
	 * @postconditions : le champ i de l'objet correspondant 
	 * a refA est mis a jour avec la valeur de vA.
	 */
	public void setObjetField(RefA refA, Integer i, ValA vA)
	{
		sA.setObjetField(refA, i, vA);
	}
	
	public void setObjetFieldWithoutUB(RefA refA, Integer i, ValA vA)
	{
		sA.setObjetField(refA, i, vA, false);
	}
	
	/**
	 * calcule la relation inferieur egale de la relation bien fondee 
	 * de l'algorithme polyvariant.
	 * 
	 * @preconditions : da est non null.
	 * @postconditions : da est inchange.
	 * @return : true si this est inferieur ou egale a da suivant la relation 
	 * bien fondee,
	 * false sinon.
	 */
	public boolean leq(AbstractDomain da)
	{
		return envA.leq(da.envA, sA, da.sA);
	}
	
	/**
	 * calcul la borne superieur de deux domaines abstraites.
	 * 
	 * @preconditions : dA est non null.
	 * @postconditions : dA est inchange.
	 * @return la borne superieur de this et dA.
	 */
	public AbstractDomain ub(AbstractDomain dA)
	{
		AbstractDomain daRes = new AbstractDomain();
		daRes.envA = envA.ub(dA.envA);
		daRes.sA = sA.ub(dA.sA);
		
		return daRes;
	}
	
	public String toString()
	{
		String res = "Env = {";
		HashMap<Identifier, ValA> hM = envA.compress();
		for(Identifier i : hM.keySet())
		{
			res += i.toString() + "=" + hM.get(i) + ",\n";
		}
		
		if(!res.equals("Env = {")) 
				res = res.substring(0, res.length()-1);
		return res + "}";	
	}
}
