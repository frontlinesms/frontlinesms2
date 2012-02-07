class DispatchRoute {
	def configure = {
		onCompletion().onCompleteOnly()
						.beanRef('dispatchRouterService', 'handleCompleted')
		onCompletion().onFailureOnly()
						.beanRef('dispatchRouterService', 'handleFailed')
				
		from('seda:dispatches')
				.dynamicRouter(bean('dispatchRouterService', 'slip'))
	}
}
