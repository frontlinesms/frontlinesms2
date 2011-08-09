package frontlinesms2.message

import frontlinesms2.Fmessage
import frontlinesms2.Folder
import frontlinesms2.Poll
import frontlinesms2.PollResponse
import frontlinesms2.enums.MessageStatus

class ArchiveMessageSpec extends grails.plugin.geb.GebSpec {
	def setup() {
		createTestData()
		assert Fmessage.getInboxMessages(['starred':false, 'archived': false]).size() == 3
		assert Poll.findByTitle('Miauow Mix').getMessages(['starred':false]).size() == 2
		assert Folder.findByName('Fools').messages.size() == 2
	}

	def cleanup() {
		deleteTestData()
	}

	def 'archived messages do not show up in inbox view'() {
		when:
			goToArchivePage()
			waitFor { $("#messages").text() == "No messages"}
		then:
			$("#messages").text() == 'No messages'
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
		then:
			$("#main-tabs a", text:"Archive").hasClass("selected")
	}

	def 'archived messages do not show up in sent view'() {
		setup:
			new Fmessage(src:'src', status: MessageStatus.SENT,dst:'+254112233', text:'hi Mary').save(flush: true)
		when:
		    goToArchivePage()
			$("#sent").click()
			waitFor { $("#messages").text() == "No messages"}
		then:
			$("#messages").text() == 'No messages'
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
		then:
			$("#main-tabs a", text:"Archive").hasClass("selected")
	}

	def "should not display archive all in archive tab"() {
		Fmessage.list().each {
			it.archived = true
			it.save(flush: true)
		}
		when:
		    goToArchivePage()
			$("#message")[0].click()
			waitFor { $("#count").text().contains("3")}
		then:
			$(".multi-action a").size() == 2
			$(".multi-action a").every() {it.text() != "Archive All"}
	}

	def 'archived messages do not show up in poll view'() {
		when:
			go "message/poll/${Poll.findByTitle('Miauow Mix').id}/show/${Fmessage.findBySrc('Barnabus').id}"
			def btnArchiveFromPoll = $('#message-details .buttons #message-archive')
			btnArchiveFromPoll.click()
			waitFor { $("div.flash.message").text().contains("archived") }
		then:
			Poll.findByTitle('Miauow Mix').getMessages(['starred':false]).size() == 1
	}

	def 'archived messages do not show up in folder view'() {
		given:
			println "Message count: ${Folder.findByName('Fools').messages.size() == 2}"
			assert Folder.findByName('Fools').messages.size() == 2
		when:
			go "message/folder/${Folder.findByName('Fools').id}/show/${Fmessage.findBySrc('Cheney').id}"
			def btnArchiveFromFolder = $('#message-details .buttons #message-archive')
			btnArchiveFromFolder.click()
			waitFor { $("div.flash.message").text().contains("archived") }
		then:
			Folder.findByName('Fools').getFolderMessages(['starred':false]).size() == 1
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
		new Poll(title:'Miauow Mix', responses:[chickenResponse, liverResponse]).save(failOnError:true, flush:true)

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

	static deleteTestData() {
		Poll.findAll().each() {
			it.refresh()
			it.delete(failOnError:true, flush:true)
		}

		Folder.findAll().each() {
			it.refresh()
			it.delete(failOnError:true, flush:true)
		}

		Fmessage.findAll().each() {
			it.refresh()
			it.delete(failOnError:true, flush:true)
		}
	}
}
