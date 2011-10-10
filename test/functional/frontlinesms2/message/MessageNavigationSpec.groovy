package frontlinesms2.message

import frontlinesms2.*
import org.openqa.selenium.Keys

class MessageNavigationSpec extends MessageBaseSpec {
	def "should move to the next message when 'down' arrow is pressed"() {
		given:
			createInboxTestMessages()
		when:
			to PageMessageInbox
		then:
			messagesSelect[1].parent().parent().hasClass("selected")
		when:
			$("#message-list") << Keys.chord(Keys.ARROW_DOWN)
		then:
			waitFor { messagesSelect[2].parent().parent().hasClass("selected") }
			!messagesSelect[1].parent().parent().hasClass("selected")
	}
	
	def "should move to the previous message when 'up' arrow is pressed"() {
		given:
			createInboxTestMessages()
		when:
			to PageMessageInbox
			messagesSelect[2].click()
		then:
			waitFor { messagesSelect[2].parent().parent().hasClass("selected") }
		when:
			$("#message-list") << Keys.chord(Keys.ARROW_UP)
		then:
			waitFor { !messagesSelect[2].parent().parent().hasClass("selected") }
			messagesSelect[1].parent().parent().hasClass("selected")
	}

}
