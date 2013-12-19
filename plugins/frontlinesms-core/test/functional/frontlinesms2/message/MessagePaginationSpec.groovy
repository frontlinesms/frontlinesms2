package frontlinesms2.message

import frontlinesms2.*
import frontlinesms2.page.*
import frontlinesms2.message.*
import frontlinesms2.poll.*
import frontlinesms2.folder.*

class MessagePaginationSpec extends grails.plugin.geb.GebSpec  {
	def "should paginate inbox messages"() {
		setup:
			setupInboxMessages()
		when:
			to PageMessageInbox
		then:
			messageList.messageCount() == 50
		when:
			footer.nextPage.click()
			waitFor {footer.nextPage.hasClass("disabled")}
		then:
			messageList.messageCount() == 1
	}

	def "should paginate pending messages"() {
		setup:
			setupPendingMessages()
		when:
			to PageMessagePending
		then:
			messageList.messageCount() == 50
			footer.prevPage.hasClass("disabled")
		when:
			footer.nextPage.click()
			waitFor {!footer.prevPage.hasClass("disabled")}
		then:
			messageList.messageCount() == 1
			footer.nextPage.hasClass("disabled")
	}

	def "should paginate deleted messages"() {
		setup:
			setupDeletedMessages()
		when:
			to PageMessageTrash
		then:
			messageList.messageCount() == 50
			footer.prevPage.hasClass("disabled")
		when:
			footer.nextPage.click()
			waitFor {!footer.prevPage.hasClass("disabled")}
		then:
			messageList.messageCount() == 1
			footer.nextPage.hasClass("disabled")
	}

	def "should paginate sent messages"() {
		setup:
			setupSentMessages()
		when:
			to PageMessageSent
		then:
			messageList.messageCount() == 50
			footer.prevPage.hasClass("disabled")
		when:
			footer.nextPage.click()
			waitFor {!footer.prevPage.hasClass("disabled")}
		then:
			messageList.messageCount() == 1
			footer.nextPage.hasClass("disabled")
	}

	def "should paginate folder messages"() {
		setup:
			setupFolderAndItsMessages()
		when:
			to PageMessageFolder, 'folder'
		then:
			messageList.messageCount() == 50
			footer.prevPage.hasClass("disabled")
		when:
			footer.nextPage.click()
			waitFor {!footer.prevPage.hasClass("disabled")}
		then:
			messageList.messageCount() == 1
			footer.nextPage.hasClass("disabled")
	}

	def "should paginate poll messages"() {
		setup:
			setupPollAndItsMessages()
		when:
			to PageMessagePoll, 'poll'
		then:
			messageList.messageCount() == 50
			footer.prevPage.hasClass("disabled")
		when:
			footer.nextPage.click()
			waitFor {!footer.prevPage.hasClass("disabled")}
		then:
			messageList.messageCount() == 1
			footer.nextPage.hasClass("disabled")
	}

	private def setupInboxMessages() {
		remote {
			(1..51).each { i ->
				TextMessage.build(src:"src${i}", text:"inbox ${i}", date:new Date()-i)
			}
			null
		}
	}


	private def setupSentMessages() {
		remote {
			(1..51).each { i ->
				new TextMessage(src:"src${i}", text:"sent ${i}")
						.addToDispatches(dst:"345678", status:DispatchStatus.SENT, dateSent:new Date())
						.save(flush:true, failOnError:true)
			}
			null
		}
	}


	private def setupPendingMessages() {
		remote {
			(1..51).each { i ->
				new TextMessage(src:"src${i}", text:"pending ${i}")
						.addToDispatches(dst:"345678", status: DispatchStatus.PENDING)
						.save(flush:true, failOnError:true)
			}
			null
		}
	}


	private def setupDeletedMessages() {
		remote {
			def deleteMessage = { TextMessage message ->
				message.isDeleted = true
				message.save(failOnError:true, flush:true)
				Trash.build(displayName:message.displayName, displayText:message.text, objectClass:message.class.name, objectId:message.id)
			}
			(1..51).each { i ->
				deleteMessage(TextMessage.build(src:"src${i}", text:"deleted ${i}"))
			}
			null
		}
	}

	private def setupFolderAndItsMessages() {
		remote {
			def folder = Folder.build(name:'folder')
			(1..51).each { i ->
				folder.addToMessages(TextMessage.build(src:"src${i}", text:"folder ${i}"))
			}
			folder.save(failOnError:true, flush:true)
			null
		}
	}

	private def setupPollAndItsMessages() {
		remote {
			def yes = new PollResponse(key:'A', value:"Yes")
			def no = new PollResponse(key:'B', value:"No")
			def poll = new Poll(name:'poll')
					.addToResponses(yes)
					.addToResponses(no)
					.addToResponses(PollResponse.createUnknown())
					.save(flush:true, failOnError:true)
			(1..25).each { i ->
				yes.addToMessages(TextMessage.build(src:"src${i}", text:"yes ${i}"))
			}
			(1..26).each { i ->
				no.addToMessages(TextMessage.build(src:"src${i}", text:"no ${i}"))
			}
			poll.save(flush:true, failOnError:true)
			null
		}
	}
}

