package frontlinesms2

import frontlinesms2.camel.routesms.*

import org.apache.camel.Exchange
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.model.RouteDefinition
import frontlinesms2.camel.exception.*

class RoutesmsFconnection extends Fconnection {
  private static final String ROUTESMS_URL = "http://121.241.242.114/websms/"
	static final configFields = ["name", "username", "password"]
	static final defaultValues = []
	static String getShortName() { "routesms" }
	
	String username
	String password // FIXME maybe encode this rather than storing plaintext
        String message
        /** 
        *String type
        *String dlr
        *String destination
	*String source
        *String server
        */

	static passwords = ['password']

	static mapping = {
		password column: 'routesms_password'
	}
	
	List<RouteDefinition> getRouteDefinitions() {
		return new RouteBuilder() {
			@Override void configure() {}
			List getRouteDefinitions() {
				return [from("seda:out-${RoutesmsFconnection.this.id}")
						.onException(AuthenticationException, InvalidApiIdException)
									.handled(true)
									.beanRef('fconnectionService', 'handleDisconnection')
									.end()
						.setHeader(Fconnection.HEADER_FCONNECTION_ID, simple(ClickatellFconnection.this.id.toString()))
						.process(new ClickatellPreProcessor())
						.setHeader(Exchange.HTTP_QUERY,
								simple('api_id=${header.routesms.apiId}&' +
										'user=${header.routesms.username}&' + 
										'password=${header.routesms.password}&' + 
										'to=${header.routesms.dst}&' +
										'text=${body}'))
						.to(ROUTESMS_URL)
						.process(new RoutesmsPostProcessor())
						.routeId("out-internet-${RoutesmsFconnection.this.id}")]
			}
		}.routeDefinitions
	}
}
