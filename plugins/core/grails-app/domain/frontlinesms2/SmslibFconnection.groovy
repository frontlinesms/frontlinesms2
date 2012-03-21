package frontlinesms2

import net.frontlinesms.messaging.ATDeviceDetector

import org.apache.camel.builder.RouteBuilder
import org.apache.camel.model.RouteDefinition
import org.smslib.NotConnectedException

class SmslibFconnection extends Fconnection {
	static passwords = ['pin']
	
	private def camelAddress = {
		"smslib:$port?debugMode=true&baud=$baud&pin=$pin&allMessages=$allMessages&smscNumber=$smsc"
	}

	String port
	int baud
	String serial
	String imsi
	String pin // FIXME maybe encode this rather than storing plaintext(?)
	String smsc
	boolean allMessages = true

	static constraints = {
		port(nullable: false, blank: false)
		imsi(nullable: true)
		pin(nullable: true)
		serial(nullable: true)
		smsc(nullable: true)
	}
	
	static namedQueries = {
		findForDetector { ATDeviceDetector d ->
			and {
				or {
					isNull('port')
					eq('port', d.portName)
				}
				or {
					isNull('serial')
					eq('serial', '')
					eq('serial', d.serial)
				}
				or {
					isNull('imsi')
					eq('imsi', '')
					eq('imsi', d.imsi)
				}
			}
		}
	}
	
	List<RouteDefinition> getRouteDefinitions() {
		return new RouteBuilder() {
			@Override void configure() {}
			List getRouteDefinitions() {
				return [from("seda:out-${SmslibFconnection.this.id}")
							.onException(NotConnectedException)
									.handled(true)
									.beanRef('fconnectionService', 'handleDisconnection')
									.end()
							.beanRef('smslibTranslationService', 'toCmessage')
							.to(camelAddress())
							.routeId("out-modem-${SmslibFconnection.this.id}"),
					from(camelAddress())
							.onException(NotConnectedException)
									.handled(true)
									.beanRef('fconnectionService', 'handleDisconnection')
									.end()
							.to('seda:raw-smslib')
							.routeId("in-${SmslibFconnection.this.id}")]
			}
		}.routeDefinitions
	}
}
