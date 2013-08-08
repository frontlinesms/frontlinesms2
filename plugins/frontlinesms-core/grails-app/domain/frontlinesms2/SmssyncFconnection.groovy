package frontlinesms2

import org.apache.camel.builder.RouteBuilder
import org.apache.camel.model.RouteDefinition
import org.apache.camel.Exchange

import frontlinesms2.api.*

@FrontlineApiAnnotations(apiUrl="smssync")
class SmssyncFconnection extends Fconnection implements FrontlineApi {
	static String getShortName() { 'smssync' }
	static final configFields = ['info-setup': ['secret'], 'info-timeout':['timeout'], 'info-name':['name']]
	static final passwords = ['secret']
	static final defaultValues = [timeout:60]

	def smssyncService
	def appSettingsService
	def dispatchRouterService

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

	def updateDispatch(Exchange x) {
		// Dispatch is already in PENDING state so no need to change the status
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
		// Secret is included here because it's required for SMSSync's 'send' task.
		// For incoming messages, we are already provided the secret in the GET params,
		// so a secret mismatch might cause confusion.  In future, SMSSync should
		// secret in task requests as well, so eventually $secret can be dropped from
		// this URL.
		return apiEnabled? "api/1/${shortName}/$id/${secret?:''}" : ''
	}
}

