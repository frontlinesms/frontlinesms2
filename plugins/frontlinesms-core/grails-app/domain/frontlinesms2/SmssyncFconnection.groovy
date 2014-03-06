package frontlinesms2

import org.apache.camel.builder.RouteBuilder
import org.apache.camel.model.RouteDefinition
import org.apache.camel.Exchange

import frontlinesms2.api.*

@FrontlineApiAnnotations(apiUrl="smssync")
class SmssyncFconnection extends Fconnection implements FrontlineApi {
	static String getShortName() { 'smssync' }
	static final configFields = ['info-setup': ['secret'], 'info-timeout':['timeout'], 'info-name':['name']]
	static final passwords = []
	static final defaultValues = [timeout:60]

	def smssyncService
	def appSettingsService
	def grailsLinkGenerator
	def urlHelperService
	def dispatchRouterService

	Date lastConnectionTime
	boolean sendEnabled = true
	boolean receiveEnabled = true
	String secret
	int timeout = 360 // 3 hour default

	static constraints = {
		secret nullable:true
		lastConnectionTime nullable:true
	}

	def removeDispatchesFromQueue(dispatches) {
		QueuedDispatch.delete(this, dispatches)
	}

	def apiProcess(controller) {
		smssyncService.apiProcess(this, controller)
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

	String getFullApiUrl(request) {
		def entityClassApiUrl = SmssyncFconnection.getAnnotation(FrontlineApiAnnotations.class)?.apiUrl()
		def path = grailsLinkGenerator.link(controller:'api', params:[entityClassApiUrl:entityClassApiUrl, entityId:id, secret:secret], absolute:false)
		return apiEnabled? "${urlHelperService.getBaseUrl(request)}$path" :''
	}
}

