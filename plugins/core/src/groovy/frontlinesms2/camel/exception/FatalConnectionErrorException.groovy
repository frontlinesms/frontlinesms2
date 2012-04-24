package frontlinesms2.camel.exception


class FatalConnectionErrorException extends RuntimeException {
	public FatalConnectionErrorException(String message) {
		super(message)
	}
}
