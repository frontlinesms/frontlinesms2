package frontlinesms2
class ExpressionProcessorService {
	def i18nUtilService
	static transactional = true	
	static regex = /[$][{]*[a-z_]*[}]/
	// fields map holds the available expressions for replacement
	// key is the expression name, and value is the controllers it is applicable to
	def fields = [
		'contact_name' : ['quickMessage', 'announcement', 'poll', 'autoreply', 'subscription'],
		'contact_number' : ['quickMessage', 'announcement', 'poll', 'autoreply', 'subscription']]

	def findByController(controllerName) {
		fields.findAll { controllerName in it.value }.keySet()
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

	String getUnsubstitutedDisplayText(messageText) {
		def matches = getExpressions(messageText)
		matches.each {
			messageText = messageText.replaceFirst(regex, '<em class="dynamic-field">'+i18nUtilService.getMessage(code:"dynamicfield."+"${(it - '\${' - '}')}"+".label") + '</em>')
		}
		messageText
	}

	private getReplacement(expression, dispatch) {
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
		return ""
	}

}
