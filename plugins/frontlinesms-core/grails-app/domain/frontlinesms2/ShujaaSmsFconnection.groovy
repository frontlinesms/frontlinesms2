package frontlinesms2

import frontlinesms2.camel.shujaasms.*

import org.apache.camel.Exchange
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.model.RouteDefinition
import frontlinesms2.camel.exception.*

class ShujaaSmsFconnection extends Fconnection {
	private static final String SHUJAASMS_URL = 'http://sms.shujaa.mobi/sendsms?'
	static final configFields = [name:null, username:null, password:null, account:null, source:null, network:null]
	static defaultValues = []
	static String getShortName() { 'shujaasms' }
		
	String username
	String password
	String account
	String source
	String network
	
	static constraints = {
		username blank:false
		password blank:false
		account blank:false
		source blank:false
		network blank:false
	}

	static passwords = ['password']
	

	static mapping = {
		password column: 'shujaa_password'
	}
	
	List<RouteDefinition> getRouteDefinitions() {
		return new RouteBuilder() {
			@Override void configure() {}
			List getRouteDefinitions() {
				return [from("seda:out-${ShujaaSmsFconnection.this.id}")
						.onException(AuthenticationException, InvalidApiIdException, InsufficientCreditException)
									.handled(true)
									.beanRef('fconnectionService', 'handleDisconnection')
									.end()
						.setHeader(Fconnection.HEADER_FCONNECTION_ID, simple(ShujaaSmsFconnection.this.id.toString()))
						.process(new ShujaaSmsPreProcessor())
						.setHeader(Exchange.HTTP_QUERY,
								simple(	'username=${header.shujaasms.username}&' + 
										'password=${header.shujaasms.password}&' +
										'account=${header.shujaasms.account}&' +
										'source=${header.shujaasms.source}&' +
										'network=${header.shujaasms.network}&' +
										'destination=${header.shujaasms.dst}&' +
										'message=${body}'
										))
						.to(SHUJAASMS_URL)
						.process(new ShujaaSmsPostProcessor())
						.routeId("out-internet-${ShujaaSmsFconnection.this.id}")]
			}
		}.routeDefinitions
	}
}