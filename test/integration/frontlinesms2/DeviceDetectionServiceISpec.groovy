package frontlinesms2

import net.frontlinesms.messaging.AllModemsDetector
import net.frontlinesms.messaging.ATDeviceDetector

/*
 * TODO: modify net.frontlinesms.messaging.AllModemsDetector and merge it into smslib
 * nb no need to call CommTest anymore as this is abstracted when we use serial.* classes.
 * -> will want to add manufacturer and model detection to the detector so we get nice outputs.
 */

class DeviceDetectionServiceISpec extends grails.plugin.spock.IntegrationSpec {
	def service
	def detecor
	
	def setup() {
/*		service = new DeviceDetectionService() // should this be injected?  we will see... */
		detector = mock(AlLModemsDetector)
	}
	
	def 'reset should stop detection of all smslib devices and remove references for them from the service'() {
		when:
			service.reset()
		then:
			1 * detector.reset()
	}
	
	def 'detect should trigger smslib device detection on all available ports'() {
		when:
			service.detect()
		then:
			1 * detector.detect()
	}
	
	def 'getDetected() should return an empty list if there are no ports detected or being searched'() {
		given:
			detector.getDetected() << []
		when:
			def detected = detector.detected
		then:
			detected == []
	}
	
	def 'getDetected() should return a list containing detected devices and ports being searched'() {		
		given:
			detector.getDetected() << [new ATDeviceDetector(port:'tty0', manufacturer:'test device', model:'1'),
					new ATDeviceDetector(port:'COM1', manufacturer:'test device', model:'2'),
					new ATDeviceDetector(port:'dev/modem', manufacturer:'test device', model:'3')]
		when:
			def detected = detector.detected
		then:
			detected*.port == ['tty0', 'COM1', 'dev/modem']
			detected*.description == ['test device 1', 'test device 2', 'test device 3']
	}
}