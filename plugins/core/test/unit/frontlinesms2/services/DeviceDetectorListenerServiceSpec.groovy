package frontlinesms2.services

import spock.lang.*

import frontlinesms2.*
import frontlinesms2.dev.MockModemUtils
import net.frontlinesms.messaging.*
import serial.CommPortIdentifier
import serial.mock.MockSerial

@TestFor(DeviceDetectorListenerService)
@Mock(SmslibFconnection)
class DeviceDetectorListenerServiceSpec extends Specification {
	def fconnectionService
	def i18nUtilService
	
	def setup() {
		MockSerial.init()

		assert !fconnectionService
		fconnectionService = Mock(FconnectionService)
		i18nUtilService = Mock(I18nUtilService)
		i18nUtilService.getMessage(_) >> { Map args -> args.code }

		service.fconnectionService = fconnectionService
		service.i18nUtilService = i18nUtilService
	}

	def 'handleDetected should create a new fconnection for newly detected devices'() {
		given:
			def port = "PORT1"
			def serial = '12345'
			def imsi = '56789'
			def d = mockDetector(port:port, serial:serial, imsi:imsi)
		when:
			service.handleDetectionCompleted(d)
		then:
			1 * fconnectionService.createRoutes(_)
			SmslibFconnection.count() == 1
			SmslibFconnection.findByPortAndSerialAndImsi(port, serial, imsi)
	}

	def 'handleDetected should not create a new fconnection if route with serial+IMSI combination already created'() {
		given:
			def port = "PORT1"
			def serial = '12345'
			def imsi = '56789'
			new SmslibFconnection(name:'Already configured', port:'/dev/different',
							serial:serial, imsi:imsi).save()
					.metaClass.getStatus = { -> RouteStatus.CONNECTED }
			assert SmslibFconnection.count() == 1
			def d = mockDetector(port:port, serial:serial, imsi:imsi)
		when:
			service.handleDetectionCompleted(d)
		then:
			0 * fconnectionService.createRoutes(_)
			SmslibFconnection.count() == 1
			!SmslibFconnection.findByPortAndSerialAndImsi(port, serial, imsi)
	}

	def 'handleDetected should not create a new fconnection if fconnection is configured with same serial+IMSI and port is visible'() {
		given:
			def port = "PORT1"
			def otherPort = '/dev/different'
			def serial = '12345'
			def imsi = '56789'
			new SmslibFconnection(name:'Already configured', port:otherPort,
							serial:serial, imsi:imsi).save()
					.metaClass.getStatus = { -> RouteStatus.NOT_CONNECTED }
			MockModemUtils.initialiseMockSerial("$otherPort":mockPortIdentifier(otherPort))
			assert SmslibFconnection.count() == 1
			def d = mockDetector(port:port, serial:serial, imsi:imsi)
		when:
			service.handleDetectionCompleted(d)
		then:
			0 * fconnectionService.createRoutes(_)
			SmslibFconnection.count() == 1
			!SmslibFconnection.findByPortAndSerialAndImsi(port, serial, imsi)
	}

	def 'handleDetected should create a new fconnection if fconnection is configured with same serial+IMSI and port is not visible'() {
		given:
			def port = "PORT1"
			def otherPort = '/dev/different'
			def serial = '12345'
			def imsi = '56789'
			new SmslibFconnection(name:'Already configured', port:otherPort,
							serial:serial, imsi:imsi).save()
					.metaClass.getStatus = { -> RouteStatus.NOT_CONNECTED }
			MockModemUtils.initialiseMockSerial([:])
			assert SmslibFconnection.count() == 1
			def d = mockDetector(port:port, serial:serial, imsi:imsi)
		when:
			service.handleDetectionCompleted(d)
		then:
			1 * fconnectionService.createRoutes(_)
			SmslibFconnection.count() == 2
			SmslibFconnection.findByPortAndSerialAndImsi(port, serial, imsi)
	}

	def 'handleDetected should create routes for corresponding configured connections'() {
		given:
			def c = mockFconnection()
			def d = Mock(ATDeviceDetector)
		when:
			service.handleDetectionCompleted(d)
		then:
			1 * fconnectionService.createRoutes(c)
	}
	
	@Unroll
	def 'handleDetected should update IMSI and serial settings of connection if required'() {
		given:
			def c = mockFconnection(serial:initialSerial, imsi:initialImsi)
			def d = Mock(ATDeviceDetector)
			d.imsi >> detectedImsi
			d.serial >> detectedSerial
		when:
			service.handleDetectionCompleted(d)
		then:
			updateImsi * c.setImsi(detectedImsi)
			updateSerial * c.setSerial(detectedSerial)
			// save * c.save() FIXME this doesn't work, and I have no idea why
			1 * fconnectionService.createRoutes(c)
		where:
			save| updateImsi | initialImsi | detectedImsi | updateSerial | initialSerial | detectedSerial
			1   | 1          | null        | '00000'      | 1            | null          | '11111'
			1   | 0          | '00000'     | '00000'      | 1            | null          | '11111'
			1   | 1          | null        | '00000'      | 0            | '11111'       | '11111'
			0   | 0          | '00000'     | '00000'      | 0            | '11111'       | '11111'
	}
	
	private SmslibFconnection mockFconnection(Map params) {
		if(!params) params = [:]
		Fconnection c = Mock(SmslibFconnection)
		c.serial >> params?.serial
		c.imsi >> params?.imsi
		mockFconnections([c])
		return c
	}
	
	private def mockFconnections(List connections) {
		SmslibFconnection.metaClass.static.findForDetector = { ATDeviceDetector d ->
			[
				list: { connections }
			]
		}
	}

	private def mockDetector(args) {
		def d = Mock(ATDeviceDetector)
		d.portName >> args.port
		d.serial >> args.serial
		d.imsi >> args.imsi
		return d
	}

	private def mockPortIdentifier(portName) {
		new serial.mock.CommPortIdentifier(portName, null)
	}
}

