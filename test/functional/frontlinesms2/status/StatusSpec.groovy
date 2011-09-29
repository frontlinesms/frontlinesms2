package frontlinesms2.status

import frontlinesms2.*

class StatusSpec extends grails.plugin.geb.GebSpec {
	def setup() {
		createTestMessages()
	}
	
	def "status tab should be visible in the global navigations"() {
		when:
			go 'message'
		then:
			$('a#tab-status').displayed
	}
	
	def "clicking on update chart button renders chart"() {
		when:
			to StatusPage
			statusButton.present()
			statusButton.click()
		then:
			at StatusPage
			$('#trafficGraph svg')
	}
	
	def "status tab should show the system status"() {
		when:
			to StatusPage
		then:
			waitFor { $("#indicator").@src == "/frontlinesms2/images/icons/status_green.png" }
	}
	
	def "Does not display connections when there are no connections available"() {
		when:
			go 'status'
		then:
			$("#connections").text() == "You have no connections configured."
	}
	
	def "Shows a list of devices that FrontlineSMS can connect to"() {
		given:
			createTestConnections()
		when:
			go 'status'
		then:
			$("#connection-${SmslibFconnection.findByName('MTN Dongle').id}").displayed
	}
	
	def "should update message count when in Settings section"() {
		when:
			go 'status'
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
	
	private createTestMessages() {
		(new Date()..new Date()-14).each {
			new Fmessage(dateReceived: it, dateCreated: it, src:"+123456789${it}", status: MessageStatus.INBOUND, text: "A message received on ${it}").save()
			new Fmessage(dateReceived: it, dateCreated: it, src:"+123456789${it}", status: MessageStatus.SENT, text: "A message sent on ${it}").save()
		}
	}
	
	def createTestConnections() {
		[new SmslibFconnection(name:'MTN Dongle', port:'stormyPort'),
				new EmailFconnection(name:'Miriam\'s Clickatell account', receiveProtocol:EmailReceiveProtocol.IMAPS, serverName:'imap.zoho.com',
						serverPort:993, username:'mr.testy@zoho.com', password:'mister')].each() {
			it.save(flush:true, failOnError: true)
		}
	}
}
