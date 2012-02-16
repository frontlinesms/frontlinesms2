package frontlinesms2.message

import frontlinesms2.*

class MessageDeleteSpec extends grails.plugin.geb.GebSpec {
	
	def setup() {
		createTestData()
	}

	def 'delete button does not show up for messages in trash view'() {
		when:
			def bobMessage = Fmessage.findBySrc('Bob')
			bobMessage.save(flush:true)
			deleteMessage(bobMessage)
			go "message/trash"
			$("a", text: "Bob").click()
		then:
			Fmessage.deleted(false).count() == 1
			$('#message-detail #message-detail-sender').text() == bobMessage.displayName
			!$('#message-detail .buttons #delete-msg')
	}

	def 'empty trash on confirmation deletes all trashed messages permanently and redirects to inbox'() {
		given:
			def message = new Fmessage(text:"to delete").save(failOnError:true)
			deleteMessage(message)
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
			waitFor { at PageMessageInbox }
			Fmessage.findAllByDeleted(true).size == 0
	}
	
	def "'Delete All' button appears for multiple selected messages and works"() {
		when:
			to PageMessageInbox
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
			go "message/inbox/show/${Fmessage.findBySrc('Bob').id}"
			def btnDelete = $("#delete-msg")
		then:
			btnDelete
		when:
			btnDelete.click()
		then:
			at PageMessageInbox
			waitFor{$("div.flash").text().contains("deleted")}
	}

	def deleteMessage(Fmessage message) {
		message.isDeleted = true
		message.save(flush:true)
		new Trash(identifier:message.contactName, message:message.text, objectType:message.class.name, linkId:message.id).save(failOnError: true, flush: true)
	}
	
	static createTestData() {
		[new Fmessage(src:'Bob', text:'hi Bob'),
				new Fmessage(src:'Alice', text:'hi Alice'),
				new Fmessage(src:'+254778899', text:'test')].each() {
					it.inbound = true
					it.save(flush:true, failOnError:true)
				}
		[new Fmessage(src:'Mary', text:'hi Mary'),
				new Fmessage(src:'+254445566', text:'test')].each() {
				    it.inbound = true
					it.save(flush:true, failOnError:true)
				}
	}
}



