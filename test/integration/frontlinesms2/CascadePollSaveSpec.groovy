package frontlinesms2

import spock.lang.*
import grails.plugin.spock.*

class CascadePollSaveSpec extends grails.plugin.spock.IntegrationSpec {
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
	
	def "Deleting a PollResponse will cascade to delete the message"() {
		// FIXME this almost certainly needs to be an integration test due to reliance on cascades (cascades are probably enforced by Hibernate rather than GORM)
		given:
			new Poll(title:'Football Teams', responses:[new PollResponse(value:'manchester'),
						new PollResponse(value:'barcelona')]).save(failOnError:true, flush:true)
				def r = new PollResponse(value:'yes', poll:new Poll()).save(failOnError:true, flush:true)
			def m = new Fmessage(src:'Bob', dst:'+254987654', text:'I like manchester').save(failOnError:true, flush:true)
		when:
			r.addToMessages(m)
			r.save()
		then:
			r.getMessages()*.find(m)
			Fmessage.find(m)
		when:
			r.delete()
		then:
			!Fmessage.find(m)
	}
	
	def "Deleting a Poll will cascade to delete the messages"() {
		// FIXME this almost certainly needs to be an integration test due to reliance on cascades (cascades are probably enforced by Hibernate rather than GORM)
		given:
			def p = new Poll(title:'Football Teams', responses:[new PollResponse(value:'manchester'),
						new PollResponse(value:'barcelona')]).save(failOnError:true, flush:true)
				def r = PollResponse.findByValue('manchester')
			def m = new Fmessage(src:'Bob', dst:'+254987654', text:'I like manchester').save(failOnError:true, flush:true)
			r.addToMessages(m)
			r.save()
		when:
			p.delete()
		then:
			!Poll.find(p)
			!PollResponse.find(r)
			!Fmessage.find(m)
	}
}

