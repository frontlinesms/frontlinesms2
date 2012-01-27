class DispatchRoute {
	def configure = {
		onCompletion().onCompleteOnly()
						.beanRef('dispatchRouterService', 'handleCompleted')
		onCompletion().onFailureOnly()
						.beanRef('dispatchRouterService', 'handleFailed')
				
		from('seda:dispatches')
				.beanRef('dispatchStorageService', 'process')
				.dynamicRouter(bean('dispatchRouterService', 'slip'))
	}
}
