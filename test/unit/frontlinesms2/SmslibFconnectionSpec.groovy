package frontlinesms2

import grails.plugin.spock.UnitSpec

class SmslibFconnectionSpec extends UnitSpec {
	def 'port should not be nullable or blank'() {
		setup:
			mockForConstraintsTests(SmslibFconnection)
		when:
			def smsLibConnection1 = new SmslibFconnection(port: null)
			def smsLibConnection2 = new SmslibFconnection(port: '')
		then:
			!smsLibConnection1.validate()
			!smsLibConnection2.validate()
	}
}