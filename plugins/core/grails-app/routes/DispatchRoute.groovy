class DispatchRoute {
	def configure = {
		from('seda:dispatches')
				.beanRef('dispatchStorageService', 'process')
				.dynamicRouter(bean('dispatchRouterService', 'slip'))
	}
}
