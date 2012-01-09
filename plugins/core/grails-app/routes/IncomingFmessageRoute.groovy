class IncomingFmessageRoute {
	def configure = {
		from('seda:incoming-fmessages-to-store').
				beanRef('messageStorageService', 'process').
				dynamicRouter(bean('incomingMessageRouterService', 'slip'))

		from('seda:incoming-fmessages-to-process').
				beanRef('keywordProcessorService', 'process')
	}
}
