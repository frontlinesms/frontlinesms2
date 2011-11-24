package frontlinesms2.domain

import frontlinesms2.*

class PollResponseISpec extends grails.plugin.spock.IntegrationSpec {
	def "Adding a message to a PollResponse will cascade to the message's activity value"() {
		given:
			def p = new Poll()
			def r = new PollResponse(value:'yes')
			p.addToResponses(r)
			p.save(flush:true, failOnError:true)
			def m = new Fmessage()
		when:
			r.addToMessages(m)
			r.save()
		then:
			m.messageOwner == r
	}
}