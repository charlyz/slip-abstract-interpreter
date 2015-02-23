package slip.interpreter;

import slip.exceptions.IndexOutObjectException;

public class Objet {

	private int length;
	private Value[] field;

	public Objet(int longueur) {
		this.length = longueur;
		field = new Value[longueur];
		for (int i = 0; i != field.length; i++) {
			field[i] = new NoInit();
		}
	}

	public int getLength() {
		return length;
	}

	public Value getVal(int i){

		if (i > this.length || i < 1){
			throw new IndexOutObjectException
			("IndexOutObjectException: Field "+i+" out of bound");
		}

		return field[i-1];

	}

	public void setVal(int i, Value val) {
		field[i-1] = val;
	}

}
