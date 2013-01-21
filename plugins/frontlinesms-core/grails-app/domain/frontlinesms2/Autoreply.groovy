package frontlinesms2

class Autoreply extends Activity {
//> CONSTANTS
	static String getShortName() { 'autoreply' }

//> PROPERTIES
	String autoreplyText
	
	static constraints = {
		name(blank:false, maxSize:255, validator:NAME_VALIDATOR(Autoreply))
		autoreplyText(blank:false)
	}
	
	static mapping = {
		keyword cascade: 'all'
		version false
	}

//> SERVICES
	def messageSendService

//> PROCESS METHODS
	def processKeyword(Fmessage message, Keyword matchedKeyword) {
		def params = [:]
		params.addresses = message.src
		params.messageText = autoreplyText
		addToMessages(message)
		def outgoingMessage = messageSendService.createOutgoingMessage(params)
		outgoingMessage.ownerDetail = message.id
		addToMessages(outgoingMessage)
		messageSendService.send(outgoingMessage)
		save()
		println "Autoreply message sent to ${message.src}"
	}
}

