class EmailTranslationRoute {
	def configure = {
		from('seda:raw-email')
				.beanRef('emailTranslationService', 'process')
				.to('seda:incoming-fmessages-to-store')
				.routeId('email-translation')
	}
}
