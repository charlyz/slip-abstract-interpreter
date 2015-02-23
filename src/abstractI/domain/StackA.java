package abstractI.domain;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;

import slip.internal.Method;
import slip.internal.Stmt;
import slip.interpreter.Identifier;

/**
 * Class that defines an abstract stack (layers architecture)
 * 
 * @author Jian Lian
 * @author Cedric Vanderperren
 */
public class StackA
{	
	private LinkedList<HashMap<Method, HashMap<Method, Transition>>> intTrans;
	// HashMap of two dimensions : <Methodto, <MethodFrom, transition>>
	
	public StackA()
	{
		intTrans = new LinkedList<HashMap<Method,HashMap<Method,Transition>>>();
		intTrans.addFirst(new HashMap<Method, HashMap<Method, Transition>>());
	}
	
	public StackA(StackA stA)
	{
		intTrans = new LinkedList<HashMap<Method, 
					HashMap<Method,Transition>>>((stA.intTrans));
		intTrans.addFirst(new HashMap<Method, HashMap<Method, Transition>>());
	}

	/**
	 * ajoute ou met a jour une transition dans la pile abstraite.
	 * 
	 * @preconditions : fromNode, dAFrom, nextStmt, returnVariable, toNode sont non null.
	 * @postconditions : fromNode, dAFrom, nextStmt, returnVariable, to Node sont inchanges.
	 * @postconditions : s'il n'existe pas de transition t tels que : 
	 * t = (fromNode', dAFrom', nextStmt', returnVariable', toNode'), fromNode=fromNode',
	 * toNode=toNode' et tel que t appartient à transitionsList 
	 * (la liste des transitions de la pile abstraite) 
	 * alors une nouvelle transition contenant les parametres precites est ajoute a transitionsList. 
	 * sinon c'est qu'il existe une transition t tel que fromNode=fromNode' et toNode=toNode', 
	 * le domaine abstrait de t est mis a jour avec la borne superieur de dAFrom et dAFrom'.
	 */
	public void setTransition(Method fromNode, AbstractDomain dAFrom, 
			Stmt nextStmt, Identifier returnVariable, Method toNode)
	{
		Transition transCurrent = new Transition(fromNode, dAFrom, nextStmt, 
				returnVariable, toNode);
		
		for(HashMap<Method, HashMap<Method,Transition>> hashMapTrans : intTrans)
		{
			if(hashMapTrans.get(toNode) != null && 
					hashMapTrans.get(toNode).get(fromNode) != null)
			{
				transCurrent.setDAFrom(transCurrent.getDAFrom().
					ub(hashMapTrans.get(toNode).get(fromNode).getDAFrom()));
				break;
			}
		} 
		
		HashMap<Method, Transition> mapTrans = intTrans.getFirst().get(toNode);
		if(mapTrans == null) mapTrans = new HashMap<Method, Transition>();
		mapTrans.put(fromNode, transCurrent);
		
		intTrans.getFirst().put(toNode, mapTrans);
	}
	
	/**
	 * renvoie toutes les transitions dans laquelle toNode est la methode appelee.
	 * 
	 * @preconditions : toNode est non null.
	 * @postconditions : toNode est inchange.
	 * @return une collection de transition ct tel que pour tout t appartenant à ct, 
	 * t = (methodeFrom -> (methodeTo -> transitionFromTo)) et methodeTo=toNode.
	 */
	public Collection<Transition> getTransitions(Method toNode)
	{
		HashMap<Method, Transition> result = new HashMap<Method, Transition>();	
		ListIterator<HashMap<Method, HashMap<Method,Transition>>> iter = 
			intTrans.listIterator(intTrans.size());
		
		while(iter.hasPrevious()) 
		{
			HashMap<Method, HashMap<Method, Transition>> hM = iter.previous();
			
			if(hM.get(toNode) != null)
				result.putAll(hM.get(toNode));
		}
		
		return result.values();
	}
}
