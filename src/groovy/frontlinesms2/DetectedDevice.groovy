package frontlinesms2

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

	}
}
