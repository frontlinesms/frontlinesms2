package frontlinesms2

class AutoforwardController extends ActivityController {
	def autoforwardService

	def save() {
		withAutoforward { autoforward ->
			doSave(autoforwardService, autoforward)
		}
	}

	def create() {
		[
			messageText: '${message_text}',
			activityType: params.controller
		]
	}

	private def withAutoforward = withDomainObject Autoforward, { params.ownerId }
}

