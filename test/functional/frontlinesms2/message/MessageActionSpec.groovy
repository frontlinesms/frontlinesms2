package frontlinesms2.message

import frontlinesms2.*

class MessageActionSpec extends frontlinesms2.poll.PollGebSpec {
	def 'message actions menu is displayed for all individual messages'() {
		given:
			createTestPolls()
			createTestMessages()
		when:
			to PollMessageViewPage
			def folders = $('#message-actions li a')*.text() 
			
		then:
			folders[0] == 'Football Teams'
			folders[1] == 'Shampoo Brands'			
		cleanup:
			deleteTestPolls()
			//deleteTestMessages()

	}
	
	def 'clicking on activity moves the message to that activity and removes it from the previous activity'() {
		given:
			createTestPolls()
			createTestMessages()
		when:
			to PollMessageViewPage
			def folder = $('#message-actions li:first-child')
			def bob = Fmessage.findBySrc('Bob')
			folder.click() 
		then:
			Poll.findByTitle("Shampoo Brands").getMessages().find { bob }
			!Poll.findByTitle("Football Teams").getMessages().find { bob }
				
		cleanup:
			deleteTestPolls()
	}
	
}
class PollMessageViewPage extends geb.Page {
 	static getUrl() { "message/poll/${Poll.findByTitle('Football Teams').id}/show/${Fmessage.findBySrc("Bob").id}" }
}
