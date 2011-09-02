package frontlinesms2.message

import frontlinesms2.*

class DeleteMessageSpec extends grails.plugin.geb.GebSpec {
	def setup() {
		createTestData()
		assert Fmessage.getInboxMessages(['starred':false, 'archived': false]).size() == 3
		assert Poll.findByTitle('Miauow Mix').getMessages(['starred':false]).size() == 2
		assert Folder.findByName('Fools').messages.size() == 2	
	}

	def 'delete button does not show up for messages in shown in trash view'() {
		when:
			def bobMessage = Fmessage.findBySrc('Bob')
			bobMessage.deleted = true
			bobMessage.save(flush:true)
			go "message/trash"
		then:
			Fmessage.getDeletedMessages(['starred':false]).size() == 1
			$('#message-details #contact-name').text() == bobMessage.displayName
			!$('#message-details .buttons #message-delete')
	}
	
	def 'empty trash on confirmation deletes all trashed messages permanently and redirects to inbox'() {
		given:
			new Fmessage(deleted:true).save(flush:true)
			go "message/trash"
			assert Fmessage.findAllByDeleted(true).size == 1
		when:
			def trashAction = $("#empty-trash")
			trashAction.click()
			sleep 1000
			$("#done").click()
			sleep 1000
		then:
			at MessagesPage
			Fmessage.findAllByDeleted(true).size == 0
	}
	
	def "'Delete All' button appears for multiple selected messages and works"() {
		when:
			go "message/inbox"
			$("#message")[1].click()
			$("#message")[2].click()
			sleep(1000)
			waitFor {$('#multiple-messages').displayed}
			def btnDelete = $("#btn_delete_all")
		then:
			btnDelete
		when:
			btnDelete.click()
		then:
			at MessagesPage
			waitFor{$("div.flash").text().contains("deleted")}
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
		new Poll(title:'Miauow Mix', responses:[chickenResponse, liverResponse]).save(failOnError:true, flush:true)

		def message1 = new Fmessage(src:'Cheney', dst:'+12345678', text:'i hate chicken')
		def message2 = new Fmessage(src:'Bush', dst:'+12345678', text:'i hate liver')
		def fools = new Folder(name:'Fools').save(failOnError:true, flush:true)
		fools.addToMessages(message1)
		fools.addToMessages(message2)
		fools.save(failOnError:true, flush:true)
	}
}



