package frontlinesms2.message

import java.util.regex.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date

import frontlinesms2.*

@Mixin(frontlinesms2.utils.GebUtil)
class MessagePendingSpec extends grails.plugin.geb.GebSpec {
	def setup() {
		remote {
			new TextMessage(text:'text',src:"src1", starred:true)
					.addToDispatches(dst:"dst2", status:DispatchStatus.FAILED)
					.save(failOnError:true, flush:true)
			new TextMessage(text:'text',src:"src2")
					.addToDispatches(dst:"dst1", status:DispatchStatus.PENDING)
					.save(failOnError:true, flush:true)
			new TextMessage(text:'text',src:"src")
					.addToDispatches(dst:"dst3", status:DispatchStatus.SENT, dateSent:new Date())
					.save(failOnError:true, flush:true)
			TextMessage.build(src:"src")
			null
		}
	}

	def "'Reply All' button does not appears for multiple selected messages"() {
		when:
			to PageMessagePending
			messageList.toggleSelect(0)
			waitFor { singleMessageDetails.displayed }
			messageList.toggleSelect(1)
			waitFor("veryslow") { multipleMessageDetails.displayed }
		then:
			waitFor("veryslow") { multipleMessageDetails.text == 'message.multiple.selected[2]' }
			!multipleMessageDetails.replyAll.displayed
	}

	def "should be able to retry a failed message"() {
		when:
			to PageMessagePending
			messageList.toggleSelect(1)
		then:
			waitFor { singleMessageDetails.retry.displayed }
		when:
			singleMessageDetails.retry.click()
		then:	
			waitFor { notifications.flashMessage.displayed }
	}

	def "should be able to retry all failed messages"() {
		when:
			to PageMessagePending
			messageList.toggleSelect(1)
		then:
			waitFor { singleMessageDetails.retry.displayed }
		when:
			messageList.selectAll.click()
		then:
			waitFor { multipleMessageDetails.retry.displayed }
		when:
			multipleMessageDetails.retry.click()
		then:
			waitFor{ notifications.flashMessage.displayed }
	}
}

