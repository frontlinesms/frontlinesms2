
package frontlinesms2

class RouteStatusSpec extends grails.plugin.spock.UnitSpec {
	def 'toString converts to nicely formatted text: CONNECTED'() {
		when:
			true
		then:
			RouteStatus.CONNECTED.toString() == "Connected"
	}
	
	def 'toString converts to nicely formatted text: NOT_CONNECTED'() {
		when:
			true
		then:
			RouteStatus.NOT_CONNECTED.toString() == "Not connected"
	}
}

