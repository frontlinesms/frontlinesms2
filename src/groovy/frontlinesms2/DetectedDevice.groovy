package frontlinesms2

import net.frontlinesms.messaging.ATDeviceDetector

class DetectedDevice {
	String port
	String description
	DetectionStatus status
	
	static DetectedDevice create(ATDeviceDetector d) {
		new DetectedDevice(port:d.port, description:getDescription(d), status:getStatus(d))
	}

	static def getDescription(ATDeviceDetector d) {

	}

	static def getStatus(ATDeviceDetector d) {
		if(!d.finished) DetectionStatus.AMBER
		else if(d.detected) DetectionStatus.GREEN
		else DetectionStatus.RED
	}
}
