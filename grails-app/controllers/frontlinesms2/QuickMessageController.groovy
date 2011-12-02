package frontlinesms2

import grails.converters.JSON

class QuickMessageController {
	def create = {
		if( params.recipients?.contains(',')) {
			def recipientList = []
			params.recipients.tokenize(',').each { recipientList << Fmessage.findById(it).src }
			params.recipients = recipientList.unique()
		}
		def recipients = params.recipients ? [params.recipients].flatten() : []
		def recipientName = recipients.size() == 1 ? (Contact.findByPrimaryMobile(recipients[0])?.name ?: Contact.findBySecondaryMobile(recipients[0])?.name ?: recipients[0]) : ""
		def fowardMessage = params.messageText ? params.messageText : []
		def contacts = Contact.list(sort: "name")
		def configureTabs = params.configureTabs ? configTabs(params.configureTabs): ['tabs-1', 'tabs-2', 'tabs-3', 'tabs-4']
		def groupList = Group.getGroupDetails() + SmartGroup.getGroupDetails()
		[contactList: contacts,
			configureTabs: configureTabs,
			groupList:groupList,
			recipients:recipients,
			recipientName: recipientName,
			messageText: fowardMessage,
			nonExistingRecipients:recipients - contacts*.getPrimaryMobile() - contacts*.getSecondaryMobile() - contacts*.getEmail()]
	}

	private def configTabs(configTabs) {
		return configTabs.tokenize(",")*.trim()
	}
}
