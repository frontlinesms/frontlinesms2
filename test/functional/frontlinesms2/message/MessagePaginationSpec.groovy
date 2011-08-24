package frontlinesms2.message

import frontlinesms2.*
import frontlinesms2.enums.MessageStatus

class MessagePaginationSpec  extends grails.plugin.geb.GebSpec  {

	def "should paginate inbox messages"() {
		setup:
			setupInboxMessages()
		when:
			go "message/inbox"
		then:
			$("#messages tbody tr").size() == 50
		when:
			$("#page-arrows a", text: 'Next').click()
			waitFor {$("a", text:"Back").displayed}
		then:
			$("#messages tbody tr").size() == 1

	}

	def "should display the message details for the message selected by default"() {
		setup:
			setupInboxMessages()
		when:
			go "message/inbox"
			def element = $("#messages tr:nth-child(2) td:nth-child(3) a")
			def expectedText = element.text()
			element.click()
			println $("#contact-name").text()
			println expectedText
			waitFor { $("#contact-name").text().contains(expectedText) }
		then:
			$("#page-arrows a", text: "Next").click()
			waitFor {$("a", text:"Back").displayed}
		then:
			$('#contact-name').text().contains( $(".selected td:nth-child(3) a").text())
	}

	def "should paginate pending messages"() {
		setup:
			setupPendingMessages()
		when:
			go "message/pending"
		then:
			$("#messages tbody tr").size() == 50
		when:
			$("#page-arrows a", text: 'Next').click()
			waitFor {$("a", text:"Back").displayed}
		then:
			$("#messages tbody tr").size() == 1

	}

	def "should paginate deleted messages"() {
		setup:
			setupDeletedMessages()
		when:
			go "message/trash"
		then:
			$("#messages tbody tr").size() == 50
		when:
			$("#page-arrows a", text: 'Next').click()
			waitFor {$("a", text:"Back").displayed}
		then:
			$("#messages tbody tr").size() == 1

	}

	def "should paginate sent messages"() {
		setup:
			setupSentMessages()
		when:
			go "message/sent"
		then:
			$("#messages tbody tr").size() == 50
		when:
			$("a", text: 'Next').click()
			waitFor {$("#page-arrows a", text:"Back").displayed}
		then:
			$("#messages tbody tr").size() == 1

	}

	def "should paginate folder messages"() {
		setup:
			setupFolderAndItsMessages()
			def folderId = Folder.findByName("folder").id
		when:
			go "/frontlinesms2/message/folder/${folderId}"
		then:
			$("#messages tbody tr").size() == 50
		when:
			$("#page-arrows a", text: 'Next').click()
			waitFor {$("a", text:"Back").displayed}
		then:
			$("#messages tbody tr").size() == 1

}

	def "should paginate poll messages"() {
		setup:
			setupPollAndItsMessages()
			def pollId = Poll.findByTitle("poll").id
		when:
			go "message/poll/${pollId}"
		then:
			$("#messages tbody tr").size() == 50
		when:
			$("#page-arrows a", text: 'Next').click()
			waitFor {$("a", text:"Back").displayed}
		then:
			$("#messages tbody tr").size() == 1

	}

	private def setupInboxMessages() {
		(1..51).each { i ->
			new Fmessage(src: "src${i}", dst: "dst${i}", text: "inbox ${i}", dateReceived: new Date() - i, status: MessageStatus.INBOUND).save(flush: true)
		}
	}


	private def setupSentMessages() {
		(1..51).each { i ->
			new Fmessage(src: "src${i}", dst: "dst${i}", text: "sent ${i}", status: MessageStatus.SENT).save(flush: true)
		}
	}


	private def setupPendingMessages() {
		(1..51).each { i ->
			new Fmessage(src: "src${i}", dst: "dst${i}", text: "pending ${i}", status: MessageStatus.SEND_PENDING).save(flush: true)
		}
	}


	private def setupDeletedMessages() {
		(1..51).each { i ->
			new Fmessage(src: "src${i}", dst: "dst${i}", text: "deleted ${i}",deleted: true).save(flush: true)
		}
	}

	private def setupFolderAndItsMessages() {
		def folder = new Folder(name:'folder').save(failOnError:true, flush:true)
		(1..51).each { i ->
			folder.addToMessages(new Fmessage(src: "src${i}", dst: "dst${i}", text: "folder ${i}"))
		}
		folder.save(flush: true)
	}


	private def setupPollAndItsMessages() {
		def poll = new Poll(title:'poll')
		poll.addToResponses(new PollResponse(value: "Yes"))
		poll.addToResponses(new PollResponse(value: "No"))
		poll.save(flush: true)
		def yes = PollResponse.findByValue('Yes')
		def no = PollResponse.findByValue('No')
		(1..25).each { i ->
			yes.addToMessages(new Fmessage(src: "src${i}", dst: "dst${i}", text: "yes ${i}"))
		}
		(1..26).each { i ->
			no.addToMessages(new Fmessage(src: "src${i}", dst: "dst${i}", text: "no ${i}"))
		}
		yes.save(flush: true)
		no.save(flush: true)
	}


}



