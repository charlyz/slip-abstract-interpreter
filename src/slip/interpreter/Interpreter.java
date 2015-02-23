package slip.interpreter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Stack;

import slip.exceptions.DivisionByZeroException;
import slip.exceptions.IncomparableException;
import slip.exceptions.IndexOutObjectException;
import slip.exceptions.InterpreterException;
import slip.exceptions.NotInitException;
import slip.exceptions.NullValueException;
import slip.exceptions.UndefinedMethodException;
import slip.internal.Aop;
import slip.internal.Call;
import slip.internal.Cexpr;
import slip.internal.CmdStmt;
import slip.internal.Cond;
import slip.internal.CondStmt;
import slip.internal.Cop;
import slip.internal.Equal;
import slip.internal.Expr;
import slip.internal.FieldDes;
import slip.internal.I;
import slip.internal.In;
import slip.internal.Less;
import slip.internal.Method;
import slip.internal.NewAss;
import slip.internal.NormalAss;
import slip.internal.Out;
import slip.internal.Prog;
import slip.internal.Sexpr;
import slip.internal.SimpleCall;
import slip.internal.Stmt;
import slip.internal.SuperCall;
import slip.internal.This;
import slip.internal.ThisFieldDes;
import slip.internal.VarDes;
import slip.internal.VariableCall;

public class Interpreter
{
	private static Method[] methods;
	private static Method currentMethod;
	private static Stack<Method> callingMethods;
	private static Environnement currentEnvironnement;
	private static PrintStream out;
	private static BufferedReader in;
	private static Stmt currentStmt;

	/**
	 * Execute prog
	 * @pre prog i!= null and have correct syntax
	 * @param prog is a set of instruction in an abstract syntax of SLIP
	 */
	static public void interprete(Prog prog)
	{
		methods = prog.getMeths();
		currentEnvironnement = new Environnement();
		callingMethods = new Stack<Method>();
		in = new BufferedReader(new InputStreamReader(System.in));
		out = new PrintStream(System.out);

		currentMethod = null;
		for (int i = 0; i < methods.length; i++)
		{
			if (methods[i].m.equals("main") && (methods[i].arity == 0))
				currentMethod = prog.getMeths()[i];
		}

		if(currentMethod == null)
			throw new UndefinedMethodException("Main");
		else  callMeth(currentMethod);
	}

	/**
	 * Execution of SLIP code method
	 * @pre method != null
	 * @param method to execute
	 * @return 	- the result of execution of the SLIP code
	 * 			- null in case of error
	 * 			- no init otherwise
	 */
	private static Value callMeth(Method method)
	{
		Stmt stmtInit = method.getLabelInit();

		CmdStmt cmdstmtCourant = null;
		currentStmt = stmtInit;

		while (currentStmt instanceof CmdStmt
				|| currentStmt instanceof CondStmt)
		{
			if (currentStmt instanceof CmdStmt)
			{
				cmdstmtCourant = (CmdStmt) currentStmt;

				if (cmdstmtCourant.getCmd() instanceof NormalAss
						&& ((NormalAss) cmdstmtCourant.getCmd()).getLeft()
						.getX() == 0
						&& ((NormalAss) cmdstmtCourant.getCmd()).getLeft()
						.getI() == 0) {
					return interpretCmd((NormalAss) cmdstmtCourant.getCmd());
				}
				if (cmdstmtCourant.getCmd() instanceof Call 
						&& ((Call) cmdstmtCourant.getCmd()).getX() == 0) {
					return interpretCmd((Call) cmdstmtCourant.getCmd());
				}
				if (cmdstmtCourant.getCmd() instanceof NewAss
						&& ((NewAss) cmdstmtCourant.getCmd()).getX() == 0) {
					return interpretCmd((NewAss) cmdstmtCourant.getCmd());
				}
				// Affectation du resultat

				interpret(cmdstmtCourant);
				currentStmt = cmdstmtCourant.getNext();
			}
			else if (currentStmt instanceof CondStmt)
			{
				interpret((CondStmt) currentStmt);
			}
		}
		if (currentStmt instanceof Method) {
			return new NoInit();
		}
		return null;
	}

	/**
	 * Execute a SLIP command
	 * @pre cmdStmt != null
	 * @param cmdStmt, a instruction on SLIP
	 */
	private static void interpret(CmdStmt cmdStmt)
	{
		if (cmdStmt.getCmd() instanceof NormalAss)
		{
			interpretCmd((NormalAss) cmdStmt.getCmd());
		}
		if (cmdStmt.getCmd() instanceof In)
		{
			interpretCmd((In) cmdStmt.getCmd());
		}
		if (cmdStmt.getCmd() instanceof Out)
		{
			interpretCmd((Out) cmdStmt.getCmd());
		}
		if (cmdStmt.getCmd() instanceof NewAss)
		{
			interpretCmd((NewAss) cmdStmt.getCmd());
		}
		if (cmdStmt.getCmd() instanceof Call)
		{
			interpretCmd((Call) cmdStmt.getCmd());
		}
	}

	/**
	 * Interpret a condition statement
	 * @param condStmt instruction on SLIP
	 * @pre : condStmt != null
	 */
	private static void interpret(CondStmt condStmt)
	{
		boolean cond = interpretCond(condStmt.getCond());
		if (cond) currentStmt = condStmt.getLtrue();
		else currentStmt = condStmt.getLfalse();
	}

	/**
	 * Interpret a condition
	 * @param cond condition to interpret
	 * @return true if cond is true, false otherwise
	 */
	private static boolean interpretCond(Cond cond) {

		Value valExpr1 = getExpValue(cond.getExpr1());
		Value valExpr2 = getExpValue(cond.getExpr2());
		Cop cop = cond.getCop();
		if (valExpr1.getClass() != valExpr2.getClass())
		{
			if (valExpr1 instanceof Null || valExpr2 instanceof Null) {
				if (cop instanceof Equal) {
					return false;
				}
			}

			throw new IncomparableException("Condition with not " +
					"comparable expression, "
					+valExpr1.toString()+" and "
					+valExpr2.toString()+"in"
					+cond.toString());
		}
		else
		{
			if (valExpr1 instanceof Int && valExpr2 instanceof Int)
			{
				Int i = (Int) valExpr1;
				Int j = (Int) valExpr2;
				if (cop instanceof Less) return i.getVal() < j.getVal();
				else
				{
					return i.getVal() == j.getVal();
				}
			}
			else if (valExpr1 instanceof Ref && valExpr2 instanceof Ref)
			{
				Ref ref1 = (Ref) valExpr1;
				Ref ref2 = (Ref) valExpr2;
				if (cop instanceof Less) {
					throw new IncomparableException(
							"Impossible to compare (\"<\") two objects" +
							" in the condition : "+cond.toString());
				}
				else
				{
					return ref1 == ref2;
				}
			}
			else if (valExpr1 instanceof Null && valExpr2 instanceof Null) {
				if (cop instanceof Less) {
					throw new NullValueException("Null Value in condition: "+cond);
				}
				else
				{
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Execute the call, create new local environnement for the method
	 *  and restore previous before return of the method
	 * @pre call != null
	 * @post the currentEnvironnement contains result of calling method
	 * @param call instruction on SLIP to call and method
	 * @return the result of execution of the call
	 */
	private static Value interpretCmd(Call call)
	{
		int maxLevel = -1;
		if(call instanceof VariableCall)
			maxLevel = currentEnvironnement.getObjet(new Identifier(
					((VariableCall)call).getTarget())).getLength();
		else if(call instanceof SuperCall) {
			maxLevel = currentMethod.level-1;
		}

		Environnement envCopy = currentEnvironnement;
		currentEnvironnement = new Environnement();
		// Create the contextual environnement for the method
		int[] arguments = call.getAp();
		for (int i = 0; i < arguments.length; i++)
		{
			Identifier id = new Identifier(i + 1);
			currentEnvironnement.setVal(id, envCopy.getVal(new Identifier(
					arguments[i])));
		}
		Value v = null;
		if(call instanceof VariableCall)
			v = envCopy.getVal(new Identifier(((VariableCall)call).getTarget()));
		else if(call instanceof SuperCall)
			v = envCopy.getVal(Identifier.THIS);
		if(maxLevel>=0) currentEnvironnement.setVal(Identifier.THIS, v);
		// Add this to environnement

		int currentLevelMethod = -2;
		callingMethods.push(currentMethod);
		for (int i = 0; i < methods.length; i++)
		{
			if (methods[i].m.equals(call.getM()) &&
					methods[i].arity == call.getAp().length &&
					methods[i].level<=maxLevel && 
					methods[i].level>currentLevelMethod)
			{
				currentMethod = methods[i];
				currentLevelMethod = methods[i].level;
			}
		}
		
		if(currentLevelMethod == -2)
			throw new UndefinedMethodException("No method found with name "
					+ call.getM()+ " and with "+call.getAp().length+" parameters" +
					" and with an appropriate level" +
			" or a dynamic method has been called on no object");
		
		if(!(call instanceof SimpleCall) && currentMethod.level==-1) 
			throw new InterpreterException("Static method called on an object");

		Value res = callMeth(currentMethod);
		// Execution of method's code and catch result

		currentMethod = callingMethods.pop();
		currentEnvironnement = envCopy;
		currentEnvironnement.setVal(new Identifier(call.getX()), res);
		// Return of the method : previous environnement with result
		return res;
	}

	/**
	 * Add a entry to the currentEnvironnement with 
	 * 	the left expression as key and right expression for the value
	 * @pre normalAss != null
	 * @param normalAss
	 * @return the right expression of  normalAss
	 */
	private static Value interpretCmd(NormalAss normalAss) {

		Value val = getExpValue(normalAss.getRight());

		if (val instanceof NoInit) {
			currentEnvironnement.setVal(new Identifier(
					normalAss.getLeft().getX()),
					val);

			throw new NotInitException("NotInitException in : " + normalAss+
					" : the variable "+normalAss.getRight()+" is not initialized");
		}
		else
		{
			if (normalAss.getLeft().getI() == 0)
			{
				currentEnvironnement.setVal(new Identifier(normalAss.getLeft()
						.getX()), val);
			}
			else {
				if (normalAss.getLeft().getX() == 0) { //this
					if (currentMethod.level < normalAss.getLeft().getI()) {
						throw new IndexOutObjectException
						("IndexOutObjectException: Field "+normalAss.getLeft().getI()+
								" greather than level of method "+currentMethod.m+
								" (level "+currentMethod.level+")");
					}
				}
				currentEnvironnement.setField(new Identifier(normalAss
						.getLeft().getX()), normalAss.getLeft().getI(), val);
			}
		}

		return val;
	}

	/**
	 * @pre newAss != null
	 * @post a new object is added to currentEnvironnement 
	 * @param newAss
	 * @return the new environnement
	 */
	private static Value interpretCmd(NewAss newAss)
	{
		return currentEnvironnement.setObjet(new Identifier(newAss.getX()),
				new Objet(newAss.getI()));
	}

	/**
	 * 
	 * @param expr
	 * @return
	 */
	private static Value getExpValue(Expr expr)
	{
		if (expr instanceof Sexpr)
		{
			return getExpValue((Sexpr) expr);
		}

		if (expr instanceof Cexpr)
		{
			return getExpValue((Cexpr) expr);
		}
		return null;
	}

	/**
	 * Get the value of the expression
	 * @pre sexpr != null
	 * @param sexpr
	 * @return the value of sexpr
	 */
	private static Value getExpValue(Sexpr sexpr)
	{
		if (sexpr instanceof I)
		{
			I i = (I) sexpr;
			return new Int(i.getI());
		}
		if (sexpr instanceof This)
		{
			if (currentMethod.level == -1) {
				throw new InterpreterException("this cannot be used in a static method");
			}
			return currentEnvironnement.getVal(new Identifier(0));
		}
		if (sexpr instanceof slip.internal.Null)
		{
			return new Null();
		}
		if (sexpr instanceof VarDes)
		{
			VarDes tmp = (VarDes) sexpr;
			return currentEnvironnement.getVal(new Identifier(tmp.getX()));
		}
		if (sexpr instanceof FieldDes)
		{
			FieldDes tmp = (FieldDes) sexpr;
			return currentEnvironnement.getField(new Identifier(tmp.getX()),
					tmp.getI());
		}
		if (sexpr instanceof ThisFieldDes)
		{
			if (currentMethod.level == -1) {
				throw new InterpreterException("this cannot be used in a static method");
			}
			ThisFieldDes tmp = (ThisFieldDes) sexpr;
			if (currentMethod.level < tmp.getI()) {
				throw new IndexOutObjectException
				("IndexOutObjectException: Field "
						+tmp.getI()+" greather than level of method "
						+currentMethod.m+" (level "+currentMethod.level+")");
			}
			return currentEnvironnement.getField(new Identifier(0), tmp.getI());
		}
		return null;
	}

	/**
	 * Get the value of the expression
	 * @pre cexpr != null
	 * @param cexpr
	 * @return the value of cexpr
	 */
	private static Value getExpValue(Cexpr cexpr) {
		Sexpr sexpr1 = cexpr.getExpr1();
		Sexpr sexpr2 = cexpr.getExpr2();		
		Value val1 = (Value) getExpValue(sexpr1);
		Value val2 = (Value) getExpValue(sexpr2);
		if(val1 instanceof Int && val2 instanceof Int){
			Int int1 = (Int) getExpValue(sexpr1);
			Int int2 = (Int) getExpValue(sexpr2);
			Aop aop = cexpr.getAop();
			if (aop.getAop() == '+') {
				return new Int (int1.getVal()+int2.getVal());
			}
			else if (aop.getAop() == '-') {
				return new Int (int1.getVal()-int2.getVal());
			}
			else if (aop.getAop() == '*') {
				return new Int (int1.getVal()*int2.getVal());
			}
			else if (aop.getAop() == '/') {
				if (int2.getVal() != 0) {
					return new Int (int1.getVal()/int2.getVal());
				}
				else {
					throw new DivisionByZeroException("Division by zero :"+
							cexpr.toString());
				}
			}
			else if (aop.getAop() == '%') {
				if (int2.getVal() != 0) {
					return new Int (int1.getVal()%int2.getVal());
				}
				else {
					throw new DivisionByZeroException("Division by zero :"+
							cexpr.toString());
				}

			}
		} else {
			if (!(val1 instanceof Int))
				throw new InterpreterException("Value in "+sexpr1.toString()+
						" is not an Int, it is a "+val1);
			else if(!(val2 instanceof Int))
				throw new InterpreterException("Value in "+sexpr2.toString()+
						" is not an Int, it is a "+val2);
			else
				throw new InterpreterException(sexpr1.toString() +" and "+
						sexpr2.toString()
						+" are not an Int");
		}

		return null;
	}

	/**
	 * Print the cmd value to stdout
	 * @pre cmd != null
	 * @post a new value is add to stdout
	 * @param cmd
	 */
	private static void interpretCmd(Out cmd)
	{
		Value val = currentEnvironnement.getVal(new Identifier(cmd.getX()));
		if (val instanceof Int) { 
			out.println(val);
		}
		else throw new InterpreterException("Writing error, "
				+ cmd.toString() + " is not an Int" );
	}

	/**
	 * Get a integer from user
	 * @pre cmd != null
	 * @post currentEnvironnement contains a 
	 * new entry with the Int get from user
	 * @param cmd
	 */
	private static void interpretCmd(In cmd)
	{
		boolean read = false;
		int inValue = 0;
		while (!read) {
			try
			{
				String inputLine = in.readLine();
				inValue = Integer.parseInt(inputLine);
				read = true;
			}
			catch (NumberFormatException e)
			{
				System.err.println("Error : the read value is not an integer");
			}
			catch (IOException e)
			{
				System.err.println("Error : IO problem");
			}
		}
		currentEnvironnement.setVal(new Identifier(cmd.getX()), new Int(
				inValue));
	}
}
