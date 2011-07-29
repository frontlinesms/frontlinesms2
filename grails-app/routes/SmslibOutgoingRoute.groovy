class SmslibOutgoingRoute {
	def configure = {
		from('seda:smslib-outgoing-fmessages')
				.beanRef('messageStorageService', 'process')
				.dynamicRouter(bean('fmessageRouterService', 'slip'))
	}
}
