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
	def autoreplyService

//> PROCESS METHODS
	def processKeyword(Fmessage message, Keyword matchedKeyword) {
		addToMessages(message)
		autoreplyService.doReply(this, message)
		save()
	}
}

