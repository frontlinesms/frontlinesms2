package frontlinesms2

class Autoreply extends Activity {
//> CONSTANTS
	static String getShortName() { 'autoreply' }

//> PROPERTIES
	static hasOne = [keyword: Keyword]
	String autoreplyText
	
	static constraints = {
		name(blank:false, maxSize:255, validator: { val, obj ->
			def similarName = Autoreply.findByNameIlike(val)
			return !similarName|| obj.id == similarName.id
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

