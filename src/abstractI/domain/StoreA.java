package abstractI.domain;

import java.util.LinkedList;
import java.util.ListIterator;
import java.util.WeakHashMap;

/**
 * Class that defines an abstract store (layers architecture)
 * 
 * @author Jian Lian
 * @author Cedric Vanderperren
 */
public class StoreA
{
	LinkedList<WeakHashMap<RefA, ObjA>> intStoreA;
	
	public StoreA()
	{
		intStoreA = new LinkedList<WeakHashMap<RefA, ObjA>>();
		intStoreA.addFirst(new WeakHashMap<RefA, ObjA>());
	}
	
	public StoreA(StoreA sA)
	{
		intStoreA = new LinkedList<WeakHashMap<RefA, ObjA>>(sA.getIntStoreA());
		intStoreA.addFirst(new WeakHashMap<RefA, ObjA>());
	}
	
	/**
	 * permet de retrouve l'objet pour une reference donne.
	 * 
	 * @preconditions : refA est non null.
	 * @postconditions : refA est inchange.
	 * @return l'objet abstrait correspondant a refA si refA est present dans le store.
	 * @return null sinon.
	 */
	public ObjA getObjA(RefA refA)
	{
		for(WeakHashMap<RefA, ObjA> e : intStoreA)
		{
			if(e.containsKey(refA)) 
				return e.get(refA);
		}
		
		return null;
	}
	
	/**
	 * ajoute un nouvel objet abstrait au store.
	 * 
	 * @preconditions : refA et objA sont non null.
	 * @postconditions : refA et objA sont inchanges.
	 * @postconditions : un nouveau couple (refA -> objA) est ajoute au store
	 * s'il n'existait pas de refA dans le store au moment de l'appel a la methode.
	 * @postconditions : sinon c'est qu'il existait un objet abstrait oldObjA tel
	 * que (refA -> oldObjA) se trouve dans le store, l'objet correspondant a
	 * refA est alors mis a jour avec la borne superieur de objA et de oldObjA.
	 */
	public void addObjA(RefA refA, ObjA objA)
	{
		ObjA objA1 = getObjA(refA);
		if(objA1 != null) objA = objA1.ub(objA);
		
		intStoreA.getFirst().put(refA, objA);
	}
	/** 
	 * modie le champs d'un objet abstrait.
	 * 
	 * @preconditions : refA, i, vA sont non null.
	 * @postconditions : refA, i, vA sont inchanges.
	 * @postconditions : le champ i de l'objet correspondant a refA est mis
	 * a jour avec la valeur de la borne superieur de la valeur courante du
	 * champ i getObjA(refA).i et vA.
	 */
	public void setObjetField(RefA refA, Integer i, ValA vA)
	{
		setObjetField(refA, i, vA, true);
	}
	
	
	void setObjetField(RefA refA, Integer i, ValA vA, boolean ub)
	{
		ObjA objA1 = getObjA(refA);
		if(!intStoreA.getFirst().containsKey(refA))
		{
			objA1 = objA1.clone();
			intStoreA.getFirst().put(refA, objA1);
		}
		
		ValA vaIObj = objA1.getField(i);
		vaIObj.setNoInitA(false);
		if(ub) vA = vA.ub(vaIObj);
		objA1.setField(i, vA);
	}

	protected WeakHashMap<RefA, ObjA> 
		compress(LinkedList<WeakHashMap<RefA, ObjA>> list)
	{
		return compress(list.listIterator(list.size()));
	}

	private WeakHashMap<RefA, ObjA> 
		compress(ListIterator<WeakHashMap<RefA, ObjA>> iter)
	{
		WeakHashMap<RefA, ObjA> result = new WeakHashMap<RefA, ObjA>();

		while(iter.hasPrevious()) 
			result.putAll(iter.previous());

		return result;
	}

	/**
	 * calcul la borne superieur entre deux stores.
	 * 
	 * @preconditions : sA est non null.
	 * @postconditions : sA est inchange.
	 * @return : un store abstrait correspondant a la borne superieur 
	 * entre this et sA.
	 */
	public StoreA ub(StoreA sA)
	{
		StoreA stUB = new StoreA();
		stUB.intStoreA.clear();

		ListIterator<WeakHashMap<RefA, ObjA>> iter = 
			intStoreA.listIterator(intStoreA.size());
		ListIterator<WeakHashMap<RefA, ObjA>> iter2 = 
			sA.intStoreA.listIterator(sA.intStoreA.size());
		
	    while(iter.hasPrevious()) 
	    {
	    	WeakHashMap<RefA, ObjA> subSt1 = iter.previous();
	    	WeakHashMap<RefA, ObjA> subSt2 = null;
	    	if(iter2.hasPrevious()) subSt2 = iter2.previous();
	    	
	    	if(subSt1 == subSt2) stUB.intStoreA.addFirst(subSt1);
	    	else break;
	    }
	    // Identifies the longest sequence of equal sub-environments
	    
	    WeakHashMap<RefA, ObjA> temp1 = compress(iter); 
	    WeakHashMap<RefA, ObjA> temp2 = compress(iter2); 
	    WeakHashMap<RefA, ObjA> hashMapUB = new WeakHashMap<RefA, ObjA>();
	    
	    for(RefA r : temp1.keySet())
	    {
	    	if(!temp2.containsKey(r)) 
	    		hashMapUB.put(r, temp1.get(r));
	    	else 
	    		hashMapUB.put(r, temp1.get(r).ub(temp2.get(r)));
	    }
	    
	    for(RefA r : temp2.keySet())
	    {
	    	if(!hashMapUB.containsKey(r)) 
	    		hashMapUB.put(r, temp2.get(r));
	    }
	    
	    stUB.intStoreA.addFirst(hashMapUB);
		
		return stUB;
	}
	
	private LinkedList<WeakHashMap<RefA, ObjA>> getIntStoreA()
	{
		return intStoreA;
	}

}
