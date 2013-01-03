package frontlinesms2

import frontlinesms2.camel.smpp.*

import org.apache.camel.Exchange
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.model.RouteDefinition
import frontlinesms2.camel.exception.*

class SmppFconnection extends Fconnection {
	static final configFields = [name:null, url:null, port:null, username:null, password:null, fromNumber:null]
	static final defaultValues = []
	static String getShortName() { 'smpp' }
	
	String url
	String port
	String username
	String password
	String fromNumber
	
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
				return [from("seda:out-${SmppFconnection.this.id}")
						.onException(RuntimeException)
									.handled(true)
									.beanRef('fconnectionService', 'handleDisconnection')
									.end()
						.setHeader(Fconnection.HEADER_FCONNECTION_ID, simple(SmppFconnection.this.id.toString()))
						.process(new SmppPreProcessor())
						.to(SmppFconnection.this.fullUrl)
						.process(new SmppPostProcessor())
						.routeId("out-internet-${SmppFconnection.this.id}")]
			}
		}.routeDefinitions
	}

	private getFullUrl(){
		return "smpp://${this.username}@${this.url}:${this.port}?password=${this.password}&enquireLinkTimer=3000&transactionTimer=5000&systemType=producer"
	}
}