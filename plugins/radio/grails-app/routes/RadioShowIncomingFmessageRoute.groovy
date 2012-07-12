import org.apache.camel.builder.RouteBuilder

class RadioShowIncomingFmessageRoute extends RouteBuilder {
	void configure() {
		from('seda:radioshow-fmessages-to-process').
				beanRef('radioShowService', 'process').
				routeId('radio-message-processing')
	}
}
