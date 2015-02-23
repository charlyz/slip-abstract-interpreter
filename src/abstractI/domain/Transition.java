package abstractI.domain;

import slip.internal.Method;
import slip.internal.Stmt;
import slip.interpreter.Identifier;

/**
 * Class that defines a transition for the abstract stack
 * 
 * @author Jian Lian
 * @author Cedric Vanderperren
 */
public class Transition
{
	private Method from;
	private Method to;
	
	private Stmt nextStmt;
	private Identifier returnVariable;
	private AbstractDomain dAFrom;
	
	public Transition(Method from, AbstractDomain dAFrom, Stmt nextStmt, 
			Identifier returnVariable, Method to)
	{
		this.from = from;
		this.to = to;
		this.nextStmt = nextStmt;
		this.returnVariable = returnVariable;
		this.dAFrom = dAFrom;
	}

	public Method getFrom()
	{
		return from;
	}

	public Method getTo()
	{
		return to;
	}

	public Stmt getNextStmt()
	{
		return nextStmt;
	}

	public Identifier getReturnVariable()
	{
		return returnVariable;
	}

	public AbstractDomain getDAFrom()
	{
		return dAFrom;
	}

	public void setDAFrom(AbstractDomain from)
	{
		dAFrom = from;
	}
}
