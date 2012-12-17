package frontlinesms2

import grails.converters.JSON
class AutoforwardController extends ActivityController {
	def autoforwardService

	def save() {
		withAutoforward { autoforward ->
			doSave('autoreply', autoforwardService, autoforward)
		}
	}

	def create() {
		def groupList = Group.getGroupDetails() + SmartGroup.getGroupDetails()
		[contactList: Contact.list(), messageText: '${message_text}',
				groupList:groupList, activityType: params.controller]
	}

	private def withAutoforward = withDomainObject Autoforward, { params.ownerId }
}

