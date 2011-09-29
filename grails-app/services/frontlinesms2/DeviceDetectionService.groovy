package frontlinesms2

import net.frontlinesms.messaging.AllModemsDetector

class DeviceDetectionService {
	static transactional = true
	
	def detector = new AllModemsDetector()

	def detect() {
		detector.detect()
	}
	
	def reset() {
		detector.reset()
	}

	def getDetected() {
		detector.detectors.collect { DetectedDevice.create(it) }
	}
}
