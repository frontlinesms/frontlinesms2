import org.apache.camel.builder.RouteBuilder

class SmslibTranslationRoute extends RouteBuilder {
	void configure() {
		from('seda:raw-smslib')
				.beanRef('smslibTranslationService', 'toFmessage')
				.to('seda:incoming-fmessages-to-store')
				.routeId('smslib-translation')
	}
}
