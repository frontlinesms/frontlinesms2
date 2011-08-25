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
		    $("a", text:"Quick message").click()
		    waitFor {$('div#tabs-1').displayed}
		then:
	        $('div#tabs-1').displayed
	}

    def "should select the next tab on click of next"() {
		when:
			to MessagesPage
		    loadFirstTab()
			loadSecondTab()
			$("#address").value("+919544426000")
			$('.add-address').click()
        then:
			$('div#tabs-2').displayed
		when:
			loadThirdTab()
		then:
			$('div#tabs-3').displayed
	}

    def "should not send message when no recipients are selected"() {
		when:
			to MessagesPage
		    loadFirstTab()
        then:
			$("#tabs li")[2].click()
		when:
			$("#done").click()
			sleep(1000)
		then:
			$("#tabs-3").displayed
		when:
			$("#tabs li")[3].click()
			sleep(1000)
		then:
			$("#tabs-3").displayed
	}

    def "should select the previous tab on click of back"() {
		when:
			to MessagesPage
			loadFirstTab()
			loadSecondTab()
			$("#address").value("+919544426000")
			$('.add-address').click()
			loadThirdTab()
		then:
			$('div#tabs-3').displayed
        when:
			$("#prevPage").click()
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
			$('.add-address').jquery.trigger('click')
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
			$("#done").click()
			waitFor{$("#tabs-4").displayed }
			$("#confirmation").click()
		then:
			$("a", text: "Inbox").click()
			waitFor{title == "Inbox"}
			$("a", text: "Pending").hasClass("send-failed")
			$("a", text: "Pending").click()
			waitFor{title == "Pending"}
			$("#message-list tbody tr").size() == 1
			$("#message-list tbody tr")[0].hasClass("send-failed")
	}


	def "should select members belonging to the selected group"() {
		setup:
			createData()
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
			createData()
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

	def "should not allow to proceed if the recipients are not selected in the quick message screen"() {
		setup:
			createData()
		when:
			to MessagesPage
			loadFirstTab()
			loadSecondTab()
			assert !$(".error-panel").displayed
			$("#nextPage").click()
		then:
			$(".error-panel").displayed
	}

	def "selected group should get unchecked when a member drops off"() {
		setup:
			createData()
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

	def "should not deselect common members across groups when one of the group is unchecked"() {
		setup:
			createData()
		when:
			to MessagesPage
			loadFirstTab()
			loadSecondTab()
			$("input[value='group1']").click()
			$("input[value='group2']").click()
		then:
			$("#recipient-count").text() == "2"
		when:
			$("input[value='group1']").click()
		then:
			!$("input[value='group1']").getAttribute("checked")
			$("input[value='group2']").getAttribute("checked")
			$("#recipient-count").text() == "2"

	}

	def "should launch announcement screen from create new activity link" () {
		when:
			to MessagesPage
			$("a", text:"Create new activity").click()
			waitFor {$("#tabs-1").displayed}
			$("input", name: "activity").value("announcement")
			$("#done").click()
			waitFor {$("#ui-dialog-title-modalBox").text() == "Announcement"}
		then:
			$("#ui-dialog-title-modalBox").text() == "Announcement"
	}

	private def createData() {
		def group = new Group(name: "group1").save(flush: true)
		def group2 = new Group(name: "group2").save(flush: true)
		def alice = new Contact(name: "alice", primaryMobile: "12345678").save(flush: true)
		def bob = new Contact(name: "bob", primaryMobile: "567812445").save(flush: true)
		group.addToMembers(alice)
		group2.addToMembers(alice)
		group.addToMembers(bob)
		group2.addToMembers(bob)
		group.save(flush: true)
		group2.save(flush: true)
	}

	def loadFirstTab() {
		$("a", text:"Quick message").click()
		waitFor {$('div#tabs-1').displayed}
	}
	def loadSecondTab() {
		$("#nextPage").click()
		waitFor {$('div#tabs-2').displayed}		
	}
	def loadThirdTab() {
		$("#nextPage").click()
		waitFor {$('div#tabs-3').displayed}
	}
	def loadQuickMessageDialog() {
		$("a", text:"Quick message").click()
		waitFor {$('div#tabs-1').displayed}
	}
}

class SentMessagesPage extends geb.Page {
	static url = 'message/sent'
}



