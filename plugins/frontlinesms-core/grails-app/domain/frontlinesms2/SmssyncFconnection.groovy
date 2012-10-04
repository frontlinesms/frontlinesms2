package frontlinesms2

import org.apache.camel.builder.RouteBuilder
import org.apache.camel.model.RouteDefinition

import frontlinesms2.api.*

class SmssyncFconnection extends Fconnection implements FrontlineApi {
	static final String apiUrl = 'smssync'
	static String getShortName() { 'smssync' }
	static final configFields = ['name', 'receiveEnabled', 'sendEnabled', 'secret']
	static final passwords = ['secret']
	static final defaultValues = [sendEnabled:true, receiveEnabled:true]

	def smssyncService

	boolean sendEnabled = true
	boolean receiveEnabled = true
	String secret
	static hasMany = [queuedDispatchIds: Long]
	List queuedDispatchIds

	static constraints = {
		queuedDispatchIds nullable:true
		secret nullable:true
	}

	def apiProcess(controller) {
		smssyncService.apiProcess(this, controller)
	}

	def addToQueuedDispatches(d) {
		addToQueuedDispatchIds(d.id)
	}

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
}

