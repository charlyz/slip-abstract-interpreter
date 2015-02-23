package slip.interpreter;

public class Int extends Value {

	private int val;

	public Int(int val) {
		this.val = val;
	}

	public String toString() {
		return ""+val;
	}

	public int getVal() {
		return val;
	}

}
