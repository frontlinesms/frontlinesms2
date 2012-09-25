package frontlinesms2

import org.apache.camel.builder.RouteBuilder
import org.apache.camel.model.RouteDefinition

import frontlinesms2.api.*

class SmssyncFconnection extends Fconnection implements FrontlineApi {
	static final String apiUrl = 'smssync'
	static String getShortName() { 'smssync' }
	static final configFields = ['receiveEnabled', 'sendEnabled', 'secret']
	static final passwords = ['secret']
	static final defaultValues = []

	def smssyncService

	boolean sendEnabled = true
	boolean receiveEnabled = true
	String secret
	String outgoingQueue // stored as String as either we are adding to it or destroying the whole thing

	static constraints = {
		outgoingQueue nullable:true
		secret nullable:true
	}

	def apiProcess(controller) {
		smssyncService.apiProcess(this, controller)
	}

	def addToQueue(Dispatch d) {
		if(outgoingQueue) {
			outgoingQueue += ',' + d.id
		} else outgoingQueue = d.id.toString()
	}

	Long[] getOutgoingQueueIds() {
		if(outgoingQueue) {
			return outgoingQueue?.split(',')*.toLong()
		} else return []
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

