package frontlinesms2.message

import frontlinesms2.*

class DeleteMessageSpec extends grails.plugin.geb.GebSpec {
	def setup() {
		createTestData()
	}

	def 'delete button does not show up for messages in shown in trash view'() {
		when:
			def bobMessage = Fmessage.findBySrc('Bob')
			bobMessage.toDelete()
			bobMessage.save(flush:true)
			go "message/trash"
			$("a", text: "Bob").click()
		then:
			Fmessage.deleted(false).count() == 1
			$('#message-details #contact-name').text() == bobMessage.displayName
			!$('#message-details .buttons #message-delete')
	}
	
	def 'empty trash on confirmation deletes all trashed messages permanently and redirects to inbox'() {
		given:
			def message = new Fmessage(text:"to delete", deleted:true).save(flush:true)
			message.toDelete()
			go "message/trash"
			assert Fmessage.findAllByDeleted(true).size == 1
		when:
			$("#trash-actions").value("empty-trash")
		then:
			waitFor { $("#ui-dialog-title-modalBox").displayed }
		when:
			$("#title").value("Empty trash")
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
	}
}



