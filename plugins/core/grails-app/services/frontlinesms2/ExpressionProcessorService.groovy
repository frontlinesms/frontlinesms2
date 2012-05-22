package frontlinesms2
class ExpressionProcessorService {
	static transactional = true	
	static regex = /[$][{]*[a-z_]*[}]/
	def fields = [
	    'contact_name' : ['quickCompose', 'reply', 'reply_all'],
	    'contact_number' : ['quickCompose', 'poll', 'auto_forward'],
	    'message_content' : ['poll', 'announcement'],
	    'keyword' : ['poll', 'announcement']]

	def findByView(viewName) {
	    def ret = [:]
	    fields.each {
	        def active = false
	        it.value.each { i ->
	            if (i == viewName)
	            {
	                active = true
	            }
	        }
	        ret.put(it.key, active)
	    }
	    ret
	}

	String replaceExpressions(Dispatch dispatch) {
	    def messageBody = dispatch.message.text
	    matches = messageBody.findAll(regex)
	    matches.each {
	        messageBody = messageBody.replaceFirst(regex, getReplacement(it, dispatch))
	    }
	    messageBody
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

	private getReplacement(expression, dispatch) {
		if (expression == "\${contact_name}")
			return Contact.findByMobileLike(dispatch.dst)? Contact.findByMobileLike(dispatch.dst).name : dispatch.dst
		if (expression == "\${contact_number}")
			return dispatch.dst
		return ""
	}

}