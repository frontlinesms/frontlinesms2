package frontlinesms2.message

import frontlinesms2.*
import frontlinesms2.archive.PageArchive

class MessageArchiveSpec extends MessageBaseSpec {
	def setup() {
		createTestData()
	}

	def 'archived messages do not show up in inbox view'() {
		when:
			to PageArchive
			waitFor { $("#no-messages").text() == "No messages"}
		then:
			$("#no-messages").text() == 'No messages'
		when:
			to PageMessageInbox
			$("a", text:"hi Bob").click()
			waitFor { $("a", text:"hi Bob").parent().parent().hasClass("selected")}
			archiveBtn.click()
			waitFor { $("div.flash.message").text().contains("archived") }
			to PageArchive
		then:
	        $("a", text:"hi Bob").displayed
		when:
	        $("a", text:"hi Bob").click()
		then:
			!$("#message-archive").displayed()
	}

	def 'archived messages do not show up in sent view'() {
		setup:
			new Fmessage(src:'src', status: MessageStatus.SENT,dst:'+254112233', text:'hi Mary').save(flush: true)
		when:
		    to PageArchive
			$("#sent").click()
			waitFor { $("#no-messages").text() == "No messages"}
		then:
			$("#no-messages").text() == 'No messages'
		when:
			to PageMessageSent
			$("a", text:"hi Mary").click()
			waitFor { $("a", text:"hi Mary").parent().parent().hasClass("selected")}
			archiveBtn.click()
			waitFor { $("div.flash.message").text().contains("archived") }
			to PageArchive
			$("#sent").click()
		then:
	        waitFor {$("a", text:"hi Mary").displayed}
		when:
			$("a", text:"hi Mary").click()
		then:
			!$("#message-archive").displayed()
	}


	def 'archive button appears in message show view and works'() {
		when:
			to PageMessageInbox
			archiveBtn.click()
		then:
			waitFor {flashMessage.displayed}
	}

	 def 'should not be able to archive activity messages'() {
		when:
			go "message/poll/${Poll.findByTitle('Miauow Mix').id}/show/${Fmessage.findBySrc('Barnabus').id}"
		then:
			!$("#message-details a", text:"Archive").displayed
		when:
			go "message/poll/${Poll.findByTitle('Miauow Mix').id}/show/${Fmessage.findBySrc('Barnabus').id}"
			$("#message")[0].click()
			$("#message")[1].click()
			sleep 1000
		 then:
			!$('#multiple-messages a', text: "Archive All").displayed
	 }

}
