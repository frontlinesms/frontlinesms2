package frontlinesms2.domain

import spock.lang.*
import grails.plugin.spock.*
import frontlinesms2.*

class CascadePollSaveISpec extends grails.plugin.spock.IntegrationSpec {

	def 'saving a poll cascades to saving poll responses'() {
		when:
			def p = new Poll(name:'Football Teams', responses:[new PollResponse(value:'manchester'),
						new PollResponse(value:'barcelona')]).save(failOnError:true, flush:true)
		then:
			p.name == 'Football Teams'
			p.responses.size() == 2
		when:
			p.refresh()
		then:
			p.name == 'Football Teams'
			p.responses.size() == 2
		when:
			p = Poll.get(p.id)
		then:
			p.name == 'Football Teams'
			p.responses.size() == 2
	}
	
	def "Deleting a Poll will cascade to delete its responses and its messages"() {
		given:
			def poll = new Poll(name:'Football Teams')
			poll.addToResponses(new PollResponse(value:'manchester'))
			poll.addToResponses(new PollResponse(value:'barcelona')).save(failOnError:true, flush:true)
			def r = PollResponse.findByValue('barcelona')
			def m = new Fmessage(src:'Bob', text:'I like manchester', date: new Date(), inbound: true).save(failOnError:true, flush:true)
		when:
			r.addToMessages(m)
			poll.save(flush: true)
		then:
			r.messages.find {it == m}
			Fmessage.find(m)
		when:
			poll.deleted = true
		then:
			m.isDeleted
	}
}

