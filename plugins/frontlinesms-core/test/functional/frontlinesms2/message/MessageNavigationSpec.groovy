package frontlinesms2.message

import frontlinesms2.*
import org.openqa.selenium.Keys

class MessageNavigationSpec extends MessageBaseSpec {
	def "should move to the next message when 'down' arrow is pressed and message should be loaded in the Single Message Details"() {
		given:
			createInboxTestMessages()
		when:
			to PageMessageInbox
			messageList.toggleSelect(0)
		then:
			messageList.hasClass(0, "selected")
		when:
			messageList << Keys.chord(Keys.ARROW_DOWN)
		then:
			waitFor { messageList.hasClass(1, "selected") }
			waitFor { messageList.messages[1].text == singleMessageDetails.text }
			!messageList.hasClass(0, "selected")
	}
	
	def "should move to the previous message when 'up' arrow is pressed and message should be loaded in the Single Message Details"() {
		given:
			createInboxTestMessages()
		when:
			to PageMessageInbox
			messageList.toggleSelect(1)
		then:
			messageList.hasClass(1, "selected")
		when:
			messageList << Keys.chord(Keys.ARROW_UP)
		then:
			waitFor { messageList.hasClass(0, "selected") }
			waitFor { messageList.messages[0].text == singleMessageDetails.text }
			!messageList.hasClass(1, "selected")
	}

	def "first message row should be highlighted when up key is pressed"() {
		given:
			createInboxTestMessages()
		when:
			to PageMessageInbox
			messageList << Keys.chord(Keys.ARROW_UP)
		then:
			messageList.hasClass(0, "selected")
	}

	def "first message row should be highlighted when down key is pressed"() {
		given:
			createInboxTestMessages()
		when:
			to PageMessageInbox
			messageList << Keys.chord(Keys.ARROW_DOWN)
		then:
			messageList.hasClass(0, "selected")
	}

	def "first message row should be highlighted when up key is pressed in Trash Page"() {
		given:
			def trashService = new TrashService()
			createInboxTestMessages()
			def announcement= Announcement.build(name:'test-announcement')
			Fmessage.getAll().each { trashService.sendToTrash(it) }
			trashService.sendToTrash(announcement)
		when:
			to PageMessageTrash
			messageList << Keys.chord(Keys.ARROW_UP)
		then:
			messageList.hasClass(0, "selected")
	}

	def "single message shoould not be updated when navigating in the Trash Page"() {
		given:
			def trashService = new TrashService()
			createInboxTestMessages()
			def announcement= Announcement.build(name:'test-announcement')
			Fmessage.getAll().each { trashService.sendToTrash(it) }
			trashService.sendToTrash(announcement)
		when:
			to PageMessageTrash
			messageList << Keys.chord(Keys.ARROW_DOWN)
			def currrentText = singleMessageDetails.text
		then:
			waitFor { messageList.messages[0].text != currrentText }
			waitFor { singleMessageDetails.text == currrentText }
		when:
			messageList << Keys.chord(Keys.ARROW_DOWN)
		then:
			 waitFor { messageList.messages[0].text != currrentText }
			 waitFor { singleMessageDetails.text == currrentText }
	}
}
