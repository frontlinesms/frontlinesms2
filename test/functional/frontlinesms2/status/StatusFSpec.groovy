package frontlinesms2.status

import frontlinesms2.*
import frontlinesms2.message.PageMessageInbox

class StatusFSpec extends StatusBaseSpec {
	def setup() {
		createTestMessages()
	}
	
	def "status tab should be visible in the global navigations"() {
		when:
			to PageMessageInbox
		then:
			$('a#tab-status').displayed
	}
	
	def "clicking on update chart button renders chart"() {
		when:
			to PageStatus
			statusButton.present()
			statusButton.click()
		then:
			at PageStatus
			$('#trafficGraph svg')
	}
	
	def "status tab should show the system status"() {
		when:
			to PageStatus
		then:
			waitFor { $("#indicator").@src == "/frontlinesms2/images/icons/status_green.png" || 
				$("#indicator").@src == "/frontlinesms2/images/icons/status_red.png"}
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
	
	def "should update message count when in Settings section"() {
		when:
			to PageStatus
			def message = new Fmessage(src:'+254999999', dst:'+254112233', text: "message count", status: MessageStatus.INBOUND).save(flush: true, failOnError:true)
		then:
			$("#tab-messages").text() == "Messages 15"
		when:
			js.refreshMessageCount()
		then:
			waitFor{ 
				$("#tab-messages").text() == "Messages 16"
			}
	}
}
