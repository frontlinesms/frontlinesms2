package frontlinesms2.message

import frontlinesms2.*
import frontlinesms2.popup.*

class MessageDeleteSpec extends grails.plugin.geb.GebSpec {
	def trashService = new TrashService()
	
	def setup() {
		createTestData()
	}

	def 'delete button does not show up for messages in trash view'() {
		when:
			def bobMessage = Fmessage.findBySrc('Bob')
			deleteMessage(bobMessage)
			to PageMessageTrash
			messageList.messages[0].textLink.click()
		then:
			waitFor { singleMessageDetails.displayed }
			Fmessage.deleted(false).count() == 1
			singleMessageDetails.sender == bobMessage.displayName
			!singleMessageDetails.delete.displayed
	}

	def 'empty trash on confirmation deletes all trashed messages permanently and redirects to inbox'() {
		given:
			def message = Fmessage.build()
			deleteMessage(message)
			to PageMessageTrash
			assert Fmessage.findAllByIsDeleted(true).size == 1
		when:
			trashMoreActions.value("empty-trash")
		then:
			waitFor { at DeleteDialog }
		when:
			done.click()
		then:
			waitFor { at PageMessageInbox }
			Fmessage.findAllByIsDeleted(true).size == 0
	}
	
	def "'Delete All' button appears for multiple selected messages and works"() {
		when:
			to PageMessageInbox
			messageList.messages[0].checkbox.click()
			messageList.messages[1].checkbox.click()
		then:
			waitFor { multipleMessageDetails.checkedMessageCount == '2 messages selected' }
			multipleMessageDetails.deleteAll.click()
		then:
			waitFor { notifications.flashMessagesText.contains("trash") }
	}
	
	def "'Delete' button appears for individual messages and works"() {
		when:
			to PageMessageInbox, Fmessage.findBySrc('Bob').id
		then:
			waitFor { singleMessageDetails.displayed }
			waitFor { singleMessageDetails.delete.displayed }
		when:
			singleMessageDetails.delete.click()
		then:
			at PageMessageInbox
			waitFor{ notifications.flashMessagesText.contains("trash") }
	}

	def deleteMessage(Fmessage message) {
		trashService.sendToTrash(message)
	}
	
	static createTestData() {
		Fmessage.build(src:'Bob', text:'hi Bob')
		Fmessage.build(src:'Alice', text:'hi Alice')
		Fmessage.build(src:'+254778899', text:'test')
		Fmessage.build(src:'Mary', text:'hi Mary')
		Fmessage.build(src:'+254445566', text:'test')
	}
}



