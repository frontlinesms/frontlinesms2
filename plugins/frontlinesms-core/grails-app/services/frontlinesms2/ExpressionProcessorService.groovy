package frontlinesms2
class ExpressionProcessorService {
	def i18nUtilService
	static transactional = true	
	static regex = /[$][{]*[a-z_]*[}]/
	// fields map holds the available expressions for replacement
	// key is the expression name, and value is the controllers it is applicable to
	def fields = [
		'recipient_number' : ['quickMessage', 'announcement', 'poll', 'autoreply', 'subscription', 'autoforward'],
		'recipient_name' : ['quickMessage', 'announcement', 'poll', 'autoreply', 'subscription', 'autoforward'],
		'sender_number' : ['autoforward'],
		'sender_name' : ['autoforward'],
		'keyword' : ['autoforward'],
		'message_text' : ['poll', 'autoreply', 'subscription','autoforward'],
		'message_text_with_keyword' : ['quickMessage','poll', 'autoreply', 'subscription','autoforward']]

	def findByController(controllerName) {
		fields.findAll { controllerName in it.value }.keySet()
	}

	String process(Dispatch dispatch) {
		def messageBody = dispatch.message.text
		def matches = getExpressions(messageBody)
		matches.unique().each { match ->
			messageBody = messageBody.replace(match, getReplacement(match, dispatch))
		}
		messageBody
	}

	def getExpressions(messageText) {
		return messageText.findAll(regex)
	}

	String getUnsubstitutedDisplayText(messageText) {
		def matches = getExpressions(messageText)
		matches.each {
			messageText = messageText.replaceFirst(regex, '<em class="dynamic-field">'+i18nUtilService.getMessage(code:"dynamicfield."+"${(it - '\${' - '}')}"+".label") + '</em>')
		}
		messageText
	}

	private getReplacement(expression, dispatch) {
		try {
			def incomingMessage = Fmessage.get(dispatch.message.ownerDetail)
			if (expression == "\${message_text}"){
				def keyword = incomingMessage.messageOwner?.keywords?.find{ incomingMessage.text.toUpperCase().startsWith(it.value) }?.value
				def text = incomingMessage.text
				if (keyword?.size() && text.toUpperCase().startsWith(keyword.toUpperCase())) {
					text = text.substring(keyword.size()).trim()
				}
				return text
			}
			if (expression == "\${message_text_with_keyword}")
				return incomingMessage.text
			if (expression == "\${sender_number}")
				return incomingMessage.src
			if (expression == "\${sender_name}")
				return Contact.findByMobileLike(incomingMessage.src)? Contact.findByMobileLike(incomingMessage.src).name : incomingMessage.src
			if (expression == "\${recipient_number}")
				return dispatch.dst
			if (expression == "\${recipient_name}")
				return Contact.findByMobileLike(dispatch.dst)? Contact.findByMobileLike(dispatch.dst).name : dispatch.dst
			if (expression == "\${keyword}"){
				def keyword = incomingMessage.messageOwner?.keywords?.find{ incomingMessage.text.toUpperCase().startsWith(it.value) }?.value
				return keyword
			}
			return expression.replace('$', '\$')
		}
		catch (Exception e) {
			return expression.replace('$', '\$')
		}
	}

}
