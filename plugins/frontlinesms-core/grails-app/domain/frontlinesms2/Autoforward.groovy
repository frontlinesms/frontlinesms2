package frontlinesms2

class Autoforward extends Activity {
//> CONSTANTS
	static def shortName = 'autoforward'

//> SERVICES
	def autoforwardService

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
		autoforwardService.doForward(this, message)
		this.addToMessages(message)
		this.save(failOnError:true)
		if(addressesAvailable()){
			def m = messageSendService.createOutgoingMessage([contacts:contacts, groups:groups?:[] + smartGroups?:[], messageText:sentMessageText])
			m.ownerDetail = message.id
			this.addToMessages(m)
			m.save(failOnError:true)
			messageSendService.send(m)
		}
	}

	private def addressesAvailable() {
		println "## All Contacts ## ${((contacts?:[] + groups*.members?:[] + smartGroups*.members?:[]).flatten() - null)}"
		((contacts?:[] + groups*.members?:[] + smartGroups*.members?:[]).flatten() - null).size() > 0
	}
}

