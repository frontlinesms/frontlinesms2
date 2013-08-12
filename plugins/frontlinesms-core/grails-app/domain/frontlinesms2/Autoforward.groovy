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
		// FIXME please fix spaces around braces
		groups.each { it.members.each { numbers << it.mobile }}
		smartGroups.each { it.members.each { numbers << it.mobile }}
		numbers.unique().size()
	}

//> PROCESS METHODS
	def processKeyword(Fmessage message, Keyword matchedKeyword) {
		this.addToMessages(message)
		this.save(failOnError:true)
		// FIXME please fix spaces around braces
		if(addressesAvailable()){
			autoforwardService.doForward(this, message)
		}
	}

	// FIXME declare this as `boolean` return type, remove `.size() > 0` check, rename to follow standard naming conventions
	// FIXME can also be simplified by ORing results together
	private def addressesAvailable() {
		println "## All Contacts ## ${((contacts?:[] + groups*.members?:[] + smartGroups*.members?:[]).flatten() - null)}"
		((contacts?:[] + groups*.members?:[] + smartGroups*.members?:[]).flatten() - null).size() > 0
	}
}

