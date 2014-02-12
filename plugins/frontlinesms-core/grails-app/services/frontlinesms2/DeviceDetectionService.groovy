package frontlinesms2

import grails.util.Environment
import net.frontlinesms.messaging.*

class DeviceDetectionService {
	static transactional = true

	def grailsApplication
	def detector
	
	def init() {
		def deviceDetectorListenerService = grailsApplication.mainContext.deviceDetectorListenerService
		detector = new AllModemsDetector(listener: deviceDetectorListenerService)
		
		if(Environment.current != Environment.TEST) {
			def disableDetect = System.properties['serial.detect.disable']
			log.info "DeviceDetectionService.init() :: disableDetect? $disableDetect"
			if(!disableDetect || !Boolean.parseBoolean(disableDetect)) {
				log.info 'DeviceDetectionService.init() :: detection enabled.  Starting...'
				detect()
			} else log.info 'DeviceDetectionService.init() :: detection disabled.'
		} else log.info 'DeviceDetectionService.init() :: detection disabled as grails environment is test.'
	}

	def detect() {
		detector.refresh()
	}
	
	def reset() {
		detector.reset()
	}

	def getDetected() {
		detector.detectors.collect { DetectedDevice.create(it) }
	}
	
	def stopFor(String port) {
		log.info "DeviceDetectionService.stopFor($port)..."
		def detectorThread
		detector.detectors.each {
			log.info "Checking $it.portName..."
			if(it.portName == port) {
				log.info "Port matched."
				detectorThread = it
			} else log.info "Not the right port."
		}
		if(detectorThread && detectorThread!=Thread.currentThread()) {
			detectorThread.interrupt()
			try { detectorThread.join() } catch(InterruptedException _) {
				// we called interrupt
			}
		}
	}

	def isConnecting(String port) {
		def detectorThread
		detector.detectors.each {
			if(it.portName == port) {
				detectorThread = it
			}
		}
		def threadState
		if(detectorThread && detectorThread!=Thread.currentThread()) {
			threadState = detectorThread.getState()
		}
		return (detectorThread != null && threadState != Thread.State.TERMINATED)
	}
}

