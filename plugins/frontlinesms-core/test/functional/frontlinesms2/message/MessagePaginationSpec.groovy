package frontlinesms2.message

import frontlinesms2.*
import frontlinesms2.page.*
import frontlinesms2.message.*
import frontlinesms2.poll.*
import frontlinesms2.folder.*

class MessagePaginationSpec  extends grails.plugin.geb.GebSpec  {

	def "should paginate inbox messages"() {
		setup:
			setupInboxMessages()
		when:
			to PageMessageInbox
		then:
			messageList.messages.size() == 50
		when:
			footer.nextPage.click()
			waitFor {footer.nextPage.hasClass("disabled")}
		then:
			messageList.messages.size() == 1

	}

	def "should paginate pending messages"() {
		setup:
			setupPendingMessages()
		when:
			to PageMessagePending
		then:
			messageList.messages.size() == 50
			footer.prevPage.hasClass("disabled")
		when:
			footer.nextPage.click()
			waitFor {!footer.prevPage.hasClass("disabled")}
		then:
			messageList.messages.size() == 1
			footer.nextPage.hasClass("disabled")

	}

	def "should paginate deleted messages"() {
		setup:
			setupDeletedMessages()
		when:
			to PageMessageTrash
		then:
			messageList.messages.size() == 50
			footer.prevPage.hasClass("disabled")
		when:
			footer.nextPage.click()
			waitFor {!footer.prevPage.hasClass("disabled")}
		then:
			messageList.messages.size() == 1
			footer.nextPage.hasClass("disabled")
	}

	def "should paginate sent messages"() {
		setup:
			setupSentMessages()
		when:
			to PageMessageSent
		then:
			messageList.messages.size() == 50
			footer.prevPage.hasClass("disabled")
		when:
			footer.nextPage.click()
			waitFor {!footer.prevPage.hasClass("disabled")}
		then:
			messageList.messages.size() == 1
			footer.nextPage.hasClass("disabled")
	}

	def "should paginate folder messages"() {
		setup:
			setupFolderAndItsMessages()
			def folderId = Folder.findByName("folder").id
		when:
			to PageMessageFolder, folderId
		then:
			messageList.messages.size() == 50
			footer.prevPage.hasClass("disabled")
		when:
			footer.nextPage.click()
			waitFor {!footer.prevPage.hasClass("disabled")}
		then:
			messageList.messages.size() == 1
			footer.nextPage.hasClass("disabled")
	}

	def "should paginate poll messages"() {
		setup:
			setupPollAndItsMessages()
			def pollId = Poll.findByName("poll").id
		when:
			to PageMessagePoll, pollId
		then:
			messageList.messages.size() == 50
			footer.prevPage.hasClass("disabled")
		when:
			footer.nextPage.click()
			waitFor {!footer.prevPage.hasClass("disabled")}
		then:
			messageList.messages.size() == 1
			footer.nextPage.hasClass("disabled")
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
		Trash.build(displayName:message.displayName, displayText:message.text, objectClass:message.class.name, objectId:message.id)
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

