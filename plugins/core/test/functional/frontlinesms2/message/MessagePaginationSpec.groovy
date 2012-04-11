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
			new Fmessage(src: "src${i}", text: "inbox ${i}", date: new Date() - i, inbound:true).save(flush: true)
		}
	}


	private def setupSentMessages() {
		def d = new Dispatch(dst:"345678", status: DispatchStatus.SENT, dateSent:new Date())	
		(1..51).each { i ->
			new Fmessage(src: "src${i}", text: "sent ${i}", hasSent:true, inbound:false, date: new Date()).addToDispatches(d).save(flush: true, failOnError:true)
		}
	}


	private def setupPendingMessages() {
		def d = new Dispatch(dst:"345678", status: DispatchStatus.PENDING)
		(1..51).each { i ->
			new Fmessage(src: "src${i}", text: "pending ${i}", inbound:false, hasPending:true, date: new Date()).addToDispatches(d).save(flush: true, failOnError:true)
		}
	}


	private def setupDeletedMessages() {
		(1..51).each { i ->
			deleteMessage(new Fmessage(src: "src${i}", text: "deleted ${i}", inbound: true, date: new Date()).save(flush: true))
		}
	}

	def deleteMessage(Fmessage message) {
		message.isDeleted = true
		message.save(flush:true)
		new Trash(identifier:message.displayName, message:message.text, objectType:message.class.name, linkId:message.id).save(failOnError: true, flush: true)
	}

	private def setupFolderAndItsMessages() {
		def folder = new Folder(name:'folder').save(failOnError:true, flush:true)
		(1..51).each { i ->
			folder.addToMessages(new Fmessage(src: "src${i}", text: "folder ${i}", inbound: true, date: new Date()))
		}
		folder.save(flush: true)
	}


	private def setupPollAndItsMessages() {
		def poll = new Poll(name:'poll')
		def yes = new PollResponse(value: "Yes")
		def no = new PollResponse(value: "No")
		def unknown = new PollResponse(value: "Unknown")
		poll.addToResponses(yes).addToResponses(no).addToResponses(unknown)
		poll.save(flush:true, failOnError:true)
		(1..25).each { i ->
			yes.addToMessages(new Fmessage(src: "src${i}", text: "yes ${i}", inbound: true, date: new Date()))
		}
		(1..26).each { i ->
			no.addToMessages(new Fmessage(src: "src${i}", text: "no ${i}", inbound: true, date: new Date()))
		}
		poll.save(flush: true)
	}


}



