package frontlinesms2

class QuickMessageController extends ControllerUtils {
	private static final CONTACT_ID_PATTERN = /^contact-(\d+)$/

	def create() {
		def groupList = params.groupList? Group.getAll(params.groupList.split(',').flatten().collect{ it as Long }): []
		// TODO we should just use a different name for the parameter which provides us with IDs from messages instead of contacts
		if(params.recipients?.contains(',')) {
			// params.recipients is a list of message IDS?!?!?!?!!!!!??
			// we need to convert the message IDs to phone numbers!!!???!
			def recipientList = []
			params.recipients.tokenize(',').each {
				def msg = Fmessage.findById(it)
				if (msg.inbound) {
					recipientList << msg.src
				} else {
					recipientList += msg.dispatches*.dst
				}
			}
			params.recipients = recipientList.unique()
		} else if(params.recipients ==~ CONTACT_ID_PATTERN) {
			params.recipients = Contact.get((params.recipients =~ CONTACT_ID_PATTERN)[0][1]).mobile
		}
		def recipients = params.recipients? [params.recipients].flatten() : []
		def recipientName = recipients.size() == 1 ? (Contact.findByMobile(recipients[0])?.name?: recipients[0]): ''
		def configureTabs = params.configureTabs? configTabs(params.configureTabs): ['tabs-1', 'tabs-2', 'tabs-3', 'tabs-4']
		[configureTabs:configureTabs,
				addresses:recipients,
				groups:groupList,
				recipientCount:recipients.size(),
				recipientName:recipientName,
				messageText:params.messageText?:'']
	}

	private def configTabs(configTabs) {
		return configTabs.tokenize(",")*.trim()
	}
}

