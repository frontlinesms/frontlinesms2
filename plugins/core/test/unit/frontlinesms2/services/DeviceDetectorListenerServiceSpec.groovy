package frontlinesms2.services

import frontlinesms2.*
import net.frontlinesms.messaging.*
import grails.plugin.spock.UnitSpec

class DeviceDetectorListenerServiceSpec extends UnitSpec {
	def service
	def fconnectionService
	
	def setup() {
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
	
	private Fconnection mockFconnection() {
		Fconnection c = Mock(Fconnection)
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
