package slip.interpreter;

public class Identifier {

	private int id;

	public static final Identifier THIS = new Identifier(0);

	public Identifier(int id) {
		this.id =id;
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Identifier))
			return false;
		Identifier other = (Identifier) obj;
		if (id != other.id)
			return false;
		return true;
	}

	public String toString() {
		return ""+id;
	}

}
