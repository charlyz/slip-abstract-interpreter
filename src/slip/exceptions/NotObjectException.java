package slip.exceptions;

public class NotObjectException  extends RuntimeException {

	private static final long serialVersionUID = -3706169340495440122L;

	public NotObjectException() {
		super();
	}

	public NotObjectException(String message) {
		super(message);
	}

	public NotObjectException(String message, Throwable cause) {
		super(message, cause);
	}

	public NotObjectException(Throwable cause) {
		super(cause);
	}
}
