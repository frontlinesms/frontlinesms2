package frontlinesms2

class MessageSendJob {
	def messageSendService

	def execute(context) {
		def ids = context.mergedJobDataMap.get('ids')
		def messages = Fmessage.getAll(ids)
		messages.each { m ->
			messageSendService.send(m)
		}
	}
}

