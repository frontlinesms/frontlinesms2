package frontlinesms2

import org.apache.camel.builder.RouteBuilder
import org.apache.camel.model.RouteDefinition

import frontlinesms2.api.*

@FrontlineApiAnnotations(apiUrl="smssync")
class SmssyncFconnection extends Fconnection implements FrontlineApi {
	static String getShortName() { 'smssync' }
	static final configFields = ['info-setup': ['secret'], 'info-send_receive':['receiveEnabled', 'sendEnabled'], 'info-timeout':['timeout'], 'info-name':['name']]
	static final passwords = ['secret']
	static final defaultValues = [sendEnabled:true, receiveEnabled:true, timeout:60]

	def smssyncService
	def appSettingsService

	boolean sendEnabled = true
	boolean receiveEnabled = true
	String secret
	int timeout = 360 // 3 hour default

	static constraints = {
		secret nullable:true
	}

	def removeDispatchesFromQueue(dispatches) {
		SmssyncFconnectionQueuedDispatch.delete(this, dispatches)
	}

	def apiProcess(controller) {
		smssyncService.apiProcess(this, controller)
	}

	def addToQueuedDispatches(d) {
		SmssyncFconnectionQueuedDispatch.create(this, d)
	}

	def getQueuedDispatches() {
		SmssyncFconnectionQueuedDispatch.getDispatches(this)
	}

	boolean isApiEnabled() { return this.sendEnabled || this.receiveEnabled }

	List<RouteDefinition> getRouteDefinitions() {
		def routeDefinitions = new RouteBuilder() {
			@Override void configure() {}
			List getRouteDefinitions() {
				def definitions = []
				if(isSendEnabled()) {
					definitions << from("seda:out-${SmssyncFconnection.this.id}")
							.setHeader('fconnection-id', simple(SmssyncFconnection.this.id.toString()))
							.beanRef('smssyncService', 'processSend')
							.routeId("out-internet-${SmssyncFconnection.this.id}")
				}
				return definitions
			}
		}.routeDefinitions
		return routeDefinitions
	}

	String getFullApiUrl() {
		return apiEnabled? "http://[your-ip-address]:${appSettingsService.serverPort}/frontlinesms-core/api/1/$apiUrl/$id/" : ""
	}
}

