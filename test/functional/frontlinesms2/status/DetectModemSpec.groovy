package frontlinesms2.status

import frontlinesms2.*

import serial.mock.MockSerial

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
			MockSerial.reset()
			MockSerial.setIdentifier('COM1', new serial.mock.PermanentlyOwnedCommPortIdentifier('COM1', 'a naughty windows application'))
		when:
			to StatusPage
		then:
			noDevicesDetectedNotification.displayed
			!detectedDevicesTable.displayed
			detectedDevicesTable.find('tbody tr').size() == 0
		when:
			detectModems.click()
		then:
			waitFor { !noDevicesDetectedNotification.displayed }
			detectedDevicesTable.displayed
		when:
			go 'status/resetDetection'
		then:
			!detectedDevicesTable.displayed
			noDevicesDetectedNotification.displayed
		cleanup:
			MockSerial.reset()
	}
}