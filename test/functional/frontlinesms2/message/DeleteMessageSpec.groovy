package frontlinesms2.message

import frontlinesms2.*

class DeleteMessageSpec extends grails.plugin.geb.GebSpec {
	def setup() {
		createTestData()
		assert Fmessage.inbox().count() == 3
		assert Poll.findByTitle('Miauow Mix').getPollMessages().count() == 2
		assert Folder.findByName('Fools').getFolderMessages().count() == 2	
	}

	def 'delete button does not show up for messages in shown in trash view'() {
		when:
			def bobMessage = Fmessage.findBySrc('Bob')
			bobMessage.deleted = true
			bobMessage.save(flush:true)
			go "message/trash"
		then:
			Fmessage.deleted(false).count() == 1
			$('#message-details #contact-name').text() == bobMessage.displayName
			!$('#message-details .buttons #message-delete')
	}
	
	def 'empty trash on confirmation deletes all trashed messages permanently and redirects to inbox'() {
		given:
			new Fmessage(deleted:true).save(flush:true)
			go "message/trash"
			assert Fmessage.findAllByDeleted(true).size == 1
		when:
			$("#trash-actions").jquery.val("empty-trash")
			$('#trash-actions').jquery.trigger('change')
		then:
			waitFor { $("#done").displayed }
		when:
			$("#done").click()
		then:
			waitFor { at MessagesPage }
			Fmessage.findAllByDeleted(true).size == 0
	}
	
	def "'Delete All' button appears for multiple selected messages and works"() {
		when:
			to MessagesPage
			messagesSelect[1].click()
			messagesSelect[2].click()
		then:
			waitFor { multipleMessagesThing.displayed }
			deleteAllButton.displayed
		when:
			deleteAllButton.click()
		then:
			waitFor{ flashMessage.text().contains("deleted") }
	}
	
	def "'Delete' button appears for individual messages and works"() {
		when:
			go "message/inbox"
			def btnDelete = $("#message-delete")
		then:
			btnDelete
		when:
			btnDelete.click()
		then:
			at MessagesPage
			waitFor{$("div.flash").text().contains("deleted")}
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
}



