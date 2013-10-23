package frontlinesms2

class QuickMessageController extends ControllerUtils {
	def create() {
		def groupList = []
		if(params.groupList) {
			groupList = params.groupList.tokenize(',').collect { Group.get(it as Long) }
		}
		if( params.recipients?.contains(',')) {
			def recipientList = []
			params.recipients.tokenize(',').each {
				def msg = Fmessage.findById(it)
				if (msg.inbound)
				{
					recipientList << msg.src
				}
				else
				{
					msg.dispatches.each{ recipientList << it.dst }
				}
			}
			params.recipients = recipientList.unique()
		}
		def recipients = params.recipients ? [params.recipients].flatten() : []
		def recipientName = recipients.size() == 1 ? (Contact.findByMobile(recipients[0])?.name ?: recipients[0]) : ""
		def contacts = Contact.list(sort: "name")
		def configureTabs = params.configureTabs ? configTabs(params.configureTabs): ['tabs-1', 'tabs-2', 'tabs-3', 'tabs-4']
		def nonContactRecipients = []
		recipients.each { if (!Contact.findByMobile(it)) nonContactRecipients << it }
		[contactList: contacts,
				configureTabs: configureTabs,
				groups:groupList,
				recipients:recipients,
				addresses:recipients,
				nonContactRecipients:nonContactRecipients,
				recipientName: recipientName,
				messageText: params.messageText ? params.messageText : [],
				nonExistingRecipients:recipients - contacts*.getMobile() - contacts*.getEmail()]
	}

	private def configTabs(configTabs) {
		return configTabs.tokenize(",")*.trim()
	}
}
