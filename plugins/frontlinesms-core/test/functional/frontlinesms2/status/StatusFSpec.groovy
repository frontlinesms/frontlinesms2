package frontlinesms2.status

import frontlinesms2.*
import frontlinesms2.message.PageMessageInbox

class StatusFSpec extends StatusBaseSpec {
	def setup() {
		createTestMessages()
	}
	
	def "Does not display connections when there are no connections available"() {
		when:
			to PageStatus
		then:
			noConnections.text() == "You have no connections configured."
	}
	
	def "Shows a list of devices that FrontlineSMS can connect to"() {
		given:
			createTestConnections()
		when:
			to PageStatus
		then:
			connectionByName('MTN Dongle').displayed
	}
	
	def "should update message count when in status section"() {
		when:
			to PageStatus
		then:
			tabs.unreadcount == 15
		when:
			def message = new Fmessage(src:'+254999999', dst:'+254112233', text: "message count", inbound:true).save(flush:true, failOnError:true)
		then:
			waitFor { tabs.unreadcount == 16 }
	}
}

