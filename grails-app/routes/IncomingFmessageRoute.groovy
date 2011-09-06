class IncomingFmessageRoute {
	def configure = {
		from('seda:incoming-fmessages-to-store').
				beanRef('messageStorageService', 'process').
				to('seda:incoming-fmessages-to-process')
				
		from('seda:incoming-fmessages-to-process').
				beanRef('keywordProcessorService', 'process')
	}
}
