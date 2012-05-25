package frontlinesms2

import net.frontlinesms.messaging.*
import serial.CommPortIdentifier
import serial.NoSuchPortException

class DeviceDetectorListenerService implements ATDeviceDetectorListener {
	def fconnectionService
	def i18nUtilService
	
	/**
	 * Handles completion of detection of a device on a database port.
	 * This method is synchronised as concurrent calls can result in multiple
	 * connections to the same device.
	 */
	synchronized void handleDetectionCompleted(ATDeviceDetector detector) {
		def log = { println "# $it" }
		println "#####################################################"
		log "deviceDetectionService.handleDetectionCompleted()"
		log "port: [$detector.portName]"
		log "manufacturer: [$detector.manufacturer]"
		log "model: [$detector.model]"
		log "imsi: [$detector.imsi]"
		log "serial: [$detector.serial]"
		log "SMS send supported: $detector.smsSendSupported"
		log "SMS receive supported: $detector.smsReceiveSupported"

		if(!(detector.smsSendSupported || detector.smsReceiveSupported)) {
			log "No point connecting if no SMS functionality is supported."
			return
		}

		log "Available connections in database:"
		SmslibFconnection.findAll().each { c ->
			log "    $c.id\t$c.port\t$c.serial\t$c.imsi"
		}

		def matchingModemAndSim = SmslibFconnection.findAllBySerialAndImsi(detector.serial, detector.imsi)
		log "Matching modem and SIM in database:"
		matchingModemAndSim.each { c ->
			log "    $c.id\t$c.port\t$c.serial\t$c.imsi"
		}
		if(matchingModemAndSim.any { it.status == RouteStatus.CONNECTED ||
				(it.port != detector.portName && isPortVisible(it.port)) }) {
			log "There was a created route already on this device."
			return
		}

		def connectionToStart
		def exactMatch = matchingModemAndSim.find { it.port == detector.portName }
		if(exactMatch && exactMatch.status != RouteStatus.CONNECTED) {
			log "Found exact match: $exactMatch"
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
		if(connectionToStart) {
			log "Starting connection $connectionToStart with imsi=$connectionToStart.imsi;serial=$connectionToStart.serial"
			fconnectionService.createRoutes(connectionToStart)
		}

		log "After connection, smslibfconnections:"
		SmslibFconnection.withNewSession { SmslibFconnection.findAll().each { c ->
			log "    $c.id\t$c.port\t$c.serial\t$c.imsi"
		} }
	}

	private boolean isPortVisible(String portName) {
		try {
			return CommPortIdentifier.getPortIdentifier(portName) != null
		} catch(NoSuchPortException ex) {
			return false
		}
	}
}

