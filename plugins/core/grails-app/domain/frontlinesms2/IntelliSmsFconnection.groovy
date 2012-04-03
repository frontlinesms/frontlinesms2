package frontlinesms2

import frontlinesms2.camel.intellisms.*

import org.apache.camel.Exchange
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.model.RouteDefinition

class IntelliSmsFconnection extends Fconnection {
	private static final String INTELLISMS_URL = 'http://www.intellisoftware.co.uk/smsgateway'
	static configFields = ['name', 'username', 'password']
	static passwords = ['password']
	static String getShortName() { 'intellisms' }
	
	String username
	String password // FIXME maybe encode this rather than storing plaintext

	List<RouteDefinition> getRouteDefinitions() {
		return new RouteBuilder() {
			@Override void configure() {}
			List getRouteDefinitions() {
				return [from("seda:out-${IntelliSmsFconnection.this.id}")
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
		}.routeDefinitions
	}
}
