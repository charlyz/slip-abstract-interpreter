import java.util.Collection;
import java.util.LinkedList;
import slip.internal.Call;
import slip.internal.Cexpr;
import slip.internal.Cmd;
import slip.internal.CmdStmt;
import slip.internal.Divide;
import slip.internal.Expr;
import slip.internal.FieldDes;
import slip.internal.I;
import slip.internal.In;
import slip.internal.Method;
import slip.internal.Minus;
import slip.internal.NewAss;
import slip.internal.NormalAss;
import slip.internal.Out;
import slip.internal.Plus;
import slip.internal.Prog;
import slip.internal.Remainder;
import slip.internal.SimpleCall;
import slip.internal.Stmt;
import slip.internal.SuperCall;
import slip.internal.This;
import slip.internal.ThisFieldDes;
import slip.internal.Times;
import slip.internal.VarDes;
import slip.internal.VariableCall;
import slip.interpreter.Identifier;
import abstractI.domain.ADxError;
import abstractI.domain.AbstractDomain;
import abstractI.domain.ObjA;
import abstractI.domain.PZa;
import abstractI.domain.RefA;
import abstractI.domain.StackA;
import abstractI.domain.State;
import abstractI.domain.Transition;
import abstractI.domain.ValA;
import abstractI.domain.ValAxError;
import abstractI.domain.Error;
import slip.internal.*;

/**
 * Class managing abstract interpretation of a slip file
 * 
 * @author Charles-Eric Dessart
 */
public class Interpreter
{
	public Prog prog;
	public Method methMain;
	private LinkedList<State> S;
	private LinkedList<State> R;
	private StackA currentStack;
	private Method currentMethod;

	public static void main(String[] args)
	{
		if(args.length == 0)
		{
			System.err.println("Please give a path of a slip file as argument "
					+ "(without extension)");
			return;
		}

		Settings.initSettings(args[0] + ".properties");

		Prog prog = Compiler.compile(args[0]);
		Interpreter interpreter = new Interpreter(prog);
		StaticAnalysis.initErrorsManagement(Stmt.getLastNumLabel());

		long timeStart = System.currentTimeMillis();
		interpreter.PolyWithSubsomption();
		long timeEnd = System.currentTimeMillis();

		StaticAnalysis.writeInAnalysisFile(args[0]);
		StaticAnalysis.writeInURMethodsFile(prog.getMeths(), args[0]);
		StaticAnalysis.writeInTimeFile(timeEnd - timeStart, args[0]);
	}

	public Interpreter(Prog prog)
	{
		this.prog = prog;
		this.S = new LinkedList<State>();
		this.R = new LinkedList<State>();
	}

	/**
	 * Return main method object
	 * 
	 */
	public Method getMain()
	{
		if(methMain != null) return methMain;

		Method[] methods = prog.getMeths();
		for(int i = 0; i < methods.length; i++)
		{
			if(methods[i].m.equals("main") && (methods[i].arity == 0))
				methMain = prog.getMeths()[i];
		}

		return methMain;
	}

	/**
	 * Return method object with specified params:
	 * @param name
	 * @param maxLevel
	 * @param arity
	 * @return
	 */
	public Method getMeth(String name, int maxLevel, int arity)
	{
		Method[] methods = prog.getMeths();
		Method res = null;
		for(int i = 0; i < methods.length; i++)
		{
			if(methods[i].m.equals(name) && methods[i].level <= maxLevel
					&& methods[i].arity == arity)
			{
				if(res != null && methods[i].level > res.level) res = prog
						.getMeths()[i];
				else if(res == null) res = prog.getMeths()[i];
			}

		}

		return res;
	}

	/**
	 * Polyvariant algorithm with subsomption used to interpret a code
	 * 
	 */
	public void PolyWithSubsomption()
	{
		Method main = getMain();
		State s0 = new State(main.getL(), new AbstractDomain(), new StackA(),
				main);
		S.add(s0);

		while(!S.isEmpty())
		{
			State s = S.pop();
			R.add(s);

			Stmt stmt = s.nextStmt;
			AbstractDomain da = new AbstractDomain(s.da);

			this.currentStack = new StackA(s.stack);
			this.currentMethod = s.meth;

			while(stmt != null)
			{

				int labelInt = stmt.labelInt;
				Error err = null;

				if(stmt instanceof CmdStmt)
				{
					CmdStmt cmd = (CmdStmt) stmt;
					err = A(cmd, da);
					if(err != null)
					{
						if(err.error) StaticAnalysis.setValueWhenError(
								stmt.labelInt, err.comment);
						else StaticAnalysis.setValueWhenNoError(stmt.labelInt);
						stmt = ((ADxError) err).nextStmt;
						da = ((ADxError) err).da;
					}
					else
					{
						StaticAnalysis.setValueWhenNoError(stmt.labelInt);
						stmt = null;
					}
				}
				else if(stmt instanceof CondStmt)
				{
					CondStmt condStmt = (CondStmt) stmt;
					err = B(condStmt, da);
					stmt = null;
				}
				else if(stmt instanceof Method)
				{
					Method methS = (Method) stmt;
					StaticAnalysis.setValueWhenNoError(stmt.labelInt);

					if(methS == main) break;
					// on a affaire à un return foo();
					// pas de prochain stmt, on utilisera un élément de S
					stmt = null;
					Collection<Transition> trans = currentStack
							.getTransitions(currentMethod);

					addStateToSFromTransitions(trans, da
							.getValA(new Identifier(0)), da);
				}
				else
				{
					stmt = null;
				}

				if(err != null)
				{

					if(err.error) StaticAnalysis.setValueWhenError(labelInt,
							err.comment);
					else
					{
						StaticAnalysis.setValueWhenNoError(labelInt);
					}

				}
				else StaticAnalysis.setValueWhenNoError(labelInt);

			}

		}

	}

	/**
	 * Add a state to S if no state s <= to a state in R
	 * @param s
	 */
	public void addStatesToS(State s)
	{
		for(State sR : R)
		{
			AbstractDomain daR = sR.da;
			AbstractDomain daT = s.da;

			if(s.nextStmt == sR.nextStmt)
			{
				if(daT.leq(daR)) return;
			}

		}

		S.add(s);
	}
	
	/**
	 * Evaluate a right expression in a specific abstract domain
	 * @param expr
	 * @param da
	 * @return
	 */
	public ValAxError V(Expr expr, AbstractDomain da)
	{
		ValAxError ve = new ValAxError();
		// expr = x;
		if(expr instanceof VarDes)
		{
			VarDes des = (VarDes) expr;
			ValA va = da.getValA(new Identifier(des.getX()));
			ve.error = va.noInitA();
			if(ve.error)
				ve.comment += "Expression de droite non initialisée, ";

			va.setNoInitA(false);
			ve.va = va;
		}
		// expr = x.i;
		else if(expr instanceof FieldDes)
		{
			FieldDes des = (FieldDes) expr;
			ValA va = da.getValA(new Identifier(des.getX()));
			// mnt on recherche toutes les refs >= i
			ValA res = new ValA();
			for(RefA refa : va.RefA().getRefs())
			{
				int i = refa.val();
				if(i >= des.getI())
				{
					// upperbound des champs i de tous les objets trouvés
					ValA vaFieldI = da.getObjA(refa).getField(des.getI());
					res = res.ub(vaFieldI);
				}
			}

			ve.error = res.noInitA() || !res.containsElements();
			if(ve.error)
				ve.comment += "Expression de droite non initialisée, ";

			va.setNoInitA(false);
			ve.va = va;
		}
		// expr = i;
		else if(expr instanceof I)
		{
			I inte = (I) expr;
			ValA res = new ValA();
			if(inte.getI() < 0) res.setZa(PZa.ub(res.Za(), PZa.MINUS));
			else if(inte.getI() == 0) res.setZa(PZa.ub(res.Za(), PZa.ZERO));
			else if(inte.getI() > 0) res.setZa(PZa.ub(res.Za(), PZa.PLUS));
			ve.va = res;
			ve.error = false;

		}
		// expr = this;
		else if(expr instanceof This)
		{
			ValA va = da.getValA(new Identifier(0));
			ve.error = va.noInitA();
			va.setNoInitA(false);
			ve.va = va;
		}
		// expr = null;
		else if(expr instanceof Null)
		{
			ValA va = new ValA();
			va.setNullA(true);
			ve.error = false;
			ve.va = va;
		}
		// expr = this.i;
		else if(expr instanceof ThisFieldDes)
		{
			ThisFieldDes des = (ThisFieldDes) expr;
			ValA vaThis = da.getValA(new Identifier(0));

			if(!vaThis.containsNoInitA() && !vaThis.containsNullA()
					&& !vaThis.containsZa()
					&& vaThis.RefA().getRefs().size() == 1
					&& vaThis.RefA().getRefs().get(0).val() >= des.getI())
			{
				ve.error = false;
				ve.va = da.getObjA(new RefA(des.getI())).getField(des.getI());
			}
			else
			{
				ve.error = true;
				if(ve.error)
					ve.comment += "Erreur avec la récupération de l'objet, ";
				ve.va = new ValA();
			}
		}
		// expr = sexpr aop sexpr
		else if(expr instanceof Cexpr)
		{
			Cexpr cexpr = (Cexpr) expr;
			ValAxError sexpr1xError = V(cexpr.getExpr1(), da);
			ValA sexpr1 = sexpr1xError.va;

			ValAxError sexpr2xError = V(cexpr.getExpr2(), da);
			ValA sexpr2 = sexpr2xError.va;
			
			int res = -1;
			boolean err = false;
			if(cexpr.getAop() instanceof Divide)
			{
				res = PZa.div(sexpr1.Za(), sexpr2.Za());
				err = PZa.divErr(sexpr1.Za(), sexpr2.Za());
			}
			else if(cexpr.getAop() instanceof Remainder)
			{
				res = PZa.mod(sexpr1.Za(), sexpr2.Za());
				err = PZa.modErr(sexpr1.Za(), sexpr2.Za());
			}
			else if(cexpr.getAop() instanceof Minus)
			{
				res = PZa.minus(sexpr1.Za(), sexpr2.Za());
				err = PZa.minusErr(sexpr1.Za(), sexpr2.Za());
			}
			else if(cexpr.getAop() instanceof Plus)
			{
				res = PZa.plus(sexpr1.Za(), sexpr2.Za());
				err = PZa.plusErr(sexpr1.Za(), sexpr2.Za());
			}
			else if(cexpr.getAop() instanceof Times)
			{
				res = PZa.times(sexpr1.Za(), sexpr2.Za());
				err = PZa.timesErr(sexpr1.Za(), sexpr2.Za());
			}

			ve.va = ValA.newValAWithZa(res);
			ve.error = sexpr1.containsNoInitA() || sexpr1.containsNullA()
					|| sexpr1.containsRefA() || sexpr2.containsNoInitA()
					|| sexpr2.containsNullA() || sexpr2.containsRefA()
					|| err;
			if(ve.error)
				ve.comment += "Erreur du a differents types d'expressions ou a une division par zero, ";

		}

		return ve;
	}

	/**
	 * Add a state s to S from a transition T.
	 * Define the return variable too.
	 * @param trans
	 * @param returnValA
	 * @param currentDA
	 */
	public void addStateToSFromTransitions(Collection<Transition> trans,
			ValA returnValA, AbstractDomain currentDA)
	{
		for(Transition t : trans)
		{
			AbstractDomain daState = t.getDAFrom();
			daState.addVal(t.getReturnVariable(), returnValA);

			for(RefA refReturnVar : returnValA.RefA().getRefs())
			{
				daState.addObjA(refReturnVar, currentDA.getObjA(refReturnVar));
			}
			State sToAdd = new State(t.getNextStmt(), daState, new StackA(
					currentStack), t.getFrom());
			addStatesToS(sToAdd);
		}
	}

	/**
	 * Evaluate a command statement with a specified abstract domain
	 * @param cmdStmt
	 * @param da
	 * @return
	 */
	public ADxError A(CmdStmt cmdStmt, AbstractDomain da)
	{
		Cmd cmd = cmdStmt.getCmd();
		ADxError ade = new ADxError();
		if(cmd instanceof NormalAss)
		{
			NormalAss nAss = (NormalAss) cmd;
			ValAxError vxe = V(nAss.getRight(), da);

			// vérifie si on a pas affaire à un return
			if(nAss.getLeft().getX() == 0 && nAss.getLeft().getI() == 0)
			{
				// pas de prochain stmt, on utilisera un élément de S
				Collection<Transition> trans = currentStack
						.getTransitions(currentMethod);
				addStateToSFromTransitions(trans, vxe.va, da);

				if(cmdStmt.getNext() instanceof Method)
					StaticAnalysis
							.setValueWhenNoError(cmdStmt.getNext().labelInt);
				ade.nextStmt = null;
				return null;
			}

			// x = expr
			if(nAss.getLeft() instanceof VarDes)
			{
				VarDes des = (VarDes) nAss.getLeft();

				da.addVal(new Identifier(des.getX()), vxe.va);
				ade.da = da;
				ade.error = vxe.error;
				ade.nextStmt = cmdStmt.getNext();	
			}
			// x.i = expr
			else if(nAss.getLeft() instanceof FieldDes)
			{
				FieldDes des = (FieldDes) nAss.getLeft();
				ValA vaDes = da.getValA(new Identifier(des.getX()));
				int iField = des.getI();
				boolean foundOneObject = false;
				ade.error = vxe.error; // er = er_expr
				for(RefA refa : vaDes.RefA().getRefs())
				{
					if(refa.val() >= iField)
					{
						foundOneObject = true;
						da.setObjetField(refa, des.getI(), vxe.va);
					}
				}
				if(!foundOneObject) ade.error = true; // er = er_des

				ade.da = da;
				ade.nextStmt = cmdStmt.getNext();	
			}
			// this.i = expr
			else if(nAss.getLeft() instanceof ThisFieldDes)
			{
				ThisFieldDes des = (ThisFieldDes) nAss.getLeft();
				ValAxError vaeDes = V(des, da);
				ValA vaDes = vaeDes.va;
				ade.error = vxe.error; // er = er_expr

				if(!vaDes.containsNoInitA() && !vaDes.containsNullA()
						&& !vaDes.containsZa()
						&& vaDes.RefA().getRefs().size() == 1
						&& vaDes.RefA().getRefs().get(0).val() >= des.getI())
				{
					da.setObjetField(vaDes.RefA().getRefs().get(0), des.getI(),
							vxe.va);
				}
				else
				{
					ade.error = true;
				}
				ade.da = da;
				ade.nextStmt = cmdStmt.getNext();
			}
		}
		// x = new/i
		else if(cmd instanceof NewAss)
		{
			NewAss cmdNA = (NewAss) cmd;
			ObjA obja = new ObjA(cmdNA.getI());

			RefA refa = new RefA(cmdNA.getI());
			ValA va = ValA.newValAWithRefA(refa);
			da.addVal(new Identifier(cmdNA.getX()), va);
			da.addObjA(refa, obja);;

			// Vérifie qu'on a a pas affaire à un return
			if(cmdNA.getX() != 0)
			{
				ade.da = da;
				ade.error = false;
				ade.nextStmt = cmdStmt.getNext();
			}
			else
			// return new/i;
			{
				// pas de prochain stmt, on utilisera un élément de S
				ade.nextStmt = null;
				Collection<Transition> trans = currentStack
						.getTransitions(currentMethod);
				addStateToSFromTransitions(trans, va, da);
				return null;
			}

		}
		else if(cmd instanceof In)
		{
			ValA NZP = ValA.newValAWithZa(PZa.MZP);
			da.addVal(new Identifier(((In) cmd).getX()), NZP);
			ade.da = da;
			ade.error = false;
			ade.nextStmt = cmdStmt.getNext();
		}
		else if(cmd instanceof Out)
		{
			ValA va = da.getValA((new Identifier(((Out) cmd).getX())));
	
			if(!va.containsOnlyZa())
			{
				ade.error = true;
				ade.comment += "Variable pour la commande write ne contient pas que des entiers, ";
			}
			else
			{
				ade.error = false;
			}
			//out.println(va);
			ade.da = da;
			ade.nextStmt = cmdStmt.getNext();

		}
		else if(cmd instanceof Skip)
		{
			ade.da = da;
			ade.error = false;
			ade.nextStmt = cmdStmt.getNext();
		}
		else if(cmd instanceof Call)
		{
			Call cmdCall = (Call) cmd;
			boolean errorCall = false;
			Transition tCurrentMethod = null;

			Method nextMeth = null;
			// crée un nouveau domaine abstrait avec le meme store
			AbstractDomain daCall = new AbstractDomain(da.getStoreA());

			int j = 0;
			for(int i : cmdCall.getAp())
			{
				j++;
				Identifier id = new Identifier(j);
				daCall.addVal(id, da.getValA(new Identifier(i)));
			}

			if(cmd instanceof SimpleCall)
			{
				nextMeth = getMeth(cmdCall.getM(), -1, cmdCall.getAp().length);
				
				// on indique où il faudra revenir quand on aura un return
				if(nextMeth != null)
				{
					tCurrentMethod = getTransition(currentMethod, nextMeth);
					if(tCurrentMethod == null || !(da.leq(tCurrentMethod.getDAFrom())))
					{
						currentStack.setTransition(currentMethod, da, cmdStmt.getNext(), new Identifier(cmdCall.getX()), nextMeth);
						State state = new State(nextMeth.getL(), daCall, currentStack, nextMeth);
						addStatesToS(state);
					}
					else
					{
						State state = new State(cmdStmt.getNext(), tCurrentMethod.getDAFrom().ub(da), currentStack, currentMethod);
						addStatesToS(state);

					}
					nextMeth = null;
				}
				else errorCall = true;
			}
			else if(cmd instanceof SuperCall)
			{
				nextMeth = getMeth(cmdCall.getM(), currentMethod.level - 1, cmdCall.getAp().length);
				Identifier id = new Identifier(0);
				daCall.addVal(id, da.getValA(id));
				if(nextMeth != null)
				{
					tCurrentMethod = getTransition(currentMethod, nextMeth);
					if(tCurrentMethod == null || !(da.leq(tCurrentMethod.getDAFrom())))
					{
						currentStack.setTransition(currentMethod, da, cmdStmt.getNext(), new Identifier(cmdCall.getX()), nextMeth);
						State state = new State(nextMeth.getL(), daCall, currentStack, nextMeth);
						addStatesToS(state);
					}
					else
					{
						State state = new State(cmdStmt.getNext(), tCurrentMethod.getDAFrom().ub(da), currentStack, currentMethod);
						addStatesToS(state);
					}
					nextMeth = null;
				}
				else errorCall = true;

			}
			else if(cmd instanceof VariableCall)
			{
				VariableCall cmdVariableCall = (VariableCall) cmd;
				ValA vaTarget = da.getValA(new Identifier(cmdVariableCall.getTarget()));
				boolean foundVariableCall = false;
				Identifier id = new Identifier(0);
				daCall.addVal(id, vaTarget);
				for(RefA refATarget : vaTarget.RefA().getRefs())
				{
					nextMeth = getMeth(cmdCall.getM(), da.getObjA(refATarget).getRef(), cmdCall.getAp().length);

					if(nextMeth != null)
					{
						foundVariableCall = true;
						tCurrentMethod = getTransition(currentMethod, nextMeth);

						if(tCurrentMethod == null || !(da.leq(tCurrentMethod.getDAFrom())))
						{
							currentStack.setTransition(currentMethod, da, cmdStmt.getNext(), new Identifier(cmdCall .getX()), nextMeth);
							State state = new State(nextMeth.getL(), daCall, currentStack, nextMeth);
							addStatesToS(state);
						}
						else
						{
							State state = new State(cmdStmt.getNext(), tCurrentMethod.getDAFrom().ub(da), currentStack, currentMethod);
							addStatesToS(state);
						}
					}
					nextMeth = null;
				}
				if(!foundVariableCall) errorCall = true;
			}

			if(errorCall)
			{
				ade.da = da;
				ade.error = true;
				ade.nextStmt = cmdStmt.getNext();
				ade.comment += "Impossible de trouver la methode d'appel, ";
			}

		}
		return ade;
	}

	/**
	 * Get transition of stack from a method to another one.
	 * @param from
	 * @param to
	 * @return
	 */
	public Transition getTransition(Method from, Method to)
	{
		Transition res = null;
		Collection<Transition> trans = currentStack.getTransitions(to);
		for(Transition t : trans)
		{
			if(t.getFrom() == currentMethod)
			{
				res = t;
				break;
			}
		}
		return res;
	}

	/**
	 * Define expr valA as a new valA with pza values in abstract domain da.
	 * @param expr
	 * @param da
	 * @param pza
	 */
	public void Ass(Expr expr, AbstractDomain da, int pza)
	{
		if(expr instanceof VarDes)
		{
			ValA vaX = ValA.newValAWithZa(pza);
			da.addVal(new Identifier(((VarDes) expr).getX()), vaX);;
		}
		else if(expr instanceof FieldDes || expr instanceof ThisFieldDes)
		{
			ValA vaX = V(expr, da).va;
			FieldDes exprFD = (FieldDes) expr;
			for(RefA refAX : vaX.RefA().getRefs())
			{
				if(refAX.val() >= exprFD.getI())
				{
					ValA vaRefA = ValA.newValAWithZa(pza);
					da.setObjetFieldWithoutUB(refAX, exprFD.getI(), vaRefA);
				}
			}
		}
	}

	/**
	 * Evaluate condition statement with a specified abstract domain
	 * @param condStmt
	 * @param da
	 * @return
	 */
	public Error B(CondStmt condStmt, AbstractDomain da)
	{
		Error er = new Error();
		Cond cond = condStmt.getCond();
		ValAxError vaeL1 = V(cond.getExpr1(), da);
		ValAxError vaeL2 = V(cond.getExpr2(), da);

		if(condStmt.getCond().getCop() instanceof Equal)
		{

			ValA L1 = vaeL1.va;
			ValA L2 = vaeL2.va;
			ValA P = L1.intersection(L2);
			ValA vaZeroNull = ValA.newValAWithZa(PZa.ZERO);
			vaZeroNull.setNullA(true);
			ValA NPZnull =  ValA.newValAWithZa(PZa.MZP);
			NPZnull.setNullA(true);
			ValA Q1 = NPZnull.except(P.intersection(vaZeroNull));
			ValA Q2 = NPZnull.except(P.intersection(vaZeroNull));

			boolean sAdded = false;

			if(P.containsZa() || P.containsOnlyNullA())
			{
				// nouvelle couche
				AbstractDomain daT = new AbstractDomain(da);
				StackA currentStackT = new StackA(currentStack);

				sAdded = true;
				// on ajoute un seul nouvel état dans le cas où == est vrai
				Ass(cond.getExpr1(), daT, P.Za());
				Ass(cond.getExpr2(), daT, P.Za());

				addStatesToS(new State(condStmt.getLtrue(), daT, currentStackT, currentMethod));
			}

			if(Q1.containsZa() && Q2.containsZa())
			{
				// nouvelle couche
				AbstractDomain daF = new AbstractDomain(da);
				StackA currentStackF = new StackA(currentStack);

				sAdded = true;
				// on ajoute un seul nouvel état dans le cas où == est faux
				Ass(cond.getExpr1(), daF, Q1.Za());
				Ass(cond.getExpr2(), daF, Q2.Za());
				addStatesToS(new State(condStmt.getLfalse(), daF, currentStackF, currentMethod));
			}

			// Aucun état ajouté, on force l'ajout avec les valA non
			// modifiées
			if(!sAdded)
			{
				// nouvelle couche
				AbstractDomain daNA = new AbstractDomain(da);
				StackA currentStackNA = new StackA(currentStack);

				Ass(cond.getExpr1(), daNA, L1.Za());
				Ass(cond.getExpr2(), daNA, L2.Za());

				addStatesToS(new State(condStmt.getLtrue(), daNA, currentStackNA, currentMethod));

				addStatesToS(new State(condStmt.getLfalse(), daNA, currentStackNA, currentMethod));

			}

			ValA L1uL2 = L1.ub(L2);

			if(L1uL2.containsOnlyZa() || L1uL2.containsOnlyNullA()
					|| L1uL2.containsOnlyRefA()) er.error = false;
			else
			{
				er.error = true;
				er.comment += "Les expressions a comparer sont de types differents, ";
			}

		}
		else if(condStmt.getCond().getCop() instanceof Less)
		{
			ValA L1prime = vaeL1.va;
			ValA L2prime = vaeL2.va;

			ValA vaZaFull =  ValA.newValAWithZa(PZa.MZP);
			ValA L1 = L1prime.intersection(vaZaFull);
			ValA L2 = L2prime.intersection(vaZaFull);

			ValA Pt1 = new ValA();
			ValA Qt1 = new ValA();
			ValA Pt2 = new ValA();
			ValA Qt2 = new ValA();
			ValA Pf1 = new ValA();
			ValA Qf1 = new ValA();
			ValA Pf2 = new ValA();
			ValA Qf2 = new ValA();

			LinkedList<Integer> l1Zas = PZa.zaList(L1.Za());
			LinkedList<Integer> l2Zas = PZa.zaList(L2.Za());

			for(Integer q : l2Zas)
			{
				Qt1.setZa(PZa.ub(Qt1.Za(), q.intValue()));
				Qf1.setZa(PZa.ub(Qf1.Za(), q.intValue()));
			}
			for(Integer p : l1Zas)
			{
				Pt2.setZa(PZa.ub(Pt2.Za(), p.intValue()));
				Pf2.setZa(PZa.ub(Pf2.Za(), p.intValue()));
			}

			for(Integer p : l1Zas)
				for(Integer q : l2Zas)
				{
					int r = PZa.cPrime(p, q);
					// on elimine les mauvais valbase de Pf2 et Qf1 // on
					// remplit Pt1 et Qt2;
					if(r == 1 /* || r==2 */)
					{
						Pf2.setZa(PZa.except(Pf2.Za(), p.intValue()));
						Qf1.setZa(PZa.except(Qf1.Za(), q.intValue()));
						Pt1.setZa(PZa.ub(Pt1.Za(), p.intValue()));
						Qt2.setZa(PZa.ub(Qt2.Za(), q.intValue()));
					}
					// on elimine les mauvais valbase de Qt1 et Pt2 // on
					// remplit Pf1 et Qf2
					if(r == 0 /* || r==2 */)
					{
						Qt1.setZa(PZa.except(Qt1.Za(), q.intValue()));
						Pt2.setZa(PZa.except(Pt2.Za(), p.intValue()));
						Pf1.setZa(PZa.ub(Pf1.Za(), p.intValue()));
						Qf2.setZa(PZa.ub(Qf2.Za(), q.intValue()));
					}
				}

			// on ajoute deux nouvels etats dans le cas ou < est vrai
			da = addStateToSFromCondLess(Pt1, Qt1, cond, da, condStmt.getLtrue(), false);
			// System.out.println("h1T: " + da);
			da = addStateToSFromCondLess(Pt2, Qt2, cond, da, condStmt.getLtrue(), false);
			// System.out.println("h21T: " + da);
			// on ajoute deux nouvels etats dans le cas ou < est faux
			da = addStateToSFromCondLess(Pf1, Qf1, cond, da, condStmt.getLfalse(), false);
			// System.out.println("h1F: " + da);
			da = addStateToSFromCondLess(Pf2, Qf2, cond, da, condStmt.getLfalse(), false);
			// System.out.println("h2F: " + da);

			// on verifie qu'on a trouve des etats, sinon on utilise les expr
			// telles quelles
			if((!Pt1.containsZa() || !Qt1.containsZa())
					&& (!Pt2.containsZa() || !Qt2.containsZa())
					&& (!Pf1.containsZa() || !Qf1.containsZa())
					&& (!Pt2.containsZa() || !Qt2.containsZa()))
			{
				da = addStateToSFromCondLess(L1, L2, cond, da, condStmt
						.getLtrue(), true);
				da = addStateToSFromCondLess(L1, L2, cond, da, condStmt
						.getLfalse(), true);
			}

			if(!L1prime.containsOnlyZa() || !L2prime.containsOnlyZa())
			{
				er.error = true;
				er.comment += "Les expressions a comparer sont de types differents, ";
			}
			else
			{
				er.error = false;
			}
		}
		return er;
	}

	/**
	 * @param v1
	 * @param v2
	 * @param cond
	 * @param da
	 * @param stmt
	 * @param forceAdd
	 * @return
	 */
	public AbstractDomain addStateToSFromCondLess(ValA v1, ValA v2, Cond cond,
			AbstractDomain da, Stmt stmt, boolean forceAdd)
	{
		if(forceAdd || (v1.containsZa() && v2.containsZa()))
		{
			// nouvelle couche
			da = new AbstractDomain(da);
			currentStack = new StackA(currentStack);
			Ass(cond.getExpr1(), da, v1.Za());
			Ass(cond.getExpr2(), da, v2.Za());
			addStatesToS(new State(stmt, da, currentStack, currentMethod));

		}
		return da;
	}

}
