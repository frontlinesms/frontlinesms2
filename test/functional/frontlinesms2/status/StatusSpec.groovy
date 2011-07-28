package frontlinesms2.status

import frontlinesms2.*
import frontlinesms2.enums.MessageStatus

class StatusSpec extends grails.plugin.geb.GebSpec {
	def setup() {
		createTestMessages()
	}
	
	def cleanup() {
		deleteTestMessages()
	}
	
	//TODO: SVG Functional Tests. Cannot DOM parse. Need to find test libraries for SVG
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

	private deleteTestMessages() {
		Fmessage.findAll()*.delete(flush:true, failOnError:true)
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