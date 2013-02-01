package frontlinesms2

import net.frontlinesms.messaging.ATDeviceDetector

class DetectedDevice {
	String port
	String manufacturer
	String model
	String description
	DetectionStatus status
	String lockType
	boolean smsSendSupported
	boolean smsReceiveSupported
	
	static DetectedDevice create(ATDeviceDetector d) {
println "alxndrsn: DetectedDevice.create() :: manufacturer=$d.manufacturer; model=$d.model"
		new DetectedDevice(port:d.portName,
			manufacturer:d.manufacturer,
			model:d.model,
			description:getDescription(d),
			status:getStatus(d),
			lockType:d.lockType,
			smsSendSupported:d.smsSendSupported,
			smsReceiveSupported:d.smsReceiveSupported)
	}

	static def getDescription(ATDeviceDetector d) {
		def man = d.manufacturer?:'[unknown manufacturer]'
		def model = d.model?:'[unknown model]'
		def num = d.phoneNumber?: 'unknown number'
		"$man $model ($num)"
	}

	static def getStatus(ATDeviceDetector d) {
		if(!d.finished) DetectionStatus.AMBER
		else if(d.detected) DetectionStatus.GREEN
		else DetectionStatus.RED
	}
}

