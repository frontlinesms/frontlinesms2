package frontlinesms2.status

import frontlinesms2.*

class StatusSpec extends grails.plugin.geb.GebSpec {
	def setup() {
		createTestMessages()
	}
	
	def "status tab should visible in the global navigations"() {
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
	
	def "should show a list of devices that FrontlineSMS can connect to"() {
		given:
			createTestConnections()
		when:
			go 'status'
		then:
			$("#connection-${SmslibFconnection.findByName('MTN Dongle').id}").displayed
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

class StatusPage extends geb.Page {
	static url = 'status'
	static at = {
		title.startsWith('Status')
	}
	static content = {
		statusButton { $('#update-chart') }
	}
}
