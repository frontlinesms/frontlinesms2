class SmslibTranslationRoute {
	def configure = {	
		from('seda:raw-smslib')
				.beanRef('smslibTranslationService', 'process')
				.to('seda:fmessages-to-store')
				.id('smslib-translation')
	}
}
