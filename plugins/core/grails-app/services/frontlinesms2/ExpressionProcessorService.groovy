package frontlinesms2
class ExpressionProcessorService {
	static regex = /[$]+[{]+[a-z_]*[}]+/

	private getReplacement(expression, dispatch) {
		if (expression == "\${contact_name}")
			return Contact.findByMobileLike(dispatch.dst)?: dispatch.dst
		if (expression == "\${contact_number}")
			return dispatch.dst
		return ""
	}

	String process(Dispatch dispatch) {
	    def messageBody = dispatch.message.text
	    def matches = getExpressions(messageBody)
	    matches.each {
	        messageBody = messageBody.replaceFirst(regex, getReplacement(it, dispatch))
	    }
	    messageBody
	}

	def getExpressions(messageText) {
		return messageText.findAll(regex)
	}
}