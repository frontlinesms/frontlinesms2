package frontlinesms2.camel.exception

class InsufficientCreditException extends RuntimeException {
	public InsufficientCreditException(String message) {
		super(message)
	}
}
