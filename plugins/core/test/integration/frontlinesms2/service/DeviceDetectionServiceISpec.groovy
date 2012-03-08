package frontlinesms2.service

import frontlinesms2.*
import net.frontlinesms.messaging.AllModemsDetector
import net.frontlinesms.messaging.ATDeviceDetector

/*
 * TODO: modify net.frontlinesms.messaging.AllModemsDetector and merge it into smslib
 * nb no need to call CommTest anymore as this is abstracted when we use serial.* classes.
 * -> will want to add manufacturer and model detection to the detector so we get nice outputs.
 */

class DeviceDetectionServiceISpec extends grails.plugin.spock.IntegrationSpec {
	def deviceDetectionService
	AllModemsDetector detector
	
	def setup() {
		detector = Mock()
		deviceDetectionService.detector = detector
	}
	
	def 'reset should stop detection of all smslib devices and remove references for them from the service'() {
		when:
			deviceDetectionService.reset()
		then:
			1 * detector.reset()
	}
	
	def 'detect should trigger smslib device detection on all available ports'() {
		when:
			deviceDetectionService.detect()
		then:
			1 * detector.refresh()
	}
	
	def 'getDetected() should return an empty list if there are no ports detected or being searched'() {
		given:
			detector.getDetectors() >> []
		when:
			def detected = deviceDetectionService.detected
		then:
			detected == []
	}
	
	def 'getDetected() should return a list containing detected devices and ports being searched'() {		
		given:
			detector.getDetectors() >> [createDetector(portName:'tty0', manufacturer:'test device', model:'1', finished:true),
					createDetector(portName:'COM1', manufacturer:'test device', model:'2'),
					createDetector(portName:'dev/modem', manufacturer:'test device', model:'3', finished:true, detected:true)]
		when:
			def detected = deviceDetectionService.detected
		then:
			detected*.port == ['tty0', 'COM1', 'dev/modem']
			detected*.description == ['test device 1 (unknown number)', 'test device 2 (unknown number)', 'test device 3 (unknown number)']
			detected*.status == [DetectionStatus.RED, DetectionStatus.AMBER, DetectionStatus.GREEN]
	}
	
	private ATDeviceDetector createDetector(p) {
		def d = Mock(ATDeviceDetector)
		p.each { k, v ->
			d."$k" >> v
		}
		return d
	}
}
