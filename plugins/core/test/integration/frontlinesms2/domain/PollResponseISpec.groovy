package frontlinesms2.domain

import frontlinesms2.*

class PollResponseISpec extends grails.plugin.spock.IntegrationSpec {
	def "Adding a message to a PollResponse will set the message_messageOwner tp the poll"() {
		given:
			def p = new Poll(name:'new')
			def r = new PollResponse(value:'yes')
			p.addToResponses(PollResponse.createUnknown())
			p.addToResponses(new PollResponse(value: 'No', key: 'No'))
			p.addToResponses(r)
			p.save(flush:true, failOnError:true)
			def m = new Fmessage(inbound:true)
		when:
			r.addToMessages(m)
			p.save(failOnError:true, flush:true)
			r.refresh()
			m.refresh()
		then:
			r.messages.contains(m)
			m.messageOwner == p
	}
}
