import org.apache.camel.builder.RouteBuilder

class IncomingMissedCallRoute extends RouteBuilder {
	void configure() {
		from('seda:incoming-missedcalls-to-store').
				beanRef('messageStorageService', 'process').
				routeId('missedcall-storage')
	}
}
