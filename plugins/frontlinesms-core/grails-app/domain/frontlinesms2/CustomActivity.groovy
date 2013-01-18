package frontlinesms2

class CustomActivity extends Activity {
	List steps
	def customActivityService
	static String getShortName() { 'customactivity' }
	static hasMany = [steps: Step]

	def processKeyword(Fmessage message, Keyword matchedKeyword) {
		addToMessages(message)
		customActivityService.triggerSteps(this, message)
		save()
	}
}
