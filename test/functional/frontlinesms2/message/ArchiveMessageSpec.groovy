package frontlinesms2.message

import frontlinesms2.*

class ArchiveMessageSpec extends grails.plugin.geb.GebSpec {
	def setup() {
		createTestData()
		assert Fmessage.getInboxMessages(['starred':false, 'archived': false]).size() == 3
		assert Poll.findByTitle('Miauow Mix').getMessages(['starred':false]).size() == 2
		assert Folder.findByName('Fools').messages.size() == 2
	}

	def 'archived messages do not show up in inbox view'() {
		when:
			goToArchivePage()
			waitFor { $("#no-messages").text() == "No messages"}
		then:
			$("#no-messages").text() == 'No messages'
		when:
			go "message/inbox/show/${Fmessage.findBySrc('Bob').id}"
			def btnArchive = $('#message-details .buttons #message-archive')
			btnArchive.click()
			waitFor { $("div.flash.message").text().contains("archived") }
			goToArchivePage()
			waitFor { $("a", text:"hi Bob").displayed}
		then:
	        $("a", text:"hi Bob").displayed
		when:
	        $("a", text:"hi Bob").click()
			!$("#message-archive").displayed()
		then:
			$("#global-nav a", text:"Archive").hasClass("selected")
	}

	def 'archived messages do not show up in sent view'() {
		setup:
			new Fmessage(src:'src', status: MessageStatus.SENT,dst:'+254112233', text:'hi Mary').save(flush: true)
		when:
		    goToArchivePage()
			$("#sent").click()
			waitFor { $("#no-messages").text() == "No messages"}
		then:
			$("#no-messages").text() == 'No messages'
		when:
			go "message/sent"
			def btnArchive = $('#message-details .buttons #message-archive')
			btnArchive.click()
			waitFor { $("div.flash.message").text().contains("archived") }
			goToArchivePage()
			$("#sent").click()
			waitFor { $("table", id:'messages').displayed}
		then:
	        $("a", text:"hi Mary").displayed
		when:
			$("a", text:"hi Mary").click()
			!$("#message-archive").displayed()
		then:
			$("#global-nav a", text:"Archive").hasClass("selected")
	}

	def 'archive button appears in message show view and works'() {
		given:
			def bob = Fmessage.findBySrc("Bob")
		when:
			go "message/inbox/show/${bob.id}"
			def btnArchive = $('#message-details .buttons #message-archive')
		then:
			btnArchive
		when:
			btnArchive.click()
			waitFor { $("div.flash.message").text().contains("archived") }
		then:
			at MessagesPage
		when:
			bob.refresh()
		then:
			bob.archived
	}

	 def 'should not be able to archive activity messages'() {
		when:
			go "message/poll/${Poll.findByTitle('Miauow Mix').id}/show/${Fmessage.findBySrc('Barnabus').id}"
		then:
			!$("#message-details a", text:"Archive").displayed
		when:
			go "message/poll/${Poll.findByTitle('Miauow Mix').id}/show/${Fmessage.findBySrc('Barnabus').id}"
			$("#message")[0].click()
			$("#message")[1].click()
			sleep 1000
		 then:
			!$('#multiple-messages a', text: "Archive All").displayed
	 }

	static createTestData() {
		[new Fmessage(src:'Bob', dst:'+254987654', text:'hi Bob'),
				new Fmessage(src:'Alice', dst:'+2541234567', text:'hi Alice'),
				new Fmessage(src:'+254778899', dst:'+254112233', text:'test')].each() {
					it.status = MessageStatus.INBOUND
					it.save(failOnError:true)
				}
		[new Fmessage(src:'Mary', dst:'+254112233', text:'hi Mary'),
				new Fmessage(src:'+254445566', dst:'+254112233', text:'test')].each() {
					it.save(failOnError:true)
				}
		
		def chickenMessage = new Fmessage(src:'Barnabus', dst:'+12345678', text:'i like chicken', status:MessageStatus.INBOUND)
		def liverMessage = new Fmessage(src:'Minime', dst:'+12345678', text:'i like liver')
		def chickenResponse = new PollResponse(value:'chicken')
		def liverResponse = new PollResponse(value:'liver')
		liverResponse.addToMessages(liverMessage)
		chickenResponse.addToMessages(chickenMessage)
		def poll = new Poll(title:'Miauow Mix')
		poll.addToResponses(chickenResponse)
		poll.addToResponses(liverResponse).save(failOnError:true, flush:true)

		def message1 = new Fmessage(src:'Cheney', dst:'+12345678', text:'i hate chicken')
		def message2 = new Fmessage(src:'Bush', dst:'+12345678', text:'i hate liver')
		def fools = new Folder(name:'Fools').save(failOnError:true, flush:true)
		fools.addToMessages(message1)
		fools.addToMessages(message2)
		fools.save(failOnError:true, flush:true)
	}

	private def goToArchivePage() {
		go ""
		$("a", class:"tab",text: "Archive").click()
		waitFor { $("a", text: 'Inbox Archive').displayed}
	}
}
