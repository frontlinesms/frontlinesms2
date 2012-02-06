package frontlinesms2.domain

import frontlinesms2.*
class DispatchISpec extends grails.plugin.spock.IntegrationSpec {
	def "updating dispatch status should update the associated message status"() {
		setup:
			def dis = new Dispatch(dst: '12345', status: DispatchStatus.PENDING)
			def message = new Fmessage(text:"test", inbound:false, date:new Date()).addToDispatches(dis).save(flush:true, failOnError:true)
		when:
			dis.save()
			message = Fmessage.get(message.id)
		then:
			message.hasPending && !message.hasFailed && !message.hasSent
		when:
			dis.status = DispatchStatus.FAILED
			dis.save(failOnError:true, flush:true)
			message = Fmessage.get(message.id)
		then:
			!message.hasPending && message.hasFailed && !message.hasSent
		when:
			dis.status = DispatchStatus.SENT
			dis.dateSent = new Date()
			dis.save(failOnError:true, flush:true)
			message = Fmessage.get(message.id)
		then:
			!message.hasPending && !message.hasFailed && message.hasSent
	}
}
