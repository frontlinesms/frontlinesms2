package frontlinesms2

class MagicWandService {
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

	private getReplacement(expression, dispatch) {
		if (expression == "\${contact_name}")
			return Contact.findByMobileLike(dispatch.dst)?: dispatch.dst
		if (expression == "\${contact_number}")
			return dispatch.dst
		return ""
	}

	String replaceExpressions(Dispatch dispatch) {
	    def messageBody = dispatch.message.text
	    matches = messageBody.findAll(regex)
	    matches.each {
	        messageBody = messageBody.replaceFirst(regex, getReplacement(it, dispatch))
	    }
	    messageBody
	}

}