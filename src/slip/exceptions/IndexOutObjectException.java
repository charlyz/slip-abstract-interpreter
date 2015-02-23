package slip.exceptions;

public class IndexOutObjectException extends RuntimeException {

	private static final long serialVersionUID = 7667274894882174203L;

	public IndexOutObjectException() {
		super();
	}

	public IndexOutObjectException(String message) {
		super(message);
	}

	public IndexOutObjectException(String message, Throwable cause) {
		super(message, cause);
	}

	public IndexOutObjectException(Throwable cause) {
		super(cause);
	}
}
