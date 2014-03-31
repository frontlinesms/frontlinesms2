package frontlinesms2

import org.apache.camel.builder.RouteBuilder
import org.apache.camel.model.RouteDefinition
import org.apache.camel.Exchange

import frontlinesms2.api.*

@FrontlineApiAnnotations(apiUrl="frontlinesync")
class FrontlinesyncFconnection extends Fconnection implements FrontlineApi {
	static String getShortName() { 'frontlinesync' }
	static final passwords = []
	static final configFields = ['info-setup': ['secret'], 'info-name':['name'], 'info-sendEnabled':['sendEnabled']]

	def frontlinesyncService
	def appSettingsService
	def grailsLinkGenerator
	def urlHelperService
	def dispatchRouterService

	Date lastConnectionTime
	boolean sendEnabled = true
	boolean receiveEnabled = true
	String secret

	static constraints = {
		lastConnectionTime nullable:true
	}

	def apiProcess(controller) {
		frontlinesyncService.apiProcess(this, controller)
	}

	boolean isApiEnabled() { return this.sendEnabled || this.receiveEnabled }

	def getCustomStatus() {
		// Currently this is just an API receiving incoming stuff, so it's always
		// deemed to be connected.
		return ConnectionStatus.CONNECTED
	}

	List<RouteDefinition> getRouteDefinitions() {
		def routeDefinitions = new RouteBuilder() {
			@Override void configure() {}
			List getRouteDefinitions() {
				def definitions = []
				if(isSendEnabled()) {
					definitions << from("seda:out-${FrontlinesyncFconnection.this.id}")
							.setHeader('fconnection-id', simple(FrontlinesyncFconnection.this.id.toString()))
							.beanRef('frontlinesyncService', 'processSend')
							.routeId("out-internet-${FrontlinesyncFconnection.this.id}")
				}
				return definitions
			}
		}.routeDefinitions
		return routeDefinitions
	}


	String getFullApiUrl(request) {
		return apiEnabled? "${urlHelperService.getBaseUrl(request)}" :''
	}

	def removeDispatchesFromQueue(dispatches) {
		QueuedDispatch.delete(this, dispatches)
	}

	def addToQueuedDispatches(d) {
		QueuedDispatch.create(this, d)
	}

	def getQueuedDispatches() {
		QueuedDispatch.getDispatches(this)
	}

	def updateDispatch(Exchange x) {
		// Dispatch is already in PENDING state so no need to change the status
	}
}

