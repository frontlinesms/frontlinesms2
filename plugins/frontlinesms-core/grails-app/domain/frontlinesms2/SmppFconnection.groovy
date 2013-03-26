package frontlinesms2

import frontlinesms2.camel.smpp.*

import org.apache.camel.builder.RouteBuilder
import org.apache.camel.model.RouteDefinition
import frontlinesms2.camel.exception.*

class SmppFconnection extends Fconnection {
	static final configFields = [name:null, url:null, port:null, username:null, password:null, fromNumber:null, send:null , receive:null]
	static defaultValues = [send:true, receive:true]
	static String getShortName() { 'smpp' }
	
	String url
	String port
	String username
	String password
	String fromNumber
	Boolean send = true
	Boolean receive = true
	
	static constraints = {
		url blank:false
		port blank:false
		username blank:false
		password blank:false
		fromNumber blank:true
	}

	static passwords = ['password']

	static mapping = {
		password column: 'smpp_password'
	}
	
	List<RouteDefinition> getRouteDefinitions() {
		return new RouteBuilder() {
			@Override void configure() {}
			List getRouteDefinitions() {
				def definitions = []
				
				if(SmppFconnection.this.send){
					definitions << from("seda:out-${SmppFconnection.this.id}")
								.setHeader("CamelSmppSourceAddr", simple(SmppFconnection.this.fromNumber))
								.onException(RuntimeException)
											.handled(true)
											.beanRef('fconnectionService', 'handleDisconnection')
											.end()
								.setHeader(Fconnection.HEADER_FCONNECTION_ID, simple(SmppFconnection.this.id.toString()))
								.process(new SmppPreProcessor())
								.to(SmppFconnection.this.sendingUrl)
								.process(new SmppPostProcessor())
								.routeId("out-internet-${SmppFconnection.this.id}")
				}

				if(SmppFconnection.this.receive){
					definitions << from(SmppFconnection.this.receivingUrl)
							.setHeader(Fconnection.HEADER_FCONNECTION_ID, simple(SmppFconnection.this.id.toString()))
							.beanRef('smppTranslationService', 'process')
							.to('seda:incoming-fmessages-to-store')
							.routeId("in-${SmppFconnection.this.id}")
				}
				return definitions
			}
		}.routeDefinitions
	}

	private getSendingUrl(){
		return "smpp://${this.username}@${this.url}:${this.port}?password=${this.password}&enquireLinkTimer=30000&transactionTimer=50000&systemType=producer"
	}

	private getReceivingUrl(){
		return "smpp://${this.username}@${this.url}:${this.port}?password=${this.password}&enquireLinkTimer=30000&transactionTimer=50000&systemType=consumer"
	}
}
