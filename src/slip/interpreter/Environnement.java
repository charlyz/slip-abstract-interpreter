package slip.interpreter;

import java.util.Hashtable;

import slip.exceptions.IndexOutObjectException;
import slip.exceptions.NotInitException;
import slip.exceptions.NotObjectException;

public class Environnement {

	private Hashtable<Identifier, Value> env;

	public Environnement() {
		env = new Hashtable<Identifier, Value>();
	}

	public Value getVal(Identifier i) {
		Value val = env.get(i);
		if (val == null) { //noinit
			return new NoInit();
		}
		return val;
	}

	public void setVal(Identifier i, Value val) {
		env.put(i, val);
	}


	public Ref setObjet(Identifier i, Objet objet) {
		Ref ref = Store.setObjet(objet);
		env.put(i, ref);
		return ref;
	}


	public Value getField(Identifier i, int champ) {
		Value val = env.get(i);
		if (val==null){
			throw new NotInitException();
		} else if (val instanceof Ref) {
			if (Store.getObjet((Ref)env.get(i)).getLength()<champ)
				throw new IndexOutObjectException();
			return Store.getObjet((Ref)env.get(i)).getVal(champ);
		} else {
			throw new NotObjectException();
		}
	}

	public Objet getObjet(Identifier i) {
		Value val = env.get(i);
		if (val==null){
			throw new NotInitException();
		} else if (val instanceof Ref) {
			return Store.getObjet((Ref)env.get(i));
		} else {
			throw new NotObjectException( env.get(i).toString()
					+" is not an object ("+val.getClass()+")");
		}
	}

	public void setField(Identifier i, int champ, Value val) {
		Value valRef = env.get(i);

		if (valRef==null){
			throw new NotInitException();
		} else if (valRef instanceof Ref) {
			if (Store.getObjet((Ref)env.get(i)).getLength() < champ)
				throw new IndexOutObjectException();
			Store.getObjet((Ref)env.get(i)).setVal(champ, val); 
		} else {
			throw new NotObjectException();
		}
	}

	/**
	 * @return the env
	 */
	public Hashtable<Identifier, Value> getEnv() {
		return env;
	}
}
