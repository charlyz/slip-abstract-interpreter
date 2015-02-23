package slip.exceptions;

public class NullValueException extends RuntimeException {

	private static final long serialVersionUID = 2559527333559512882L;

	public NullValueException() {
		super();
	}

	public NullValueException(String message) {
		super(message);
	}

	public NullValueException(String message, Throwable cause) {
		super(message, cause);
	}

	public NullValueException(Throwable cause) {
		super(cause);
	}
}
