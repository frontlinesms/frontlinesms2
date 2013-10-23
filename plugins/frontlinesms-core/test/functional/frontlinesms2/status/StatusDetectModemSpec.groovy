package frontlinesms2.status

import frontlinesms2.*

import serial.mock.MockSerial

class StatusDetectModemSpec extends grails.plugin.geb.GebSpec {
	def deviceDetectionService

	def 'DETECT MODEMS button should be visible on STATUS tab'() {
		when:
			to PageStatus
		then:
			at PageStatus
			detectModems.displayed
	}
	
	def 'DETECTED DEVICES section should be visible on STATUS tab'() {
		when:
			to PageStatus
		then:
			detectedDevicesSection.displayed
			detectionTitle.equalsIgnoreCase('status.devises.header')
			noDevicesDetectedNotification.displayed
			noDevicesDetectedNotification.text() == 'status.modems.none'
	}

	def 'DETECTED DEVICES list should appear when a device has been detected'() {
		setup:
			remote {
				MockSerial.reset()
				MockSerial.setIdentifier('COM1', new serial.mock.PermanentlyOwnedCommPortIdentifier('COM1', 'a naughty windows application'))
			}
		when:
			to PageStatus
		then:
			noDevicesDetectedNotification.displayed
		when:
			detectModems.click()
		then:
			waitFor { !noDevicesDetectedNotification.displayed }
		when:
			go 'status/resetDetection'
		then:
			noDevicesDetectedNotification.displayed
		cleanup:
			remote { MockSerial.reset() }
	}
}

