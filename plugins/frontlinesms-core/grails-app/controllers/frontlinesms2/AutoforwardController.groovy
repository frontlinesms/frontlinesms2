package frontlinesms2

class AutoforwardController extends ActivityController {
	def autoforwardService

	def save() {
		withAutoforward { autoforward ->
			doSave(autoforwardService, autoforward)
		}
	}

	def create() {
		def groupList = Group.getGroupDetails() + SmartGroup.getGroupDetails()
		[
			messageText: '${message_text}',
			groupList:groupList,
			activityType: params.controller
		]
	}

	private def withAutoforward = withDomainObject Autoforward, { params.ownerId }
}

