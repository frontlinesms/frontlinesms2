package frontlinesms2

import net.frontlinesms.messaging.ATDeviceDetector

class DetectedDevice {
	String port
	String description
	DetectionStatus status
	String lockType
	
	static DetectedDevice create(ATDeviceDetector d) {
		new DetectedDevice(port:d.portName,
			description:getDescription(d),
			status:getStatus(d),
			lockType:d.lockType)
	}

	static def getDescription(ATDeviceDetector d) {
		def man = d.manufacturer?:'[unknown manufacturer]'
		def model = d.model?:'[unknown model]'
		def num = d.phoneNumber?: 'unknown number'
		"$man $model ($num)"
	}

	static def getStatus(ATDeviceDetector d) {
		if(!d.finished) DetectionStatus.AMBER
		else if(d.detected) DetectionStatus.GREEN
		else DetectionStatus.RED
	}
}
