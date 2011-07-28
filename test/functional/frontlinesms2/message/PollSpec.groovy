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
		cleanup:
			deleteTestPolls()
			deleteTestMessages()
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
		cleanup:
			deleteTestPolls()
			deleteTestMessages()
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
		cleanup:
			deleteTestPolls()
			deleteTestMessages()
	}
	
	def 'activities should also list message counts'() {
		given:
			createTestPolls()
			createTestMessages()
		when:
			to PollShowPage
		then:
			$('#activities-submenu li')*.text() ==  ['Football Teams (2)', 'Shampoo Brands (1)', 'Rugby Brands (0)']
		cleanup:
			deleteTestPolls()
			deleteTestMessages()
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
			$("a", text:'Next')[0].jquery.trigger('click')
			waitFor { $('#tabs-3').displayed }
            $("a", text:"Confirm").click()
			waitFor { $('#tabs-4').displayed }
			$("input", name:'title').value("POLL NAME")
			$("input", id:'create-poll').click()
			waitFor {!$("div.flash.message").text().isEmpty()}
		then:
			Poll.findByTitle("POLL NAME").responses*.value.containsAll("yes", "no")
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

