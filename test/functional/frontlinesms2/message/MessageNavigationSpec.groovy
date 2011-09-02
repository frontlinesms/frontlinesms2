package frontlinesms2.message

import frontlinesms2.*
import org.openqa.selenium.Keys

class MessageNavigationSpec extends MessageGebSpec {
	def "should move to the next message when 'down' arrow is pressed"() {
		given:
			createInboxTestMessages()
		when:
			go "message/inbox/show/${Fmessage.list()[0].id}"
		then:
			$("tr#message-${Fmessage.list()[0].id}").hasClass('selected')
		when:
			$("#message-list") << Keys.chord(Keys.ARROW_DOWN)
			sleep 1000
		then:
			$("tr#message-${Fmessage.list()[1].id}").hasClass('selected')
			!$("tr#message-${Fmessage.list()[0].id}").hasClass('selected')
	}
	
	def "should move to the previous message when 'up' arrow is pressed"() {
		given:
			createInboxTestMessages()
		when:
			go "message/inbox/show/${Fmessage.list()[1].id}"
		then:
			$("tr#message-${Fmessage.list()[1].id}").hasClass('selected')
		when:
			$("#message-list") << Keys.chord(Keys.ARROW_UP)
			sleep 1000
		then:
			!$("tr#message-${Fmessage.list()[1].id}").hasClass('selected')
			$("tr#message-${Fmessage.list()[0].id}").hasClass('selected')
	}

}
