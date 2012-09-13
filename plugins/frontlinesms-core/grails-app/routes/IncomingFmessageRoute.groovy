import org.apache.camel.builder.RouteBuilder

class IncomingFmessageRoute extends RouteBuilder {
	void configure() {
		from('seda:incoming-fmessages-to-store').
				beanRef('messageStorageService', 'process').
				recipientList(bean('incomingMessageRouterService', 'route')).
				routeId('message-storage')

		from('seda:incoming-fmessages-to-process').
				beanRef('keywordProcessorService', 'process').
				routeId('message-processing')
	}
}
