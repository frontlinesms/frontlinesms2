package frontlinesms2

class MessageSendJob {
	def grailsApplication

    def execute(context) {
       grailsApplication.mainContext.messageSendService.send(context.mergedJobDataMap.get('ids'))
    }
}
