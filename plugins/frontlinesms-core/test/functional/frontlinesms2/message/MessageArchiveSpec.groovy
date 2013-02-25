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
			messageList.toggleSelected(0)
			waitFor { singleMessageDetails.text == "test2" }
			singleMessageDetails.archive.click()
			to PageArchiveInbox
		then:
			messageList.messages[0].text == "test2"
		when:
			to PageMessageInbox
		then:
			!messageList.messages.text.contains("test2")
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
			messageList.toggleSelected(0)
			waitFor { singleMessageDetails.text == "hi Mary" }
			singleMessageDetails.archive.click()
			to PageArchiveSent
		then:
	        waitFor { messageList.messages[0].text == "hi Mary" }
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
			messageList.toggleSelected(0)
			messageList.toggleSelected(1)
		 then:
			waitFor { !multipleMessageDetails.archiveAll.displayed }
	 }

}
