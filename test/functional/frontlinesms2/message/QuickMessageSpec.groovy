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
	        $('div#tabs-1').displayed
	}

    def "should select the next tab on click of next"() {
		when:
			to MessagesPage
		    loadFirstTab()
			loadSecondTab()
        then:
			$('div#tabs-2').displayed
		when:
			loadThirdTab()
		then:
			$('div#tabs-3').displayed
	}

    def "should select the previous tab on click of back"() {
		when:
			to MessagesPage
			loadFirstTab()
			loadSecondTab()
			loadThirdTab()
		then:
			$('div#tabs-3').displayed
        when:
			$("div#tabs-3 .back").click()
			waitFor {$('div#tabs-2').displayed}
		then:
			$('div#tabs-2').displayed

	}

	def "should add the manually entered contacts to the list "() {
		when:
			to MessagesPage
			loadFirstTab()
			loadSecondTab()
			$("#address").value("+919544426000")
			$('.add-address').click()
		then:
			$('div#contacts div')[0].find('input').value() == "+919544426000"
	}

	def "should send the message to the selected recipients"() {
		when:
			to MessagesPage
			loadFirstTab()
			loadSecondTab()
			$("#address").value("+919544426000")
			$('.add-address').click()
			loadThirdTab()
        	$("#sendMsg").click()
		then:
            at SentMessagesPage
	}

	def loadFirstTab() {
		$("a.quick_message").click()
		waitFor {$('div#tabs-1').displayed}
	}
	def loadSecondTab() {
		$("div#tabs-1 .next").click()
		waitFor {$('div#tabs-2').displayed}		
	}
	def loadThirdTab() {
		$("div#tabs-2 .next").click()
		waitFor {$('div#tabs-3').displayed}		
	}
}

class SentMessagesPage extends geb.Page {
	static url = 'message/sent'
}



