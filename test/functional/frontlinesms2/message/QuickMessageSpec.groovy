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
			$("#recipient-count").text() == "1"
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


	def "should select members belonging to the selected group"() {
		setup:
			def group = new Group(name: "group1").save(flush: true)
			def alice = new Contact(name: "alice", primaryMobile: "12345678").save(flush: true)
			def bob = new Contact(name: "bob", primaryMobile: "567812445").save(flush: true)
			group.addToMembers(alice)
			group.addToMembers(bob)
			group.save(flush: true)
		when:
			to MessagesPage
			loadFirstTab()
			loadSecondTab()
			$("input[name=groups]").value("group1")
			$("input[value=group1]").jquery.trigger("click")
		then:
			$("#recipient-count").text() == "2"
	}

	def "should deselect all member recipients when a group is un checked"() {
		setup:
			def group = new Group(name: "group1").save(flush: true)
			def alice = new Contact(name: "alice", primaryMobile: "12345678").save(flush: true)
			def bob = new Contact(name: "bob", primaryMobile: "567812445").save(flush: true)
			group.addToMembers(alice)
			group.addToMembers(bob)
			group.save(flush: true)
		when:
			to MessagesPage
			loadFirstTab()
			loadSecondTab()
			$("input[name=groups]").value("group1")
			$("input[value=group1]").jquery.trigger("click")
		then:
			$("#recipient-count").text() == "2"
		when:
			$("input[value=group1]").jquery.trigger("click")
		then:
			$("#recipient-count").text() == "0"
	}

	def "selected group should get unchecked when a member drops off"() {
		setup:
			def group = new Group(name: "group1").save(flush: true)
			def group2 = new Group(name: "group2").save(flush: true)
			def alice = new Contact(name: "alice", primaryMobile: "12345678").save(flush: true)
			def bob = new Contact(name: "bob", primaryMobile: "567812445").save(flush: true)
			group.addToMembers(alice)
			group2.addToMembers(alice)
			group.addToMembers(alice)
			group2.addToMembers(bob)
			group.save(flush: true)
			group2.save(flush: true)
		when:
			to MessagesPage
			loadFirstTab()
			loadSecondTab()
			$("input[value='group1']").click()
			$("input[value='group2']").click()
		then:
			$("#recipient-count").text() == "2"
		when:
			$("input[value='12345678']").click()
		then:
			!$("input[value='group1']").getAttribute("checked")
			!$("input[value='group2']").getAttribute("checked")
			$("#recipient-count").text() == "1"
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
	def loadQuickMessageDialog() {
		$("a.quick_message").click()
		waitFor {$('div#tabs-1').displayed}
	}
}

class SentMessagesPage extends geb.Page {
	static url = 'message/sent'
}



