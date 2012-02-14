package frontlinesms2.domain

import spock.lang.*
import grails.plugin.spock.*
import frontlinesms2.*

class CascadePollSaveISpec extends grails.plugin.spock.IntegrationSpec {
	def controller
	def setup() {
		controller = new PollController()
	}

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
}
