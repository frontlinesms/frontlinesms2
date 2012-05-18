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

	/** Send a message or messages in 30 seconds time */
	static defer(Fmessage message) {
		defer([message])
	}

	/** Send a message or messages in 30 seconds time */
	static defer(messages) {
		def sendTime = new Date()
		use(groovy.time.TimeCategory) {
			sendTime += 30000
		}
		MessageSendJob.schedule(sendTime, [ids:messages*.id])
	}
}

