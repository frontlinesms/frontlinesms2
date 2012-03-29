package frontlinesms2

import frontlinesms2.camel.intellisms.*

import org.apache.camel.Exchange
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.model.RouteDefinition

class IntelliSMSFconnection extends Fconnection {
	private static final String INTELLISMS_URL = 'http://www.intellisoftware.co.uk/smsgateway'
	
	String username
	String password // FIXME maybe encode this rather than storing plaintext

	static passwords = ['password']

	List<RouteDefinition> getRouteDefinitions() {
		return new RouteBuilder() {
			@Override void configure() {}
			List getRouteDefinitions() {
				return [from("seda:out-${IntelliSMSFconnection.this.id}")
						.process(new IntelliSMSPreProcessor())
						.setHeader(Exchange.HTTP_URI,
								simple(INTELLISMS_URL + '/sendmsg.aspx?' + 
										'username=${header.intellisms.username}&' + 
										'password=${header.intellisms.password}&' + 
										'to=${header.intellisms.dst}&' +
										'text=${body}'))
						.to(INTELLISMS_URL)
						.process(new IntelliSMSPostProcessor())
						.routeId("out-internet-${IntelliSMSFconnection.this.id}")]
			}
		}.routeDefinitions
	}
}
