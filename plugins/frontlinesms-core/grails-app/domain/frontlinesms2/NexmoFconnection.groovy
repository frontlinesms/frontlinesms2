package frontlinesms2

import frontlinesms2.camel.nexmo.*

import org.apache.camel.Exchange
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.model.RouteDefinition
import frontlinesms2.camel.exception.*
import frontlinesms2.api.*

@FrontlineApiAnnotations(apiUrl="nexmo")
class NexmoFconnection extends Fconnection implements FrontlineApi {
	private static final String NEXMO_URL = 'http://rest.nexmo.com/sms/json?'
	static final configFields = [ name:null, api_key:null, api_secret:null, fromNumber:null, receiveEnabled:null, sendEnabled:null ]
	static final defaultValues = [ receiveEnabled:true, sendEnabled:true ]
	static String getShortName() { 'nexmo' }
	
	String api_key
	String api_secret
	String fromNumber
	boolean receiveEnabled = true
	boolean sendEnabled = true

	static passwords = []
	
	static constraints = {
		api_key blank:false
		api_secret blank:false
		fromNumber blank:false
	}

	def nexmoService

	def apiProcess(controller) {
		nexmoService.apiProcess(this, controller)
	}
	
	String getSecret(){ return "" } // No Secret for Nexmo

	boolean isApiEnabled() { this.receivedEnabled }

	String getFullApiUrl() {
		return apiEnabled? "http://[your-ip-address]:${appSettingsService.serverPort}/frontlinesms-core/api/1/$apiUrl/$id/" : ""
	}
	
	List<RouteDefinition> getRouteDefinitions() {
		return new RouteBuilder() {
			@Override void configure() {}
			List getRouteDefinitions() {
				return [from("seda:out-${NexmoFconnection.this.id}")
						.onException(AuthenticationException, InvalidApiIdException, InsufficientCreditException)
									.handled(true)
									.beanRef('fconnectionService', 'handleDisconnection')
									.end()
						.setHeader(Fconnection.HEADER_FCONNECTION_ID, simple(NexmoFconnection.this.id.toString()))
						.process(new NexmoPreProcessor())
						.setHeader(Exchange.HTTP_QUERY,
								simple('api_key=${header.nexmo.api_key}&' +
										'api_secret=${header.nexmo.api_secret}&' + 
										'from=${header.nexmo.fromNumber}&' + 
										'to=${header.nexmo.dst}&' +
										'text=${body}'))
						.to(NEXMO_URL)
						.process(new NexmoPostProcessor())
						.routeId("out-internet-${NexmoFconnection.this.id}")]
			}
		}.routeDefinitions
	}
}
