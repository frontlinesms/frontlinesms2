package frontlinesms2.services

import frontlinesms2.*
import net.frontlinesms.messaging.*
import grails.plugin.spock.UnitSpec
import spock.lang.*

class DeviceDetectorListenerServiceSpec extends UnitSpec {
	def service
	def fconnectionService
	
	def setup() {
		registerMetaClass(SmslibFconnection)
		service = new DeviceDetectorListenerService()
		fconnectionService = Mock(FconnectionService)
		service.fconnectionService = fconnectionService
	}
	
	def 'handleDetected should do nothing if there are no corresponding configured connections'() {
		given:
			mockFconnections([])
			def d = Mock(ATDeviceDetector)
		when:
			service.handleDetectionCompleted(d)
		then:
			0 * fconnectionService.createRoutes(_)
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
}
