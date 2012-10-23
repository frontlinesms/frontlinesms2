package frontlinesms2

class Autoreply extends Activity {
//> CONSTANTS
	static String getShortName() { 'autoreply' }

//> PROPERTIES
	String autoreplyText
	
	static constraints = {
		name(blank:false, maxSize:255, validator: { val, obj ->
			if(obj?.deleted || obj?.archived) return true
			def identical = Autoreply.findAllByNameIlike(val)
			if(!identical) return true
			else if (identical.any { it.id != obj.id && !it?.archived && !it?.deleted }) return false
			else return true
			})
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
		addToMessages(outgoingMessage)
		messageSendService.send(outgoingMessage)
		save()
		println "Autoreply message sent to ${message.src}"
	}
}

