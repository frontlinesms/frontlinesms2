package frontlinesms2

import net.frontlinesms.messaging.*

class DeviceDetectorListenerService implements ATDeviceDetectorListener {
	def fconnectionService
	
	void handleDetectionCompleted(ATDeviceDetector detector) {
		println "#####################################################"
		println "# deviceDetectionService.handleDetectionCompleted() #"
		println "# port: $detector.portName"
		println "# imsi: $detector.imsi"
		println "# serial: $detector.serial"
		def c = SmslibFconnection.findForDetector(detector).list()
		println "# Found for detector: $c"
		if(c) {
			c = c.get(0)
			def dirty
			if(!c.serial) {
				c.setSerial(detector.serial)
				dirty = true
			}
			if(!c.imsi) {
				c.setImsi(detector.imsi)
				dirty = true
			}
			if(dirty) c.save()
			fconnectionService.createRoutes(c)
		}
		println "#####################################################"
	}
}