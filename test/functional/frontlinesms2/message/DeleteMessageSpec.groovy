package frontlinesms2.message

import frontlinesms2.*
import frontlinesms2.enums.MessageStatus

class DeleteMessageSpec extends grails.plugin.geb.GebSpec {
	def setup() {
		createTestData()
		assert Fmessage.getInboxMessages(['starred':false, 'archived': false]).size() == 3
		assert Poll.findByTitle('Miauow Mix').getMessages(['starred':false]).size() == 2
		assert Folder.findByName('Fools').messages.size() == 2	
	}
	
	def 'deleted messages do not show up in inbox view'() {
		when:
			go "message/inbox/show/${Fmessage.findBySrc('Bob').id}"
			def btnDelete = $('#message-details .buttons #message-delete')
			btnDelete.click()
			waitFor { $("div.flash.message").text().contains("deleted") }
		then:
			Fmessage.getInboxMessages(['starred':false, 'archived': false]).size() == 2
	}

	def 'deleted messages do show up in trash view'() {
		when:
			def bobMessage = Fmessage.findBySrc('Bob')
			go "message/inbox/show/${bobMessage.id}"
			def btnDelete = $('#message-details .buttons #message-delete')
			btnDelete.click()
			waitFor { $("div.flash").text().contains("deleted") }
			go "message/trash"
		then:
			Fmessage.getDeletedMessages(['starred':false]).size() == 1
			$('#message-details .message-name').text() == bobMessage.displayName
	}

	def 'delete button does not show up for messages in shown in trash view'() {
		when:
			def bobMessage = Fmessage.findBySrc('Bob')
			bobMessage.deleted = true
			bobMessage.save(flush:true)
			go "message/trash"
		then:
			Fmessage.getDeletedMessages(['starred':false]).size() == 1
			$('#message-details .message-name').text() == bobMessage.displayName
			!$('#message-details .buttons #message-delete')
	}
	
	def 'deleted messages do not show up in poll view'() {
		when:
			go "message/poll/${Poll.findByTitle('Miauow Mix').id}/show/${Fmessage.findBySrc('Barnabus').id}"
			def btnDeleteFromPoll = $('#message-details .buttons #message-delete')
			btnDeleteFromPoll.click()
			waitFor { $("div.flash.message").displayed }
		then:
			Poll.findByTitle('Miauow Mix').getMessages(['starred':false]).size() == 1
	}
	
	def 'deleted messages do not show up in folder view'() {
		given:
			assert Folder.findByName('Fools').messages.size() == 2
		when:
			go "message/folder/${Folder.findByName('Fools').id}/show/${Fmessage.findBySrc('Cheney').id}"
			def btnDeleteFromFolder = $('#message-details .buttons #message-delete')
			btnDeleteFromFolder.click()
			waitFor { $("div.flash.message").text().contains("deleted") }
		then:
			Folder.findByName('Fools').getFolderMessages(['starred':false]).size() == 1
	}

	def 'empty trash on confirmation deletes all trashed messages permanently and redirects to inbox'() {
		given:
			new Fmessage(deleted:true).save(flush:true)
			go "message/trash"
			assert Fmessage.findAllByDeleted(true).size == 1
		when:
			def trashAction = $("select", id:"empty-trash")
			trashAction.getJquery().val('Empty trash')
			trashAction.jquery.trigger('change')
			waitFor {$('.ui-button')}
			$('.ui-button')[0].click()
		then:
			at MessagesPage
			Fmessage.findAllByDeleted(true).size == 0
	}
	
	def 'delete button appears in message show view and works'() {
		given:
			def bob = Fmessage.findBySrc("Bob")
		when:
			go "message/inbox/show/${bob.id}"
			def btnDelete = $('#message-details .buttons #message-delete')
		then:
			btnDelete
		when:
			btnDelete.click()
			waitFor { $("div.flash.message").text().contains("deleted") }
		then:
			at MessagesPage
		when:
			bob.refresh()
		then:
			bob.deleted
	}
	
	def "'Delete All' button appears for multiple selected messages and works"() {
		given:
			def aliceMessage = Fmessage.findBySrc('Alice')
			def message3 = Fmessage.findBySrc('+254778899')
		when:
			go "message/inbox"
			$("#message")[1].click()
			$("#message")[2].click()
			waitFor {$('.multi-action').displayed}
			def btnDelete = $('.multi-action a')[2]
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



