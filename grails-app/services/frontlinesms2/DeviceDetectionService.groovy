package frontlinesms2

import net.frontlinesms.messaging.AllModemsDetector

class DeviceDetectionService {
	static transactional = true
	
	def allModemsDetector = new AllModemsDetector()
	
	// temp variable used to pass acceptance tests.  should be removed when integration tests are in place
	boolean detected

	def detect() {
		detected = true
		allModemsDetector.detect()
	}
	
	def reset() {
		detected = false
		allModemsDetector.reset()
	}

	def getDetected() {
		allModemsDetector.detected.collect { DetectedDevice.create(it) }
	}
}
