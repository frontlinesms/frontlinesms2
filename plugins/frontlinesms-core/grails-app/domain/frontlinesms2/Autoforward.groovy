package frontlinesms2

class Autoforward extends Activity {
//> CONSTANTS
	static def shortName = 'autoforward'

//> SERVICES
	def messageSendService

//> PROPERTIES
	static hasMany = [contacts:Contact, groups:Group, smartGroups:SmartGroup]

//> DOMAIN SETUP
	static constraints = {
		name blank:false, maxSize:255, validator:NAME_VALIDATOR(Autoforward)
		sentMessageText blank:false
	}

//> ACCESSORS
	int getRecipientCount() {
		def numbers = []
		contacts.each { numbers << it.mobile }
		groups.each { it.members.each { numbers << it.mobile }}
		smartGroups.each { it.members.each { numbers << it.mobile }}
		numbers.unique().size()
	}

//> PROCESS METHODS
	def processKeyword(Fmessage message, Keyword matchedKeyword) {
		println "#####Mocked OwnerDetail ## $message.ownerDetail"
		println "#####Mocked id ## $message.id"
		def m = messageSendService.createOutgoingMessage([contacts:contacts, groups:groups?:[] + smartGroups?:[], messageText:sentMessageText])
		println "#####Mocked OutgoingMessage ## $m.id"
		m.ownerDetail = message.id
		this.addToMessages(m)
		this.addToMessages(message)
		m.save(failOnError:true)
		println "############# OwnerDetail of OutgoingMessage ## $m ####### $m.ownerDetail"
		messageSendService.send(m)
		this.save(failOnError:true)
	}
}

