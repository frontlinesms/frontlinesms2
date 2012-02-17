package frontlinesms2.message

import java.util.regex.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date

import frontlinesms2.*

@Mixin(frontlinesms2.utils.GebUtil)
class MessagePendingSpec extends grails.plugin.geb.GebSpec {
	def setup() {
		def pendingDispatch = new Dispatch(dst:"dst1", status:DispatchStatus.PENDING)
		def failedDispatch = new Dispatch(dst:"dst2", status:DispatchStatus.FAILED)
		def sentDispatch = new Dispatch(dst:"dst3", status:DispatchStatus.SENT, dateSent:new Date())
		new Fmessage(src: "src1", hasFailed:true, inbound:false, starred: true, date: new Date()).addToDispatches(failedDispatch).save(flush: true)
		new Fmessage(src: "src2", hasPending:true, inbound:false, date: new Date()).addToDispatches(pendingDispatch).save(flush: true)
		new Fmessage(src: "src", inbound:false, hasSent:true, date: new Date()).addToDispatches(sentDispatch).save(flush: true)
		new Fmessage(src: "src", inbound:true, date: new Date()).save(flush: true)
	}
	
	def "'Reply All' button does not appears for multiple selected messages"() {
		when:
			go "message/pending"
		then:
			at PageMessagePending
		when:
			messagesSelect[1].click()
			messagesSelect[2].click()
		then:
			waitFor { !$('.multi-action a', text:'Reply All').displayed }
	}

	def "should filter pending messages for pending and failed messages"() {
		when:
			go "message/pending"
		then:
			at PageMessagePending
			$("#messages tbody tr").size() == 2
		when:
			$('a', text:'Failed').click()
		then:	
			waitFor { $("#messages tbody tr").size() == 1 }
			getColumnText('message-list', 3) == ['To: dst2']
		when:
			$('a', text:'All').click()
		then:	
			waitFor { $("#messages tbody tr").size() == 2 }
			getColumnText('message-list', 3).containsAll(['To: dst1', 'To: dst2'])
	}

	def "retry button must not apper if there are no failed messages"() {
		when:
			go "message/pending"
		then:
			at PageMessagePending
			!$("#retry").displayed
		when:
			messagesSelect[0].click()
		then:
			waitFor { $("#multiple-messages").displayed }
			!$("#retry-failed").displayed
	}

	def "should be able to retry a failed message"() {
		when:
			go "message/pending"
		then:
			at PageMessagePending
		when:
			$("a", text:contains("dst2")).click()
		then:
			waitFor { $("#btn_reply").displayed }
		when:
			$("#btn_reply").click()
		then:	
			waitFor { $(".flash").displayed }
	}

	def "should be able to retry all failed messages"() {
		when:
			go "message/pending"
		then:
			at PageMessagePending
		when:
			$("a", text: contains("dst2")).click()
		then:
			waitFor { $("#btn_reply").displayed }
		when:
			messagesSelect[0].click()
		then:
			waitFor { $("#btn_reply").displayed }
		when:
			$("#btn_reply").click()
		then:
			waitFor{ $(".flash").displayed }
	}
}
