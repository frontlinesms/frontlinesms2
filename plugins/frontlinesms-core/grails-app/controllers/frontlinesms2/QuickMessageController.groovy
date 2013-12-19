package frontlinesms2

class QuickMessageController extends ControllerUtils {
	private static final CONTACT_ID_PATTERN = /^contact-(\d+)$/

	def create() {
		def groupList = params.groupList? Group.getAll(params.groupList.split(',').flatten().collect{ it as Long }): []
		def recipientList = []
		if(params.messageIds?.contains(',')) {
			params.messageIds.tokenize(',').each {
				def msg = TextMessage.findById(it)
				if (msg.inbound) {
					recipientList << msg.src
				} else {
					recipientList += msg.dispatches*.dst
				}
			}
			recipientList = recipientList.unique()
		} else if(params.contactId) {
			recipientList << Contact.get(params.contactId).mobile
		}
		recipientList = recipientList.flatten()
		def recipientName = recipientList.size() == 1 ? (Contact.findByMobile(recipientList[0])?.name?: recipientList[0]): ''
		def configureTabs = params.configureTabs? configTabs(params.configureTabs): ['tabs-1', 'tabs-2', 'tabs-3', 'tabs-4']
		[configureTabs:configureTabs,
				addresses:recipientList,
				groups:groupList,
				recipientCount:recipientList.size(),
				recipientName:recipientName,
				messageText:params.messageText?:'']
	}

	private def configTabs(configTabs) {
		return configTabs.tokenize(",")*.trim()
	}
}

