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
			singleMessageDetails.noneSelected
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
			remote {
				def d = new Dispatch(dst:"34567890", dateSent: new Date(), status: DispatchStatus.SENT)
				new Fmessage(src:'src', hasSent:true, inbound:false, text:'hi Mary').addToDispatches(d).save(flush:true, failOnError:true)
				null
			}
		when:
			to PageArchiveSent
		then:
			waitFor() { messageList.noContent.text() == 'fmessage.messages.sent.none' }
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
			waitFor() { messageList.noContent.text() == 'fmessage.messages.sent.none' }
	}

	 def 'should not be able to archive activity messages'() {
		when:
			to PageMessagePoll, 'Miauow Mix', remote { Fmessage.findBySrc('Barnabus').id }
		then:
			waitFor { singleMessageDetails.displayed }
			messageList.toggleSelect(0)
			messageList.toggleSelect(1)
		 then:
			waitFor { !multipleMessageDetails.archiveAll.displayed }
	 }
}

