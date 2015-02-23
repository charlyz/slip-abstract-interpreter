package slip.exceptions;

public class IncomparableException extends RuntimeException {
	
	private static final long serialVersionUID = 6402213190821857075L;

	public IncomparableException() {
		super();
	}

	public IncomparableException(String message) {
		super(message);
	}

	public IncomparableException(String message, Throwable cause) {
		super(message, cause);
	}

	public IncomparableException(Throwable cause) {
		super(cause);
	}
}
