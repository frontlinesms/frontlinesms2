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
			$("#connections").text() == "You have no connections configured."
	}
	
	def "Shows a list of devices that FrontlineSMS can connect to"() {
		given:
			createTestConnections()
		when:
			to PageStatus
		then:
			$("#connection-${SmslibFconnection.findByName('MTN Dongle').id}").displayed
	}
	
	def "should update message count when in status section"() {
		when:
			to PageStatus
			def message = new Fmessage(src:'+254999999', dst:'+254112233', text: "message count", inbound:true).save(flush: true, failOnError:true)
		then:
			$("#inbox-indicator").text() == "15"
		when:
			js.refreshMessageCount()
		then:
			waitFor { $("#inbox-indicator").text() == "16" }
	}
}
