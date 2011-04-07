

class EmailTranslationRoute {
    def configure = {
		from('seda:raw-email')
				.beanRef('emailTranslationService', 'process')
				.to('seda:fmessages-to-store')
	}
}
