package frontlinesms2

import net.frontlinesms.messaging.*
import serial.CommPortIdentifier
import serial.NoSuchPortException

class DeviceDetectorListenerService implements ATDeviceDetectorListener {
	def fconnectionService
	def i18nUtilService
	
	void handleDetectionCompleted(ATDeviceDetector detector) {
		def log = { println "# $it" }
		println "#####################################################"
		log "deviceDetectionService.handleDetectionCompleted()"
		log "port: $detector.portName"
		log "manufacturer: $detector.manufacturer"
		log "model: $detector.model"
		log "imsi: $detector.imsi"
		log "serial: $detector.serial"
		log "SMS send supported: $detector.smsSendSupported"
		log "SMS receive supported: $detector.smsReceiveSupported"

		def matchingModemAndSim = SmslibFconnection.findAllBySerialAndImsi(detector.serial, detector.imsi)
		if(matchingModemAndSim.any { it.status == RouteStatus.CONNECTED || isPortVisible(it.port) }) {
			log "There was a created route already on this device."
			return
		}

		def connectionToStart
		def exactMatch = matchingModemAndSim.find { it.port == detector.portName }
		if(exactMatch) {
			connectionToStart = exactMatch
		} else {
			def c = SmslibFconnection.findForDetector(detector).list()
			if(c) {
				log "Found for detector: $c"
				c = c[0]
				def dirty = !(c.serial && c.imsi)
				if(!c.serial) c.setSerial(detector.serial)
				if(!c.imsi) c.setImsi(detector.imsi)
				if(dirty) c.save()
				connectionToStart = c
			} else {
				def name = i18nUtilService.getMessage(code:'connection.name.autoconfigured', args:[
						detector.manufacturer, detector.model, detector.portName])
				connectionToStart = new SmslibFconnection(name:name, port:detector.portName, baud:detector.maxBaudRate,
								serial:detector.serial, imsi:detector.imsi)
						.save(flush:true, failOnError:true)
				log "Created new detector: $name"
			}
		}
		if(connectionToStart) fconnectionService.createRoutes(connectionToStart)
	}

	private boolean isPortVisible(String portName) {
		try {
			return CommPortIdentifier.getPortIdentifier(portName) != null
		} catch(NoSuchPortException ex) {
			return false
		}
	}
}

