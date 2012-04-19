package frontlinesms2

class Autoreply extends Activity {
	def messageSendService
	static hasOne = [keyword: Keyword]
	String autoreplyText
	
	static constraints = {
		name(blank:false, maxSize:255, unique:true)
		autoreplyText(blank:false)
	}
	
	static mapping = {
		keyword cascade: 'all'
	}
	
	def getType() {
		return 'autoreply'
	}

	def processKeyword(Fmessage message, boolean exactMatch) {
		if(!exactMatch && keyword.value) return
		def params = [:]
		params.addresses = message.src
		params.messageText = autoreplyText
		addToMessages(message)
		def outgoingMessage = messageSendService.createOutgoingMessage(params)
		addToMessages(outgoingMessage)
		messageSendService.send(outgoingMessage)
		save()
		println "Autoreply message sent to ${message.src}"
	}
}

