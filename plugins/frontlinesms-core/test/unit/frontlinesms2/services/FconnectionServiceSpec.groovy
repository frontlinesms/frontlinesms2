package frontlinesms2.services

import spock.lang.*

import frontlinesms2.*

import org.apache.camel.CamelContext
import org.apache.camel.Exchange

@TestFor(FconnectionService)
@Mock([LogEntry, Dispatch, Fmessage, SystemNotification])
class FconnectionServiceSpec extends Specification {
	def context
	def systemNotificationService = Mock(SystemNotificationService)

	def setup() {
		context = Mock(CamelContext)
		service.camelContext = context
		def i18nUtilService = Mock(I18nUtilService)
		i18nUtilService.getMessage(_) >> { args -> args.code[0] }
		service.i18nUtilService = i18nUtilService
		service.systemNotificationService = systemNotificationService
		Fconnection.metaClass.static.get = { Serializable id -> println "overrided 'get()' called"; return [] }
		def logService = Mock(LogService)
		logService.handleRouteCreated = {/* Impostor! */}
		logService.handleRouteCreationFailed = {/* Another impostor! */}
		service.logService = logService

		service.deviceDetectionService = Mock(DeviceDetectionService)
	}

	def 'Unconnected enabled Fconnection gives a status of FAILED'() {
		given:
			context.routes >> []
			def c = mockFconnection(1, true)
		when:
			def status = service.getConnectionStatus(c)
		then:
			status == ConnectionStatus.FAILED
	}

	def 'Unconnected enabled SmslibFconnection gives a status of NOT_CONNECTED'() {
		given:
			context.routes >> []
			def c = mockSmslibFconnection(1, true)
		when:
			def status = service.getConnectionStatus(c)
		then:
			status == ConnectionStatus.NOT_CONNECTED
	}

	def 'Connecting enabled SmslibFconnection gives a status of CONNECTING'() {
		given:
			context.routes >> []
			def c = mockSmslibFconnection(1, true)
			service.deviceDetectionService.isConnecting(_) >> { ConnectionStatus.CONNECTING }
		when:
			def status = service.getConnectionStatus(c)
		then:
			status == ConnectionStatus.CONNECTING
	}

	def 'Disabled Fconnection gives a status of DISABLED'() {
		given:
			def disabledConnection = mockFconnection(2, false)
		when:
			true
		then:
			service.getConnectionStatus(disabledConnection) == ConnectionStatus.DISABLED
	}

	def 'Connected Fconnection gives a status of CONNECTED'() {
		given:
			def connected = mockFconnection(1)
			def alsoConnected = mockFconnection(3)
			context.routes >> ["in-1", "out-3"].collect { [id:it] }
		when:
			true
		then:
			service.getConnectionStatus(connected) == ConnectionStatus.CONNECTED
			service.getConnectionStatus(alsoConnected) == ConnectionStatus.CONNECTED
	}

	@Unroll
	def 'test route statuses'() {
		given:
			context.routes >> routeNames.collect { [id:it] }
			def c = mockFconnection(id, enabled)
		expect:
			service.getConnectionStatus(c) == expectedStatus
		where:
			id | routeNames                 | enabled | expectedStatus
			1  | []                         | false   | ConnectionStatus.DISABLED
			1  | ['in-2', 'out-modem-3']    | true    | ConnectionStatus.FAILED
			1  | ['in-1']                   | true    | ConnectionStatus.CONNECTED
			1  | ['in-1', 'out-internet-1'] | true    | ConnectionStatus.CONNECTED
			1  | ['in-1', 'out-modem-1']    | true    | ConnectionStatus.CONNECTED
			1  | ['out-internet-1']         | true    | ConnectionStatus.CONNECTED
			1  | ['out-modem-1']            | true    | ConnectionStatus.CONNECTED
	}

	def 'creating a SMSLib route should stop detection on the corresponding port'() {
		given:
			final String PORT = 'COM77'
			DeviceDetectionService detector = Mock()
			service.deviceDetectionService = detector
			Fconnection f = new SmslibFconnection(port:PORT)
		when:
			service.createRoutes(f)
		then:
			1 * detector.stopFor(PORT)
	}

	def 'creating a non-smslib route should not affect the device detector'() {
		given:
			DeviceDetectionService detector = Mock()
			service.deviceDetectionService = detector
			Fconnection f = Mock()
			f.enabled >> true
			f.getCamelProducerAddress() >> 'nothing'
		when:
			service.createRoutes(f)
		then:
			0 * detector.stopFor(_)
	}

	def 'Routes supplied by the Fconnection are added to the camel context'() {
		given:
			def c = Mock(Fconnection)
			c.routeDefinitions >> [[id:'in-1'], [id:'out-modem-1']]
			c.enabled >> true
		when:
			service.createRoutes(c)
		then:
			1 * context.addRouteDefinitions { it*.id.sort() == ['in-1', 'out-modem-1'] }
	}

	@Unroll
	def 'handleDisconnection should trigger shutdown of related Fconnection'() {
		given:
			int jobRouteId
			RouteDestroyJob.metaClass.static.triggerNow = { Map args ->
				jobRouteId = args.routeId
			}
			Exchange exchange = Mock()
			exchange.fromRouteId >> routeId
		when:
			service.handleDisconnection(exchange)
		then:
			1 * systemNotificationService.createSystemNotification(_,_,_)
			jobRouteId == connectionId
		where:
			routeId          | connectionId
			"out-modem-1"    | 1
			"out-internet-2" | 2
			"out-modem-3"    | 3
			"in-4"           | 4
	}

	@Unroll
	def 'destroyRoutes should stop and remove all relevant routes'() {
		given:
			context.routes >> (relatedRoutes + unrelatedRoutes).collect { [id:it] }
		when:
			service.destroyRoutes(1)
		then:
			relatedRoutes.size() * context.stopRoute(_)
			relatedRoutes.size() * context.removeRoute(_)
		where:
			relatedRoutes      | unrelatedRoutes
			['in-1']           | []
			['out-1']          | []
			['in-1', 'out-1']  | []
			['in-1', 'out-1']  | ['in-2', 'out-3']
			['out-modem-1']    | ['in-2', 'out-modem-3']
			['out-internet-1'] | ['in-2', 'out-modem-3']
			['out-internet-1'] | ['in-2', 'out-internet-3']
	}

	private def mockSmslibFconnection(int id, boolean enabled=true) {
		mockFconnection(SmslibFconnection, id, enabled)
	}

	private def mockFconnection(int id, boolean enabled=true) {
		mockFconnection(Fconnection, id, enabled)
	}

	private def mockFconnection(clazz, int id, boolean enabled) {
		def c = Mock(clazz)
		c.id >> id
		c.enabled >> enabled
		return c
	}
}
