package abstractI.domain;

import slip.internal.Method;
import slip.internal.Stmt;

/**
 * State used in polyvariant algorithm
 * 
 * @author cdessart
 */
public class State 
{
	public Stmt nextStmt;
	public AbstractDomain da;
	public StackA stack;
	public Method meth;
	
	public State(Stmt nextStmt, AbstractDomain da, StackA stack, Method meth) 
	{
		this.nextStmt = nextStmt;
		this.da = da;
		this.stack = stack;
		this.meth = meth;
	}
	
	public String toString()
	{
		return "State[" + "{da: " + da +"}, {meth:" + meth.label +"}, {stmt:" + nextStmt.label+ "}]";
	}
}
