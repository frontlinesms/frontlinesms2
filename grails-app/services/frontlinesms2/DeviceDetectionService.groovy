package frontlinesms2

class DeviceDetectionService {
	static transactional = true
	
	boolean detected

	def detect() {
		detected = true
	}
	
	def reset() {
		detected = false
	}

	def getDetected() {
		detected ? [new DetectedDevice(port:'scsi0', description:'zip drive!')] : []
	}
}
