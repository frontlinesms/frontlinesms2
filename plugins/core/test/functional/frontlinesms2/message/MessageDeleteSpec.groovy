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
			def message = Fmessage.build()
			deleteMessage(message)
			go "message/trash"
			assert Fmessage.findAllByIsDeleted(true).size == 1
		when:
			$("#trash-actions").value("empty-trash")
		then:
			waitFor { $("#ui-dialog-title-modalBox").displayed }
		when:
			$("#title").value("Empty trash")
			$("#done").click()
		then:
			waitFor { at PageMessageInbox }
			Fmessage.findAllByIsDeleted(true).size == 0
	}
	
	def "'Delete All' button appears for multiple selected messages and works"() {
		when:
			to PageMessageInbox
			messagesSelect[1].click()
			messagesSelect[2].click()
			waitFor { multipleMessagesThing.displayed }
			$("#btn_delete_all").jquery.trigger('click')
		then:
			waitFor{ $(".flash.message").text().contains("trash") }
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
			waitFor{$("div.flash").text().contains("trash")}
	}

	def deleteMessage(Fmessage message) {
		message.isDeleted = true
		message.save(flush:true)
		Trash.build(displayName:message.displayName, displayDetail:message.text, objectClass:message.class.name, objectId:message.id)
	}
	
	static createTestData() {
		Fmessage.build(src:'Bob', text:'hi Bob')
		Fmessage.build(src:'Alice', text:'hi Alice')
		Fmessage.build(src:'+254778899', text:'test')
		Fmessage.build(src:'Mary', text:'hi Mary')
		Fmessage.build(src:'+254445566', text:'test')
	}
}



