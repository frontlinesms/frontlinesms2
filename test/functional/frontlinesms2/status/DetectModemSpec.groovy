package frontlinesms2.status

import frontlinesms2.*

class DetectModemSpec extends grails.plugin.geb.GebSpec {
	def deviceDetectionService

	def 'DETECT MODEMS button should be visible on STATUS tab'() {
		when:
			to StatusPage
		then:
			detectModems.displayed
	}
	
	def 'DETECTED DEVICES section should be visible on STATUS tab'() {
		when:
			to StatusPage
		then:
			detectedDevicesSection.displayed
			detectedDevicesSection.find('h2').text() == 'Detected devices'
			noDevicesDetectedNotification.displayed
			noDevicesDetectedNotification.text() == 'No devices have been detected yet.'
			!detectedDevicesTable.displayed
	}

	def 'DETECTED DEVICES list should appear when a device has been detected'() {
		setup:
			// TODO mock a single connection on COM1 with manufacturer Kiwanja and model T1 Test Modem
		when:
			to StatusPage
		then:
			noDevicesDetectedNotification.displayed
			!detectedDevicesTable.displayed
			detectedDevicesTable.find('tbody tr').size() == 0
		when:
			detectModems.click()
		then:
			waitFor { !noDevicesDetectedNotification.displayed } // TODO we need to refresh the page here?  Should JS be updating this automatically?
			detectedDevicesTable.displayed
		when:
			go 'status/resetDetection'
		then:
			!detectedDevicesTable.displayed
			noDevicesDetectedNotification.displayed
	}
}