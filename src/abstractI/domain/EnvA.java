package abstractI.domain;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;

import slip.interpreter.Identifier;

/**
 * Class that defines an abstract environment (layers architecture)
 * 
 * @author Jian Lian
 * @author Cedric Vanderperren
 */
public class EnvA
{
	private LinkedList<HashMap<Identifier, ValA>> intEnvA;
	
	public EnvA()
	{
		intEnvA = new LinkedList<HashMap<Identifier, ValA>>();
		intEnvA.addFirst(new HashMap<Identifier, ValA>());
	}
	
	public EnvA(EnvA envA)
	{
		intEnvA = new LinkedList<HashMap<Identifier, ValA>>(envA.getIntEnvA());
		intEnvA.addFirst(new HashMap<Identifier, ValA>());
	}
	
	/**
	 * qui permet d'ajouter une nouvelle variable dans l'environnement.
	 * 
	 * @preconditions : i et va sont non null.
	 * @postconditions : le couplet (i -> va) est ajoute aux elements a l'environnement.
	 */
	public void addVal(Identifier i, ValA va){
		intEnvA.getFirst().put(i, va);
	}
	
	/**
	 * recupere la valeur d'une variable dans l'environnement.
	 * 
	 * @preconditions : i est non null.
	 * @postconditions : i est inchange.
	 * @return la valeur abstraite correspondant a l'identicateur i si i est
	 * present dans l'environnement abstrait.
	 * @return une valeur abstraite correspondant a noninit sinon.
	 */
	public ValA getValA(Identifier i)
	{
		for(HashMap<Identifier, ValA> e : intEnvA)
		{
			if(e.containsKey(i)) return e.get(i);
		}
		
		return null;
	}
	
	/**
	 * calcule la relation inferieur egale de la relation bien fondee 
	 * de l'algorithme polyvariant entre deux domaines abstraits.
	 * 
	 * @preconditions : envA2, sA1 et sA2 sont non null.
	 * sA1 est le store abstrait associe a this et sA2 est le store abstrait 
	 * correspondant a l'environnement envA2.
	 * @postconditions : envA2, sA1 et sA2 sont inchanges.
	 * @return true si this et sA1 est bien inferieur ou egale a envA2 et sA2
	 * suivant la relation bien fondee.
	 * @return false sinon.
	 */
	public boolean leq(EnvA envA2, StoreA sA1, StoreA sA2)
	{
		HashMap<Identifier, ValA> compression = compress();
		for(Identifier i : compression.keySet())
		{
			ValA vaEnva2 = envA2.getValA(i);
			if(vaEnva2 == null)
				return false;
			if(!compression.get(i).leq(vaEnva2, sA1, sA2))
				return false;
		}
		
		return true;
	}
	
	protected HashMap<Identifier, ValA> compress()
	{	
		return compress(intEnvA);
	}
	
	protected HashMap<Identifier, ValA> 
		compress(LinkedList<HashMap<Identifier, ValA>> list)
	{
		return compress(list.listIterator(list.size()));
	}
	
	private HashMap<Identifier, ValA> 
		compress(ListIterator<HashMap<Identifier, ValA>> iter)
	{
		HashMap<Identifier, ValA> result = new HashMap<Identifier, ValA>();

		while(iter.hasPrevious()) 
			result.putAll(iter.previous());

		return result;
	}
	
	/**
	 * calcule la borne superieur entre deux environnements abstraits.
	 * 
	 * @preconditions : eA est non null.
	 * @postconditions : eA est inchange.
	 * @return un environnement correspondant a la borne superieur de this et eA.
	 */
	public EnvA ub(EnvA eA)
	{
		EnvA envUB = new EnvA();
		envUB.intEnvA.clear();

		ListIterator<HashMap<Identifier, ValA>> iter = 
			intEnvA.listIterator(intEnvA.size());
		ListIterator<HashMap<Identifier, ValA>> iter2 = 
			eA.intEnvA.listIterator(eA.intEnvA.size());
		
	    while(iter.hasPrevious()) 
	    {
	    	HashMap<Identifier, ValA> subEnv1 = iter.previous();
	    	HashMap<Identifier, ValA> subEnv2 = null;
	    	if(iter2.hasPrevious()) subEnv2 = iter2.previous();
	    	
	    	if(subEnv1 == subEnv2) envUB.intEnvA.addFirst(subEnv1);
	    	else break;
	    }
	    // Identifies the longest sequence of equal sub-environments
	    
	    HashMap<Identifier, ValA> temp1 = compress(iter); 
	    HashMap<Identifier, ValA> temp2 = compress(iter2); 
	    HashMap<Identifier, ValA> hashMapUB = new HashMap<Identifier, ValA>();
	    
	    for(Identifier i : temp1.keySet())
	    {
	    	if(!temp2.containsKey(i)) 
	    		hashMapUB.put(i, temp1.get(i));
	    	else 
	    		hashMapUB.put(i, temp1.get(i).ub(temp2.get(i)));
	    }
	    
	    for(Identifier i : temp2.keySet())
	    {
	    	if(!hashMapUB.containsKey(i)) 
	    		hashMapUB.put(i, temp2.get(i));
	    }
	    
	    envUB.intEnvA.addFirst(hashMapUB);
		
		return envUB;
	}
	
	private LinkedList<HashMap<Identifier, ValA>> getIntEnvA()
	{
		return intEnvA;
	}
}
