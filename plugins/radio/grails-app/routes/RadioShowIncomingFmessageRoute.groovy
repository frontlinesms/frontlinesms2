 class RadioShowIncomingFmessageRoute {
	def configure = {
		from('seda:radioshow-fmessages-to-process').
				beanRef('radioShowService', 'process')
				routeId('radio-message-processing')
	}
}
