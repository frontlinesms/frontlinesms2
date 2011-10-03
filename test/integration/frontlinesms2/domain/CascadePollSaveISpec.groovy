package frontlinesms2.domain

import spock.lang.*
import grails.plugin.spock.*
import frontlinesms2.*

class CascadePollSaveISpec extends grails.plugin.spock.IntegrationSpec {

	def 'saving a poll cascades to saving poll responses'() {
		when:
			def p = new Poll(title:'Football Teams', responses:[new PollResponse(value:'manchester'),
						new PollResponse(value:'barcelona')]).save(failOnError:true, flush:true)
		then:
			p.title == 'Football Teams'
			p.responses.size() == 2
		when:
			p.refresh()
		then:
			p.title == 'Football Teams'
			p.responses.size() == 2
		when:
			p = Poll.get(p.id)
		then:
			p.title == 'Football Teams'
			p.responses.size() == 2
	}
	
	def "Deleting a Poll will cascade to delete its responses messages"() {
		given:
			def poll = new Poll(title:'Football Teams')
			poll.addToResponses(new PollResponse(value:'manchester'))
			poll.addToResponses(new PollResponse(value:'barcelona')).save(failOnError:true, flush:true)
			def r = PollResponse.findByValue('barcelona')
			def m = new Fmessage(src:'Bob', dst:'+254987654', text:'I like manchester').save(failOnError:true, flush:true)
		when:
			r.addToMessages(m)
			r.save(flush: true)
		then:
			r.getMessages()*.find(m)
			Fmessage.find(m)
		when:
			poll.delete(flush: true)
		then:
			!Fmessage.find(m)
	}
}

