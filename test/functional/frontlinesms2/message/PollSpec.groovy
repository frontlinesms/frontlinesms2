package frontlinesms2.message

import frontlinesms2.*
import java.util.regex.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date

class PollSpec extends frontlinesms2.poll.PollGebSpec {
	def 'message from alice is first in the list, and links to the show page'() {
		given:
			createTestPolls()
			createTestMessages()
			def message = Fmessage.findBySrc('Alice')
			def poll = Poll.findByTitle('Football Teams')
		when:
			to PollShowPage
			def firstMessageLink = $('#messages tbody tr:nth-child(1) a', href:"/frontlinesms2/message/poll/${poll.id}/show/${message.id}")
		then:
			firstMessageLink.text() == 'Alice'
	}

	def 'selected message and its details are displayed'() {
		given:
			createTestPolls()
			createTestMessages()
			def message = Fmessage.findBySrc('Alice')
			def poll = Poll.findByTitle('Football Teams')
		when:
			to PollShowPage
			def formatedDate = dateToString(message.dateCreated)
		then:
			$('.message-name').text() == message.src
			$('.message-date').text() == formatedDate
			$('.message-body').text() == message.text
	}

	def 'selected message is highlighted'() {
		given:
			createTestPolls()
			createTestMessages()
			def poll = Poll.findByTitle('Football Teams')
			def aliceMessage = Fmessage.findBySrc('Alice')
			def bobMessage = Fmessage.findBySrc('Bob')
		when:
			to PollShowPage
		then:
			$('#messages .selected td:nth-child(3) a').getAttribute('href') == "/frontlinesms2/message/poll/${poll.id}/show/${aliceMessage.id}"
		when:
			go "message//poll/${poll.id}/show/${bobMessage.id}"
		then:
			$('#messages .selected td:nth-child(3) a').getAttribute('href') == "/frontlinesms2/message/poll/${poll.id}/show/${bobMessage.id}"
	}
	
	def 'activities should also list message counts'() {
		given:
			createTestPolls()
			createTestMessages()
		when:
			to PollShowPage
		then:
			$('#activities-submenu li')*.text() ==  ['Football Teams (2)', 'Shampoo Brands (1)', 'Rugby Brands (0)']
	}

	def "should auto populate poll response when a poll with yes or no answer is created"() {
		when:
			go "message"
		then:
			$("a", text: "Poll").click()
			waitFor {$('#tabs-1').displayed}
			$("input", name:'poll-type').value("standard")
			$("input", value:'standard').jquery.trigger('click')
		when:
			$("#tabs-1  a", text:'Next').jquery.trigger('click')
			waitFor { $('#tabs-3 ').displayed }
            $("#tabs-3 a", text:'Next').jquery.trigger('click')
			waitFor { $('#tabs-4 ').displayed }
			$("#tabs-4 a", text:'Next').jquery.trigger('click')
			waitFor { $('#tabs-5').displayed }
			$("input", name:'title').value("POLL NAME")
			$("input", id:'create-poll').click()
			waitFor {!$("div.flash.message").text().isEmpty()}
		then:
			Poll.findByTitle("POLL NAME").responses*.value.containsAll("Yes","No", "Unknown")
	}

	def "should skip recipients tab when do not send message option is chosen"() {
		when:
			go "message"
		then:
			$("a", text: "Poll").click()
			waitFor {$('#tabs-1').displayed}
			$("input", name:'poll-type').value("standard")
			$("input", value:'standard').jquery.trigger('click')
			$("input", name:"collect-responses").value('no-message')
		when:
			$("#tabs-1  a", text:'Next').jquery.trigger('click')
			waitFor { $('#tabs-3 ').displayed }
			$("#tabs-3 a", text:'Next').jquery.trigger('click')
			waitFor { $('#tabs-5 ').displayed }
		then:
			 $('#tabs-5 ').displayed
	}


	def "should move to the next tab when multiple choice poll is selected"() {
		when:
			go "message"
		then:
			$("a", text: "Poll").click()
			waitFor {$('#tabs-1').displayed}
			$("input", name:'poll-type').value("multiple")
			$("input", value:'multiple').jquery.trigger('click')
		when:
			$("a", text:'Next')[0].jquery.trigger('click')
			waitFor { $('#tabs-2').displayed }
		then:
			$('#tabs-2').displayed 
	}

	def "should enter instructions for the poll and validate multiple choices user entered"() {
		when:
			go "message"
			$("a", text: "Poll").click()
		    waitFor {$('#tabs-1').displayed}
			$("input", name:'poll-type').value("multiple")
            $("textarea", name:'question').value("How often do you drink coffee?")
            $("a", text: "Next").click()
        then:
			waitFor {$('#tabs-2').displayed}
			$("label[for='choiceA']").hasClass('bold') == false
			$("label[for='choiceA']").hasClass('bold') == false
		when:
			$("input", name:'instruction').value("Reply A,B etc")
            keyInData('choiceA', "Never")
            keyInData('choiceB',"Once a day")
            keyInData('choiceC', "Twice a day")
            $("a", text: "Next").click()
		then:
			$("label[for='choiceA']").hasClass('bold') == true
			$("label[for='choiceA']").hasClass('bold') == true
			waitFor {$('#tabs-3').displayed}
		when:
            $("textarea", name:'autoReplyText').value("Thanks for participating...")
            $("a", text: "Next").click()
		then:
			waitFor {$('#tabs-4').displayed}
		when:
			$("a", text: "Next").click()
		then:
			waitFor { $('#tabs-5 ').displayed }
            $("input", name:'title').value("Cofee Poll")
            $("#poll-question-text").text() == "How often do you drink coffee? A) Never B) Once a day C) Twice a day Reply A,B etc"
            $("#confirm-recepients-count").text() == "0 contacts selected"
            $("#auto-reply-read-only-text").text() == "Thanks for participating..."
		when:
			$("#create-poll").click()
		then:
			waitFor { $("div.flash.message").text().contains("The poll has been created!") }
	}
	
	def keyInData(String selector, String value) {
		def element = $("input", name:selector) 
		element.value(value)
		element.jquery.trigger('blur')
	}

	String dateToString(Date date) {
		DateFormat formatedDate = createDateFormat();
		return formatedDate.format(date)
	}

	DateFormat createDateFormat() {
		return new SimpleDateFormat("dd-MMM-yyyy hh:mm")
	}
}

class PollShowPage extends geb.Page {
	static getUrl() { "message/poll/${Poll.findByTitle('Football Teams').id}/show/${Fmessage.findBySrc('Alice').id}" }
	static at = {
		title.endsWith('Poll')
	}
}