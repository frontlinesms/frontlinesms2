package frontlinesms2

class Autoforward extends Activity {
//> CONSTANTS
	static def shortName = 'autoforward'
	private static def RECIPIENT_VALIDATOR = { val, obj ->
		println "RECIPIENT_VALIDATOR:: obj=$obj val=$val"
		def valid = val || obj.contacts || obj.groups || obj.smartGroups
		println "Valid: $valid"
		return valid
	}

//> SERVICES
	def messageSendService

//> PROPERTIES
	static hasMany = [contacts:Contact, groups:Group, smartGroups:SmartGroup]

//> DOMAIN SETUP
	static constraints = {
		name blank:false, maxSize:255, validator:NAME_VALIDATOR(Autoforward)
		contacts validator:RECIPIENT_VALIDATOR
		groups validator:RECIPIENT_VALIDATOR
		smartGroups validator:RECIPIENT_VALIDATOR
		sentMessageText blank:false
	}

//> ACCESSORS
	int getRecipientCount() {
		(contacts? contacts.size(): 0) +
				(groups? (groups.collect { it.members?.size()?:0 }?.sum()): 0) +
				(smartGroups? (smartGroups.collect { it.members?.size()?:0 }?.sum()): 0)
	}

//> PROCESS METHODS
	def processKeyword(Fmessage message, Keyword matchedKeyword) {
		def m = messageSendService.createOutgoingMessage([contacts:contacts, groups:groups+smartGroups, messageText:sentMessageText])
		messageSendService.send(m)
	}
}

