package frontlinesms2

import frontlinesms2.camel.intellisms.*

import org.apache.camel.Exchange
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.model.RouteDefinition
import frontlinesms2.camel.exception.FatalConnectionException

class IntelliSmsFconnection extends Fconnection {
	private static final String INTELLISMS_URL = 'http://www.intellisoftware.co.uk/smsgateway'
	static configFields = [name:null,
				send: ['username', 'password'], 
				receive: ['receiveProtocol', 'serverName', 'serverPort', 'emailUserName', 'emailPassword']]
	static passwords = ['password', 'emailPassword']
	static String getShortName() { 'intellisms' }
	
	String username
	String password // FIXME maybe encode this rather than storing plaintext
	boolean send
	boolean receive
	
	//Http forwarding configuration
	EmailReceiveProtocol receiveProtocol
	String serverName
	Integer serverPort
	String emailUserName
	String emailPassword

	static constraints = {
		send(validator: { val, obj ->
			if(val) {
				return obj.username && obj.password
			}
			else return obj.receive
		})
		receive(validator: { val, obj ->
				 if(val) return obj.receiveProtocol && obj.serverName && obj.emailUserName && obj.emailUserName
				 else return obj.send
		})
		username(nullable:true, blank:false)
		password(nullable:true, blank:false)
		receiveProtocol(nullable:true, blank:false)
		serverName(nullable:true, blank:false)
		serverPort(nullable:true, blank:false)
		emailUserName(nullable:true, blank:false)
		emailPassword(nullable:true, blank:false)
	}
	
	private def camelProducerAddress = {
		String serverPortParam = serverPort ? ":${serverPort}" : ""
		"${receiveProtocol.name().toLowerCase()}://${serverName}${serverPortParam}?debugMode=true&consumer.delay=15000&username=${emailUserName}&password=${emailPassword}"
		
	}
	
	List<RouteDefinition> getRouteDefinitions() {
		
		return new RouteBuilder() {
			@Override void configure() {}
			List getRouteDefinitions() {
				if(getSend() && getReceive()) {
					return [from("seda:out-${IntelliSmsFconnection.this.id}")
							.onException(FatalConnectionException)
									.handled(true)
									.beanRef('fconnectionService', 'handleDisconnection')
									.end()
							.process(new IntelliSmsPreProcessor())
							.setHeader(Exchange.HTTP_URI,
									simple(INTELLISMS_URL + '/sendmsg.aspx?' + 
											'username=${header.intellisms.username}&' + 
											'password=${header.intellisms.password}&' + 
											'to=${header.intellisms.dst}&' +
											'text=${body}'))
							.to(INTELLISMS_URL)
							.process(new IntelliSmsPostProcessor())
							.routeId("out-internet-${IntelliSmsFconnection.this.id}"),
						from(camelProducerAddress())
							.beanRef('intelliSmsTranslationService', 'process')
							.to('seda:incoming-fmessages-to-store')
							.routeId("in-${IntelliSmsFconnection.this.id}")]
				}
				else if(getReceive()) {
					return [from(camelProducerAddress())
							.beanRef('intelliSmsTranslationService', 'process')
							.to('seda:incoming-fmessages-to-store')
							.routeId("in-${IntelliSmsFconnection.this.id}")]
				}
				else {
					return [from("seda:out-${IntelliSmsFconnection.this.id}")
							.onException(FatalConnectionException)
									.handled(true)
									.beanRef('fconnectionService', 'handleDisconnection')
									.end()
							.process(new IntelliSmsPreProcessor())
							.setHeader(Exchange.HTTP_URI,
									simple(INTELLISMS_URL + '/sendmsg.aspx?' + 
											'username=${header.intellisms.username}&' + 
											'password=${header.intellisms.password}&' + 
											'to=${header.intellisms.dst}&' +
											'text=${body}'))
							.to(INTELLISMS_URL)
							.process(new IntelliSmsPostProcessor())
							.routeId("out-internet-${IntelliSmsFconnection.this.id}")]
				}
			}
		}.routeDefinitions
	}
}
