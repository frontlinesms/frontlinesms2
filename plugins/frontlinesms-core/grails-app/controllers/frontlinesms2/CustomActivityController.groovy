package frontlinesms2

class CustomActivityController {
	def customActivityService 

	def save() {
		withCustomActivity { customActivity ->
			//TODO implement create and edit of CustomActivity
		}
	}

	private def withCustomActivity = withDomainObject CustomActivity, { params.ownerId }
}