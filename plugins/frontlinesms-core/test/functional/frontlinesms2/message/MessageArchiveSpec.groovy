package frontlinesms2.message

import frontlinesms2.*
import frontlinesms2.archive.*
import frontlinesms2.poll.*

class MessageArchiveSpec extends MessageBaseSpec {
	def setup() {
		createTestData()
	}

	def 'archived messages do not show up in inbox view'() {
		when:
			to PageArchiveInbox
		then:
			waitFor() { messageList.noContent.text() == "No messages here, yet." }
		when:
			to PageMessageInbox
			messageList.toggleSelect(0)
			waitFor { singleMessageDetails.text == 'test2' }
			singleMessageDetails.archive.click()
			to PageArchiveInbox
		then:
			messageList.messageText(0) == 'test2'
		when:
			to PageMessageInbox
		then:
			messageList.messageText(0) != 'test2'
	}

	def 'archived messages do not show up in sent view'() {
		setup:
			def d = new Dispatch(dst:"34567890", dateSent: new Date(), status: DispatchStatus.SENT)
			new Fmessage(src:'src', hasSent:true, inbound:false, text:'hi Mary').addToDispatches(d).save(flush:true, failOnError:true)
		when:
		    to PageArchiveSent
		then:
			waitFor() { messageList.noContent.text() == "No messages here, yet." }
		when:
			to PageMessageSent
			messageList.toggleSelect(0)
			waitFor { singleMessageDetails.text == "hi Mary" }
			singleMessageDetails.archive.click()
			to PageArchiveSent
		then:
	        waitFor { messageList.messageText(0) == "hi Mary" }
		when:
			to PageMessageSent
		then:
			waitFor() { messageList.noContent.text() == "No messages here, yet." }
	}

	 def 'should not be able to archive activity messages'() {
		when:
			to PageMessagePoll, Poll.findByName('Miauow Mix').id, Fmessage.findBySrc('Barnabus')
		then:
			waitFor { singleMessageDetails.displayed }
			messageList.toggleSelect(0)
			messageList.toggleSelect(1)
		 then:
			waitFor { !multipleMessageDetails.archiveAll.displayed }
	 }

}
