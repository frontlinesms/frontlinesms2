class SmslibOutgoingRoute {
	def configure = {
		from('seda:smslib-outgoing-fmessages').
				beanRef('smslibOutgoingTranslationService', 'process').
				to('seda:smslib-messages-to-send')
	}
}
