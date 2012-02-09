package frontlinesms2.service

import frontlinesms2.*

class DispatchRouterServiceISpec extends grails.plugin.spock.IntegrationSpec {
	def dispatchRouterService
	
	def 'handleFailed should update dispatch status to FAILED and update message_hasFailed and message_hasPending accordingly'() {
		given:
			def o = setUpOutgoingMessage()
			def m = o.m
			def d = o.d
			def x = o.x
		when:
			dispatchRouterService.handleFailed(x)
			m = Fmessage.get(m.id)
		then:
			Dispatch.get(d.id).status == DispatchStatus.FAILED
			!m.hasPending && m.hasFailed && !m.hasSent
	}
	
	def 'handleCompleted should update dispatch status to SENT and update message_hasSent and message_hasPending accordingly'() {
		given:
			def o = setUpOutgoingMessage()
			def m = o.m
			def d = o.d
			def x = o.x
		when:
			dispatchRouterService.handleCompleted(x)
			m = Fmessage.get(m.id)
		then:
			Dispatch.get(d.id).status == DispatchStatus.SENT
			!m.hasPending && !m.hasFailed && m.hasSent
	}
	
	private def setUpOutgoingMessage() {
		Fmessage m = new Fmessage(text:"test", inbound:false, date:new Date())
		Dispatch d = new Dispatch(dst: '54321', status: DispatchStatus.PENDING)
		m.addToDispatches(d)
		m.save(failOnError:true, flush:true)
		assert m.hasPending && !m.hasFailed && !m.hasSent
		org.apache.camel.Exchange x = Mock()
		org.apache.camel.Message camelMessage = [
				getHeader:{ v -> v=='frontlinesms.dispatch.id'?d.id:null },
				getBody:{ null }
		] as org.apache.camel.Message
		x.in >> camelMessage
		return [m:m, d:d, x:x]
	}
}
		
