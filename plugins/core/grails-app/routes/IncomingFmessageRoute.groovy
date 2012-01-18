class IncomingFmessageRoute {
	def configure = {
		from('seda:incoming-fmessages-to-store').
				beanRef('messageStorageService', 'process').
				recipientList(bean('incomingMessageRouterService', 'route'))

		from('seda:incoming-fmessages-to-process').
				beanRef('keywordProcessorService', 'process')
	}
}
