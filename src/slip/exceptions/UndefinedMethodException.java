package slip.exceptions;

public class UndefinedMethodException  extends RuntimeException {

	private static final long serialVersionUID = 3059918176695797067L;

	public UndefinedMethodException() {
		super();
	}

	public UndefinedMethodException(String message) {
		super(message);
	}

	public UndefinedMethodException(String message, Throwable cause) {
		super(message, cause);
	}

	public UndefinedMethodException(Throwable cause) {
		super(cause);
	}
}
