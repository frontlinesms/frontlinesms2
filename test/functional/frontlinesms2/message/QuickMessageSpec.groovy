package frontlinesms2.message

import frontlinesms2.*
import java.util.regex.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date

class QuickMessageSpec extends grails.plugin.geb.GebSpec {
	def "quick message link opens the popup to send messages"() {
		when:
			to MessagesPage
		    $("a.quick_message").click()
		    waitFor {$('div#message-text').displayed}
		then:
	        //TODO: The assertion is a placeholder. It will be replaced
	        $('div#message-text').displayed
	}
}


