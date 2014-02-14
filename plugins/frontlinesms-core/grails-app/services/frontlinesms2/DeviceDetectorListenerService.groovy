package frontlinesms2

import net.frontlinesms.messaging.*
import serial.CommPortIdentifier
import serial.NoSuchPortException

class DeviceDetectorListenerService implements ATDeviceDetectorListener {
	def fconnectionService
	def i18nUtilService
	def appSettingsService
	
	/**
	 * Handles completion of detection of a device on a database port.
	 * This method is synchronised as concurrent calls can result in multiple
	 * connections to the same device.
	 */
	synchronized void handleDetectionCompleted(ATDeviceDetector detector) {
		def logWithPrefix = { log.info "# $it" }
		log.info "#####################################################"
		logWithPrefix "deviceDetectionService.handleDetectionCompleted()"
		logWithPrefix "port: [$detector.portName]"
		logWithPrefix "manufacturer: [$detector.manufacturer]"
		logWithPrefix "model: [$detector.model]"
		logWithPrefix "imsi: [$detector.imsi]"
		logWithPrefix "serial: [$detector.serial]"
		logWithPrefix "SMS send supported: $detector.smsSendSupported"
		logWithPrefix "SMS receive supported: $detector.smsReceiveSupported"

		if(!(detector.smsSendSupported || detector.smsReceiveSupported)) {
			logWithPrefix "No point connecting if no SMS functionality is supported."
			return
		}

		logWithPrefix "Available connections in database:"
		SmslibFconnection.findAll().each { c ->
			logWithPrefix "    $c.id\t$c.port\t$c.serial\t$c.imsi"
		}

		def matchingModemAndSim = SmslibFconnection.findAllBySerialAndImsi(detector.serial, detector.imsi)
		logWithPrefix "Matching modem and SIM in database:"
		matchingModemAndSim.each { c ->
			logWithPrefix "    $c.id\t$c.port\t$c.serial\t$c.imsi"
		}
		if(matchingModemAndSim.any { it.status == ConnectionStatus.CONNECTED ||
				(it.port != detector.portName && isPortVisible(it.port)) }) {
			logWithPrefix "There was a created route already on this device."
			return
		}

		def connectionToStart
		def exactMatch = matchingModemAndSim.find { it.port == detector.portName }
		if(exactMatch && !(exactMatch.status in [ConnectionStatus.CONNECTED, ConnectionStatus.DISABLED])) {
			logWithPrefix "Found exact match: $exactMatch"
			connectionToStart = exactMatch
		} else {
			def c = SmslibFconnection.findForDetector(detector).list()
			if(c) {
				logWithPrefix "Found for detector: $c"
				c = c[0]
				def dirty = !(c.serial && c.imsi)
				if(!c.serial) c.setSerial(detector.serial)
				if(!c.imsi) c.setImsi(detector.imsi)
				if(dirty) c.save()
				if(c.enabled) connectionToStart = c
			} else {
				def name = i18nUtilService.getMessage(code:'connection.name.autoconfigured', args:[
						detector.manufacturer, detector.model, detector.portName])
				connectionToStart = new SmslibFconnection(name:name,
								manufacturer:detector.manufacturer, model:detector.model,
								port:detector.portName, baud:detector.maxBaudRate,
								serial:detector.serial, imsi:detector.imsi)
						.save(flush:true, failOnError:true)
				logWithPrefix "Created new SmslibFconnection: $name"
				addConnectionToRoutingRules(connectionToStart)
			}
		}
		if(connectionToStart) {
			logWithPrefix "Starting connection $connectionToStart with imsi=$connectionToStart.imsi;serial=$connectionToStart.serial"
			fconnectionService.createRoutes(connectionToStart)
		}

		logWithPrefix "After connection, smslibfconnections:"
		SmslibFconnection.withNewSession { SmslibFconnection.findAll().each { c ->
			logWithPrefix "    $c.id\t$c.port\t$c.serial\t$c.imsi"
		} }
	}

	private addConnectionToRoutingRules(connection) {
		def connectionUseSetting = appSettingsService['routing.use']
		appSettingsService['routing.use'] = connectionUseSetting?
			"$connectionUseSetting,fconnection-$connection.id":
			"fconnection-$connection.id"
	}

	private boolean isPortVisible(String portName) {
		try {
			return CommPortIdentifier.getPortIdentifier(portName) != null
		} catch(NoSuchPortException ex) {
			return false
		}
	}
}

