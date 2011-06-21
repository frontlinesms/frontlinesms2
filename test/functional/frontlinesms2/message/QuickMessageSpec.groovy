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
		    waitFor {$('div#tabs-1').displayed}
		then:
	        //TODO: The assertion is a placeholder. It will be replaced
	        $('div#tabs-1').displayed
	}

    def "should select the next tab on click of next"() {
		when:
			to MessagesPage
			$("a.quick_message").click()
			waitFor {$('div#tabs-1').displayed}
			$("div#tabs-1 .next").click()
			waitFor {$('div#tabs-2').displayed}
        then:
			$('div#tabs-2').displayed
		when:
			$("div#tabs-2 .next").click()
			waitFor {$('div#tabs-3').displayed}
		then:
			$('div#tabs-3').displayed
	}

	def "should add the manually entered contacts to the list "() {
		when:
			to MessagesPage
			$("a.quick_message").click()
			waitFor {$('div#tabs-1').displayed}
			$("div#tabs-1 .next").click()
			waitFor {$('div#tabs-2').displayed}
			$("#address").value("+919544426000")
			$('.add-address').click()
		then:
			$('div#contacts div')[0].find('input').value() == "+919544426000"
	}
}


