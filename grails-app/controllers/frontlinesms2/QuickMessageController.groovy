package frontlinesms2

import grails.converters.JSON

class QuickMessageController {
	def create = {
		if( params['recipients'].contains(',')) {
			def recipientList = []
			params['recipients'].tokenize(',').each { recipientList << Fmessage.findById(it).src }
			params['recipients'] = recipientList
		}
		def recipients = params['recipients'] ? [params['recipients']].flatten() : []
		def fowardMessage = params['messageText'] ? params['messageText'] : []
		def contacts = Contact.list()
		def configureTabs = params['configureTabs'] ? configTabs(params['configureTabs']): ['tabs-1', 'tabs-2', 'tabs-3']
		[contactList: contacts,
			configureTabs: configureTabs,
			groupList:Group.getGroupDetails(),
			recipients:recipients,
			messageText: fowardMessage,
			nonExistingRecipients:recipients - contacts*.getPrimaryMobile() - contacts*.getSecondaryMobile() - contacts*.getEmail()]
	}

	private def configTabs(configTabs) {
		return configTabs.tokenize(",")*.trim()
	}
}
