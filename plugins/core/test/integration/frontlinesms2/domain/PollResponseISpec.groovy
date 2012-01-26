package frontlinesms2.domain

import frontlinesms2.*

class PollResponseISpec extends grails.plugin.spock.IntegrationSpec {
	def "Adding a message to a PollResponse will cascade to the message's messageOwner value"() {
		given:
			def p = new Poll(name:'new')
			def r = new PollResponse(value:'yes')
			p.addToResponses(new PollResponse(value: 'Unknown', key: 'Unknown'))
			p.addToResponses(new PollResponse(value: 'No', key: 'No'))
			p.addToResponses(r)
			p.save(flush:true, failOnError:true)
			def m = new Fmessage()
		when:
			r.addToMessages(m)
			r.save()
		then:
			r.messages.contains(m)
			m.messageOwner == p
	}
}