package slip.exceptions;

public class NotInitException extends RuntimeException {

	private static final long serialVersionUID = 7081067270963098800L;

	public NotInitException() {
		super();
	}

	public NotInitException(String message) {
		super(message);
	}

	public NotInitException(String message, Throwable cause) {
		super(message, cause);
	}

	public NotInitException(Throwable cause) {
		super(cause);
	}
}
