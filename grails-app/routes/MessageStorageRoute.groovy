class MessageStorageRoute {
	def configure = {
		from('seda:fmessages-to-store').
				beanRef('messageStorageService', 'process').
				to('seda:fmessages-to-process')
	}
}
