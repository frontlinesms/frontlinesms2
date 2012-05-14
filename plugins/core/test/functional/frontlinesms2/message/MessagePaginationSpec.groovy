package frontlinesms2.message

import frontlinesms2.*

class MessagePaginationSpec  extends grails.plugin.geb.GebSpec  {

	def "should paginate inbox messages"() {
		setup:
			setupInboxMessages()
		when:
			go "message/inbox"
		then:
			$("#message-list tr").size() == 51
		when:
			$(".nextLink").click()
			waitFor {!$(".prevLink").hasClass("disabled")}
		then:
			$("#message-list tr").size() == 2

	}

	def "should paginate pending messages"() {
		setup:
			setupPendingMessages()
		when:
			go "message/pending"
		then:
			$("#message-list tr").size() == 51
			$(".prevLink").hasClass("disabled")
		when:
			$(".nextLink").click()
			waitFor {!$(".prevLink").hasClass("disabled")}
		then:
			$("#message-list tr").size() == 2
			$(".nextLink").hasClass("disabled")

	}

	def "should paginate deleted messages"() {
		setup:
			setupDeletedMessages()
		when:
			go "message/trash"
		then:
			$("#message-list tr").size() == 51
			$(".prevLink").hasClass("disabled")
		when:
			$(".nextLink").click()
			waitFor {!$(".prevLink").hasClass("disabled")}
		then:
			$("#message-list tr").size() == 2
			$(".nextLink").hasClass("disabled")
	}

	def "should paginate sent messages"() {
		setup:
			setupSentMessages()
		when:
			go "message/sent"
		then:
			$("#message-list tr").size() == 51
			$(".prevLink").hasClass("disabled")
		when:
			$(".nextLink").click()
			waitFor {!$(".prevLink").hasClass("disabled")}
		then:
			$("#message-list tr").size() == 2
			$(".nextLink").hasClass("disabled")
	}

	def "should paginate folder messages"() {
		setup:
			setupFolderAndItsMessages()
			def folderId = Folder.findByName("folder").id
		when:
			go "message/folder/${folderId}"
		then:
			$("#message-list tr").size() == 51
			$(".prevLink").hasClass("disabled")
		when:
			$(".nextLink").click()
			waitFor {!$(".prevLink").hasClass("disabled")}
		then:
			$("#message-list tr").size() == 2
			$(".nextLink").hasClass("disabled")
	}

	def "should paginate poll messages"() {
		setup:
			setupPollAndItsMessages()
			def pollId = Poll.findByName("poll").id
		when:
			go "message/poll/${pollId}"
		then:
			$("#message-list .main-table tr").size() == 51
			$(".prevLink").hasClass("disabled")
		when:
			$(".nextLink").click()
			waitFor {!$(".prevLink").hasClass("disabled")}
		then:
			$("#message-list .main-table tr").size() == 2
			$(".nextLink").hasClass("disabled")
	}

	private def setupInboxMessages() {
		(1..51).each { i ->
			Fmessage.build(src:"src${i}", text:"inbox ${i}", date:new Date()-i)
		}
	}


	private def setupSentMessages() {
		(1..51).each { i ->
			new Fmessage(src:"src${i}", text:"sent ${i}")
					.addToDispatches(dst:"345678", status:DispatchStatus.SENT, dateSent:new Date())
					.save(flush:true, failOnError:true)
		}
	}


	private def setupPendingMessages() {
		(1..51).each { i ->
			new Fmessage(src:"src${i}", text:"pending ${i}")
					.addToDispatches(dst:"345678", status: DispatchStatus.PENDING)
					.save(flush:true, failOnError:true)
		}
	}


	private def setupDeletedMessages() {
		(1..51).each { i ->
			deleteMessage(Fmessage.build(src:"src${i}", text:"deleted ${i}"))
		}
	}

	private def deleteMessage(Fmessage message) {
		message.isDeleted = true
		message.save(failOnError:true, flush:true)
		Trash.build(displayName:message.displayName, displayText:message.text, objectClass:message.class, objectId:message.id)
	}

	private def setupFolderAndItsMessages() {
		def folder = Folder.build(name:'folder')
		(1..51).each { i ->
			folder.addToMessages(Fmessage.build(src:"src${i}", text:"folder ${i}"))
		}
		folder.save(failOnError:true, flush:true)
	}


	private def setupPollAndItsMessages() {
		def yes = new PollResponse(value:"Yes")
		def no = new PollResponse(value:"No")
		def unknown = new PollResponse(value:"Unknown")
		def poll = new Poll(name:'poll')
				.addToResponses(yes)
				.addToResponses(no)
				.addToResponses(unknown)
				.save(flush:true, failOnError:true)
		(1..25).each { i ->
			yes.addToMessages(Fmessage.build(src:"src${i}", text:"yes ${i}"))
		}
		(1..26).each { i ->
			no.addToMessages(Fmessage.build(src:"src${i}", text:"no ${i}"))
		}
		poll.save(flush:true, failOnError:true)
	}
}

