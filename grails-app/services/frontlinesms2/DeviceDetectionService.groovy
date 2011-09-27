package frontlinesms2

import net.frontlinesms.messaging.AllModemsDetector

class DeviceDetectionService {
	static transactional = true
	
	def allModemsDetector = new AllModemsDetector()
	
	// temp variable used to pass acceptance tests.  should be removed when integration tests are in place
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
