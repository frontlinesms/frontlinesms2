package frontlinesms2

import net.frontlinesms.messaging.*

class DeviceDetectorListenerService implements ATDeviceDetectorListener {
	def fconnectionService
	def messageSource
	
	void handleDetectionCompleted(ATDeviceDetector detector) {
		println "#####################################################"
		println "# deviceDetectionService.handleDetectionCompleted() #"
		println "# port: $detector.portName"
		println "# manufacturer: $detector.manufacturer"
		println "# model: $detector.model"
		println "# imsi: $detector.imsi"
		println "# serial: $detector.serial"
		println "# SMS send supported: $detector.smsSendSupported"
		println "# SMS receive supported: $detector.smsReceiveSupported"
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
		} else {
			def name = message(code:'connection.name.autoconfigured', args:[
					detector.manufacturer, detector.model, detector.portName])
			c = new SmslibFconnection(name:name, port:detector.portName, baud:detector.maxBaudRate,
							serial:detector.serial, imsi:detector.imsi)
					.save(flush:true, failOnError:true)
			println "# Created new detector: $c"
		}
		fconnectionService.createRoutes(c)
	}

	private def message(args) {
		return messageSource.getMessage(args.code, args.args as Object[], null)
	}
}

