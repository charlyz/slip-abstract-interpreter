/*
 * Created on 13 janv. 2010
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package abstractI.domain;

import slip.internal.Stmt;

/**
 * Data structure containing an abstract domain and a stmt
 * 
 * @author cdessart
 */
public class ADxError extends Error
{
	public AbstractDomain da;
	public Stmt nextStmt;
}
