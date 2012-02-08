package frontlinesms2.domain

import frontlinesms2.*
class DispatchISpec extends grails.plugin.spock.IntegrationSpec {
	def "updating dispatch status to SENT should update the associated message status"() {
		setup:
			def dis = new Dispatch(dst: '12345', status: DispatchStatus.PENDING)
			def message = new Fmessage(text:"test", inbound:false, date:new Date()).addToDispatches(dis).save(flush:true, failOnError:true)
			message = Fmessage.get(message.id)
			assert message.hasPending && !message.hasFailed && !message.hasSent
		when:
			dis = Dispatch.get(dis.id)
			dis.status = DispatchStatus.SENT
			dis.dateSent = new Date()
			dis.save(failOnError:true, flush:true)
			message = Fmessage.get(message.id)
		then:
			!message.hasPending && !message.hasFailed && message.hasSent
	}
	
	def "updating dispatch status to FAILED should update the associated message status"() {
		setup:
			def dis = new Dispatch(dst: '12345', status: DispatchStatus.PENDING)
			def message = new Fmessage(text:"test", inbound:false, date:new Date()).addToDispatches(dis).save(flush:true, failOnError:true)
			message = Fmessage.get(message.id)
			assert message.hasPending && !message.hasFailed && !message.hasSent
		when:
			dis = Dispatch.get(dis.id)
			dis.status = DispatchStatus.FAILED
			dis.save(failOnError:true, flush:true)
			message = Fmessage.get(message.id)
		then:
			!message.hasPending && message.hasFailed && !message.hasSent
	}
}

