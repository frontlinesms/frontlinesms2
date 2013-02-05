import org.apache.camel.builder.RouteBuilder

import frontlinesms2.camel.exception.NoRouteAvailableException

class DispatchRoute extends RouteBuilder {
	void configure() {
		onCompletion().onCompleteOnly()
				.beanRef('dispatchRouterService', 'handleCompleted')
		onCompletion().onFailureOnly()
				.beanRef('dispatchRouterService', 'handleFailed')
				
		from('seda:dispatches')
				.onException(NoRouteAvailableException)
						.beanRef('dispatchRouterService', 'handleNoRoutes')
						.handled(false)
						.end()
				.dynamicRouter(bean('dispatchRouterService', 'slip'))
				.routeId('dispatch-route')
	}
}

