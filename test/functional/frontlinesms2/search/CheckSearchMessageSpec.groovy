package frontlinesms2.search

import frontlinesms2.*

class CheckSearchMessageSpec extends SearchGebSpec {
	
	def "header checkbox is checked when all the messages are checked"() {
		given:
			createInboxTestMessages()
		when:
			to SearchPage
			$("#message")[1].click()
			$("#message")[2].click()
			$("#message")[3].click()
		then:
			$("#message")[0].@checked == "true"
	}
	
	def "message count displayed when multiple messages are selected"() {
		given:
			createInboxTestMessages()
		when:
			to SearchPage
			$("#message")[1].click()
			$("#message")[2].click()
			sleep 1000
		then:
			$("#checked-message-count").text() == "2 messages selected"
	}
	
	def "checked message details are displayed when message is checked"() {
		given:
			createInboxTestMessages()
		when:
			to SearchPage
			$("#message")[2].click()
			sleep 1000
		then:
			$("#message-details #contact-name").text() == $(".displayName-${Fmessage.findBySrc('Bob').id}").text()
		
		when:
			$("#message")[1].click()
			sleep 1000
		then:
			$("tr#message-${Fmessage.list()[0].id}").hasClass('selected')
			$("tr#message-${Fmessage.list()[1].id}").hasClass('selected')
	}
	
	def "'Reply All' button appears for multiple selected messages and works"() {
		given:
			createInboxTestMessages()
			new Contact(name: 'Alice', primaryMobile: 'Alice').save(failOnError:true)
			new Contact(name: 'June', primaryMobile: '+254778899').save(failOnError:true)
		when:
			to SearchPage
			$("#message")[1].click()
			$("#message")[2].click()
			sleep 1000
			def btnReply = $('#multiple-messages a')[0]
		then:
			btnReply
		when:
			btnReply.click()
			sleep 1000
			$("div#tabs-1 .next").click()
		then:
			$('input', value:'Alice').getAttribute('checked')
			$('input', value:'Bob').getAttribute('checked')
			!$('input', value:'June').getAttribute('checked')
	}
	
	def "'Forward' button still work when all messages are unchecked"() {
		given:
			createInboxTestMessages()
			def message = Fmessage.findBySrc('Alice')
		when:
			to SearchPage
			$("#message")[0].click()
		then:
			$("#message")*.@checked == ["true", "true", "true", "true"]
		when:
			$("#message")[0].click()
			sleep 1000
		then:
			$("#message")*.@checked == ["", "", "", ""]
		when:
			$('#btn_dropdown').click()
			sleep 2000
			$('#btn_forward').click()
			sleep 2000
		then:
			$('textArea', name:'messageText').text() == "hi Alice"
	}
	
	def "should set row as selected when a message is checked"() {
		given:
			createInboxTestMessages()
			def message = Fmessage.findBySrc('Bob')
		when:
			to SearchPage
			$("#message")[2].click()
		then:
			$("#message")[2].@checked == "true"
			$("#message")[2].parent().parent().hasClass("selected")
	}


	def "select all should update the total message count when messages are checked"() {
		given:
			createInboxTestMessages()
			new Fmessage(src: "src", dst: "dst", status: MessageStatus.INBOUND).save(flush: true)
		when:
			to SearchPage
			$("#message")[0].click()
			sleep(1000)
			waitFor { $('#multiple-messages').displayed}
		then:
			$("#checked-message-count").text() == "3 messages selected"
		when:
			$('#message')[1].click()
			sleep(1000)
			waitFor { $("#checked-message-count").text().contains("2") }
		then:
			$("#checked-message-count").text() == "2 messages selected"
		when:
			$('#message')[0].click()
			sleep(1000)
			waitFor { $("#checked-message-count").text().contains("3") }
		then:
			$("#checked-message-count").text() == "3 messages selected"
	}
}
