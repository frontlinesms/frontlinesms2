package frontlinesms2.status

import frontlinesms2.*

class StatusSpec extends grails.plugin.geb.GebSpec {
	def setup() {
		createTestMessages()
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
		
	private createTestMessages() {
		(new Date()..new Date()-14).each {
			new Fmessage(dateReceived: it, dateCreated: it, src:"+123456789${it}", status: MessageStatus.INBOUND, text: "A message received on ${it}").save()
			new Fmessage(dateReceived: it, dateCreated: it, src:"+123456789${it}", status: MessageStatus.SENT, text: "A message sent on ${it}").save()
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
