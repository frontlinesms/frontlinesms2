package frontlinesms2

import spock.lang.*
import grails.plugin.spock.*
import net.frontlinesms.messaging.ATDeviceDetector

class DeviceDetectionServiceSpec extends UnitSpec {
	def service = new DeviceDetectionService()
	
	def "stopFor should not interrupt detection of unrelated ports"() {
		given:
			def d = mockDetectors("COM2", "COM3")
		when:
			service.stopFor("COM1")
		then:
			0 * d[0].interrupt()
			0 * d[1].interrupt()
	}
	
	def "stopFor should interrupt detector if one is running for relevant port"() {
		given:
			def d = mockDetectors("COM2", "COM1", "COM3")
		when:
			service.stopFor("COM1")
		then:
			0 * d[0].interrupt()
			1 * d[1].interrupt()
			0 * d[2].interrupt()
	}
	
	def "stopFor should not interrupt detector if detector called stopFor"() {
		given:
			def d = mockDetectors('COM1')[0]
			registerMetaClass Thread
			Thread.metaClass.static.currentThread = { d }
		when:
			service.stopFor("COM1")
		then:
			0 * d.interrupt()
	}
	
	private def mockDetectors(String... names) {
		def detectors = names.collect { mockDetector(it) }
		service.detector = [detectors:detectors]
		return detectors
	}
	
	private ATDeviceDetector mockDetector(String name) {
		ATDeviceDetector d = Mock()
		d.portName >> name
		d
	}
}

