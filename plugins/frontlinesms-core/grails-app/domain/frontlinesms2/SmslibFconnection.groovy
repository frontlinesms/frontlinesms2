package frontlinesms2

import net.frontlinesms.messaging.ATDeviceDetector

import org.apache.camel.builder.RouteBuilder
import org.apache.camel.model.RouteDefinition
import org.smslib.NotConnectedException

class SmslibFconnection extends Fconnection {
	static passwords = ['pin']
	static configFields = ['name', 'manufacturer', 'model', 'port', 'baud', 'pin', 'imsi', 'serial', 'sendEnabled', 'receiveEnabled']
	static defaultValues = [sendEnabled:true, receiveEnabled:true, baud:9600]
	static String getShortName() { 'smslib' }
	
	private def camelAddress = {
		def optional = { name, val ->
			return val? "&$name=$val": ''
		}
		"smslib:$port?debugMode=true&baud=$baud${optional('pin', pin)}&allMessages=$allMessages${optional('manufacturer', manufacturer)}${optional('model', model)}"
	}

	String manufacturer
	String model
	String port
	int baud
	String serial
	String imsi
	String pin // FIXME maybe encode this rather than storing plaintext(?)
	boolean allMessages = true
	// TODO rename sendEnabled
	boolean sendEnabled = true
	// TODO rename receiveEnabled
	boolean receiveEnabled = true

	static constraints = {
		manufacturer nullable:true
		model nullable:true
		port blank:false
		imsi nullable:true
		pin nullable:true
		serial nullable:true
		sendEnabled(nullable:true, validator: { val, obj ->
			if(!val) {
				return obj.receiveEnabled
			}
		})
		receiveEnabled(nullable:true, validator: { val, obj ->
			if(!val) {
				return obj.sendEnabled
			}
		})
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
				def definitions = []
				if(isSendEnabled()) {
					definitions << from("seda:out-${SmslibFconnection.this.id}")
							.onException(NotConnectedException)
									.handled(true)
									.beanRef('fconnectionService', 'handleDisconnection')
									.end()
							.setHeader(Fconnection.HEADER_FCONNECTION_ID, simple(SmslibFconnection.this.id.toString()))
							.beanRef('smslibTranslationService', 'toCmessage')
							.to(camelAddress())
							.routeId("out-modem-${SmslibFconnection.this.id}")
				}
				if(isReceiveEnabled()) {
					definitions << from(camelAddress())
							.onException(NotConnectedException)
									.handled(true)
									.beanRef('fconnectionService', 'handleDisconnection')
									.end()
							.setHeader(Fconnection.HEADER_FCONNECTION_ID, simple(SmslibFconnection.this.id.toString()))
							.to('seda:raw-smslib')
							.routeId("in-${SmslibFconnection.this.id}")
				}
				return definitions
			}
		}.routeDefinitions
	}
}
