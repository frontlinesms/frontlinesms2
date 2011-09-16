package frontlinesms2.message

import frontlinesms2.*
import org.openqa.selenium.Keys

class MessageNavigationSpec extends MessageGebSpec {
	def "should move to the next message when 'down' arrow is pressed"() {
		given:
			createInboxTestMessages()
		when:
			go "message/inbox/"
		then:
			$("#message")[1].parent().parent().hasClass("selected")
		when:
			$("#message-list") << Keys.chord(Keys.ARROW_DOWN)
			sleep 1000
		then:
			$("#message")[2].parent().parent().hasClass("selected")
			!$("#message")[1].parent().parent().hasClass("selected")
	}
	
	def "should move to the previous message when 'up' arrow is pressed"() {
		given:
			createInboxTestMessages()
		when:
			go "message/inbox/"
			$("#message")[2].click()
			sleep 1000
		then:
			$("#message")[2].parent().parent().hasClass("selected")
		when:
			$("#message-list") << Keys.chord(Keys.ARROW_UP)
			sleep 1000
		then:
			!$("#message")[2].parent().parent().hasClass("selected")
			$("#message")[1].parent().parent().hasClass("selected")
	}

}
