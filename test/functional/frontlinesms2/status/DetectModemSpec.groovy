package frontlinesms2.status

import frontlinesms2.*

class DetectModemSpec extends grails.plugin.geb.GebSpec {
	def deviceDetectionService

	def 'DETECT MODEMS button should be visible on STATUS tab'() {
		when:
			go StatusPage
		then:
			detectModems.displayed
	}
	
	def 'DETECTED DEVICES section should be visible on STATUS tab'() {
		when:
			go StatusPage
		then:
			detectedDevicesSection.displayed
			detectedDevicesSection.find('h2').text() == 'Detected devices'
			noDevicesDetectedNotification.displayed
			noDevicesDetectedNotification.text() == 'No devices have been detected yet.'
			!detectedDevicesList.visible
	}

	def 'DETECTED DEVICES list should appear when a device has been detected'() {
		when:
			deviceDetectionService.publishDetection new DetectedDevice(port:'COM1', description:'Kiwanja T1 Test Modem')
			go StatusPage
		then:
			!noDevicesDetectedNotification.displayed
			detectedDevicesList.displayed
		cleanup:
			deviceDetectionService.reset()
	}

	def 'DETECTED DEVICES list should update when a device detection is published'() {
		when:
			go StatusPage
		then:
			noDevicesDetectedNotification.displayed
			!detectedDevicesList.displayed
			detectedDevicesList.find('li').size() == 0
		when:
			deviceDetectionService.publishDetection new DetectedDevice(port:'COM2', description:'Kiwanja T2 Test Modem')
		when:
			waitFor { detectedDevicesList.find('li').size() == 1 }
		cleanup:
			deviceDetectionService.reset()
	}
}

class StatusPage extends geb.Page {
	static url = 'status'
	static content = {
		detectModems { $('.button', href:'status/detectModems') }
		detectedDevicesSection { $('div#detectedDevices') }
		noDevicesDetectedNotification { detectedDevicesSection.find('p') }
	}
}