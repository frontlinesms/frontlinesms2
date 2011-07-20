class SmslibOutgoingRoute {
	def configure = {
		from('seda:smslib-outgoing-fmessages').
				beanRef('smslibOutgoingTranslationService', 'process').
				to('seda:smslib-messages-to-send')

		from('seda:smslib-messages-to-send').to('stream:out')
	}
}
