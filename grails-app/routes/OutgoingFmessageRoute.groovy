class OutgoingFmessageRoute {
	def configure = {
		from('seda:outgoing-fmessages')
				.beanRef('messageStorageService', 'process')
				.dynamicRouter(bean('fmessageRouterService', 'slip'))
	}
}
