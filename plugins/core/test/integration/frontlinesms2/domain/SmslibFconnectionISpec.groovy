package frontlinesms2.domain

import spock.lang.*
import frontlinesms2.*
import net.frontlinesms.messaging.ATDeviceDetector

class SmslibFconnectionISpec extends grails.plugin.spock.IntegrationSpec {
	@Unroll
	def "find for detector should be #match in some complex situation"() {
		given:
			def c = new SmslibFconnection(name: 'test', port:port, imsi:savedImsi,
					serial:savedSerial).save(failOnError:true)
		expect:
			(findForDetector(port, detectedImsi, detectedSerial)?.id == c.id) == match
		where:
			match | port       | savedImsi  |detectedImsi| savedSerial |detectedSerial
			true  |'/dev/tty01'| null       |'0000000000'| null        |'11111111111'
			true  |'/dev/tty01'|'0000000000'|'0000000000'| ''          |'11111111111'
			true  |'/dev/tty01'|''          |'0000000000'|'11111111111'|'11111111111'
			true  |'/dev/tty01'|'0000000000'|'0000000000'|'11111111111'|'11111111111'
			true  |'/dev/tty01'|''          |'0000000000'| ''          |'11111111111'
			false |'/dev/tty01'|'9999999999'|'0000000000'| null        |'11111111111'
			false |'/dev/tty01'| null       |'0000000000'|'99999999999'|'11111111111'
			false |'/dev/tty01'|'9999999999'|'0000000000'|'11111111111'|'11111111111'
			false |'/dev/tty01'|'0000000000'|'0000000000'|'99999999999'|'11111111111'
	}
	
	private def findForDetector(port, imsi, serial) {
		def d = Mock(ATDeviceDetector)
		d.portName >> port
		d.imsi >> imsi
		d.serial >> serial
		def list = SmslibFconnection.findForDetector(d).list()
		list? list.get(0): null
	}
}