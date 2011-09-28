package frontlinesms2

import spock.lang.*
import grails.plugin.spock.*

import net.frontlinesms.messaging.ATDeviceDetector

class DetectedDeviceSpec extends UnitSpec {
	def "detected modem should have status GREEN"() {
		given:
			ATDeviceDetector d = Mock()
		when:
			d.finished >> true
			d.detected >> true
		then:
			DetectedDevice.getStatus(d) == DetectionStatus.GREEN
	}

	def "failed detection of modem should have status RED"() {
		given:
			ATDeviceDetector d = Mock()
		when:
			d.finished >> true
			d.detected >> false
		then:
			DetectedDevice.getStatus(d) == DetectionStatus.RED
	}
	
	def "detection in progress should have status AMBER if a device HAS been found"() {
		given:
			ATDeviceDetector d = Mock()
		when:
			d.finished >> false
			d.detected >> true
		then:
			DetectedDevice.getStatus(d) == DetectionStatus.AMBER
	}

	def "detection in progress should have status AMBER if a device HAS NOT been found"() {
		given:
			ATDeviceDetector d = Mock()
		when:
			d.finished >> false
			d.detected >> false
		then:
			DetectedDevice.getStatus(d) == DetectionStatus.AMBER
	}

	def "description should consist of the modem's MANUFACTURER, MODEL and PHONE NUMBER"() {
		given:
			ATDeviceDetector d = Mock()
		when:
			d.manufacturer = "Nokia"
			d.model = "5110"
			d.phoneNumber = "+447890123456"
		then:
			DetectedDevice.getDescription(d) == 'Nokia 5110 (+447890123456)'
	}

	def "if manufacturer is unavailable, description should display 'unknown manufacturer'"() {
		given:
			ATDeviceDetector d = Mock()
		when:
			d.model = "5110"
			d.phoneNumber = "+447890123456"
		then:
			DetectedDevice.getDescription(d) == '[unknown manufacturer] 5110 (+447890123456)'
	}

	def "if model is unavailable, description should display 'unknown model'"() {
		given:
			ATDeviceDetector d = Mock()
		when:
			d.manufacturer = "Nokia"
			d.phoneNumber = "+447890123456"
		then:
			DetectedDevice.getDescription(d) == 'Nokia [unknown model] (+447890123456)'
	}

	def "if phone number is unavailable, description should display 'unknown number'"() {
		given:
			ATDeviceDetector d = Mock()
		when:
			d.manufacturer = "Nokia"
			d.model = "5110"
		then:
			DetectedDevice.getDescription(d) == 'Nokia 5110 (unknown number)'
	}
}

