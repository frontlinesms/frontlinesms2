

class SmslibOutgoingRoute {
    def configure = {
        from('seda:smslib-messages-to-send').
			beanRef('smslibOutgoingTranslationService', 'process').
			to('seda:smslib-send')
    }
}
