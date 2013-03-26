package frontlinesms2

import frontlinesms2.camel.clickatell.*

import org.apache.camel.Exchange
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.model.RouteDefinition
import frontlinesms2.camel.exception.*

class ClickatellFconnection extends Fconnection {
	private static final String CLICKATELL_URL = 'http://api.clickatell.com/http/sendmsg?'
	static final configFields = [name:null, apiId:null, username:null, password:null, sendToUsa:['fromNumber']]
	static final defaultValues = []
	static String getShortName() { 'clickatell' }
	
	String apiId
	String username
	String password // FIXME maybe encode this rather than storing plaintext
	boolean sendToUsa
	String fromNumber
	boolean sendEnabled = true
	boolean receiveEnabled = false
	
	static constraints = {
		apiId blank:false
		username blank:false
		password blank:false
		fromNumber(nullable: true, validator: { val, obj ->
			return !obj.sendToUsa || val
		})
	}

	static passwords = ['password']

	static mapping = {
		tablePerHierarchy false
		password column: 'clickatell_password'
	}
	
	List<RouteDefinition> getRouteDefinitions() {
		return new RouteBuilder() {
			@Override void configure() {}
			List getRouteDefinitions() {
				return [from("seda:out-${ClickatellFconnection.this.id}")
						.onException(AuthenticationException, InvalidApiIdException, InsufficientCreditException)
									.handled(true)
									.beanRef('fconnectionService', 'handleDisconnection')
									.end()
						.setHeader(Fconnection.HEADER_FCONNECTION_ID, simple(ClickatellFconnection.this.id.toString()))
						.process(new ClickatellPreProcessor())
						.setHeader(Exchange.HTTP_QUERY,
								simple('api_id=${header.clickatell.apiId}&' +
										'user=${header.clickatell.username}&' + 
										'password=${header.clickatell.password}&' + 
										'to=${header.clickatell.dst}&' +
										'text=${body}' +
										(sendToUsa ? '&mo=1&from=${header.clickatell.fromNumber}' : '')))
						.to(CLICKATELL_URL)
						.process(new ClickatellPostProcessor())
						.routeId("out-internet-${ClickatellFconnection.this.id}")]
			}
		}.routeDefinitions
	}
}
