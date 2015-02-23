package slip.exceptions;

public class DivisionByZeroException  extends RuntimeException {

	private static final long serialVersionUID = -4948345737077182868L;

	public DivisionByZeroException() {
		super();
	}

	public DivisionByZeroException(String message) {
		super(message);
	}

	public DivisionByZeroException(String message, Throwable cause) {
		super(message, cause);
	}

	public DivisionByZeroException(Throwable cause) {
		super(cause);
	}
}
