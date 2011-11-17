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
		def contacts = Contact.list()
		def configureTabs = params.configureTabs ? configTabs(params.configureTabs): ['tabs-1', 'tabs-2', 'tabs-3', 'tabs-4']
		[contactList: contacts,
			configureTabs: configureTabs,
			groupList:Group.getGroupDetails(),
			recipients:recipients,
			recipientName: recipientName,
			messageText: fowardMessage,
			nonExistingRecipients:recipients - contacts*.getPrimaryMobile() - contacts*.getSecondaryMobile() - contacts*.getEmail()]
	}
	
	def send = {
		def failedMessageIds = params.failedMessageIds
		def messages = failedMessageIds ? Fmessage.getAll([failedMessageIds].flatten()): messageSendService.getMessagesToSend(params)
		messages.each { message ->
			messageSendService.send(message)
		}
		flash.message = "Message has been queued to send to " + messages*.dst.join(", ")
		if(params.hasSummary)
			[]
		else
			redirect (controller: "message", action: 'pending')
	}

	private def configTabs(configTabs) {
		return configTabs.tokenize(",")*.trim()
	}
}
