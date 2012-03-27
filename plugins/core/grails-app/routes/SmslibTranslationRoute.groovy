class SmslibTranslationRoute {
	def configure = {	
		from('seda:raw-smslib')
				.beanRef('smslibTranslationService', 'toFmessage')
				.to('seda:incoming-fmessages-to-store')
				.routeId('smslib-translation')
	}
}
