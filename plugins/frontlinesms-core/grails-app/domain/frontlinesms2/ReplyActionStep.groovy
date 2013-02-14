package frontlinesms2

class ReplyActionStep extends Step {
	def autoreplyService
	static service = 'autoreply'
	static action = 'doReply'
	static String getShortName() { 'reply' }

	static configFields = [autoreplyText: 'textarea']

	static constraints = {
	}

	Map getConfig() {
		[stepId:id, autoreplyText:autoreplyText]
	}

	def getAutoreplyText() {
		getPropertyValue("autoreplyText")
	}

	def getAutoreplyTextSummary() {
		this.autoreplyText.truncate(20);
	}

	def process(Fmessage message) {
		autoreplyService.doReply(this, message)
	}

	def getDescription() {
		i18nUtilService.getMessage(code:"customactivity.${this.shortName}.description", args:[this.autoreplyTextSummary])
	}

}
