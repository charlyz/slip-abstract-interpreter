package slip.exceptions;

public class InterpreterException extends RuntimeException {
	
	private static final long serialVersionUID = 2266356152402070692L;

	public InterpreterException() {
		super();
	}

	public InterpreterException(String message) {
		super(message);
	}

	public InterpreterException(String message, Throwable cause) {
		super(message, cause);
	}

	public InterpreterException(Throwable cause) {
		super(cause);
	}
}
