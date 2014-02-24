package frontlinesms2.missedcall

import java.util.regex.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import frontlinesms2.popup.*
import frontlinesms2.*
import frontlinesms2.message.*

import static frontlinesms.grails.test.EchoMessageSource.formatDate

class MissedCallViewSpec extends MessageBaseSpec {
	def setup() {
		createTestMissedCalls()
	}
	
	def "All missed calls are displayed as a list"() {
		when:
			to PageMissedCall
		then:
			messageList.messageCount() == 3
	}

	def "When viewing a missed call, the details are displayed, as well as relevant actions"() {
		when:
			to PageMissedCall, MissedCall.findBySrc('123').id
		then:
			singleMessageDetails.sender.startsWith '123'
			singleMessageDetails.date
			singleMessageDetails.text == 'missedCall.displaytext[123]'
	}

	def "Can delete a missed call"() {
		when:
			to PageMissedCall, MissedCall.findBySrc('123').id
		then:
			singleMessageDetails.delete.displayed
		when:
			singleMessageDetails.delete.click()
		then:
			waitFor {
				at PageMissedCall
				messageList.messageCount() == 2
			}
			notifications.flashMessageText
	}

	def "Can delete multiple missed calls"() {
		when:
			to PageMissedCall
			messageList.selectAll.click()
		then:
			waitFor {
				multipleMessageDetails.deleteAll.displayed
			}
		when:
			multipleMessageDetails.deleteAll.click()
		then:
			waitFor {
				notifications.flashMessageText
			}
			messageList.messageCount() == 1
	}

	def "Can reply to a missed call by SMS"() {
		when:
			to PageMissedCall, MissedCall.findBySrc('123').id
		then:
			singleMessageDetails.reply.displayed
		when:
			singleMessageDetails.reply.click()
		then:
			waitFor {
				at QuickMessageDialog
			}
			recipients.count() == 1
	}

	def "Can reply to multiple missed calls by SMS"() {
		given:
			remote {
				Contact.build(name:'Alice', mobile:'123')
				Contact.build(name:'June', mobile:'+254778899')
				null
			}
		when:
			to PageMissedCall
			messageList.toggleSelect(0)
		then:
			waitFor {
				singleMessageDetails.sender
			}
		when:
			messageList.toggleSelect(1)
		then:
			waitFor { multipleMessageDetails.replyAll.displayed }
		when:
			multipleMessageDetails.replyAll.click()
		then:
			waitFor { at QuickMessageDialog }
			recipients.count() == 2
	}

	private def createTestMissedCalls() {
		new MissedCall(src:'123', date:new Date() - 2).save(flush:true, failOnError: true)
		new MissedCall(src:'214124', date:new Date() - 1).save(flush:true, failOnError: true)
		new MissedCall(src:'321', date:new Date()).save(flush:true, failOnError: true)
	}
}

