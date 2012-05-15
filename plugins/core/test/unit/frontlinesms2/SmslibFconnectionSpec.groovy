package frontlinesms2

import grails.plugin.mixin.*
import spock.lang.*

@TestFor(SmslibFconnection)
class SmslibFconnectionSpec extends Specification {
	def 'port should not be nullable or blank'() {
		when:
			def smsLibConnection1 = new SmslibFconnection(port: null)
			def smsLibConnection2 = new SmslibFconnection(port: '')
		then:
			!smsLibConnection1.validate()
			!smsLibConnection2.validate()
	}

	def "should contain errors when invalid baud rate is provided"() {
		when:
			def conn = new SmslibFconnection(name:"testConnection", baud:"invalid", port:"/dev/ttyUSB0")
		then:
			conn.errors.hasFieldErrors("baud")
	}
}
