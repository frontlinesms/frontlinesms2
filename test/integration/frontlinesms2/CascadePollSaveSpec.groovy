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
			println p.responses
		when:
			p.refresh()
		then:
			p.title == 'Football Teams'
			p.responses.size() == 2
			println p.responses
		when:
			p = Poll.get(p.id)
		then:
			p.title == 'Football Teams'
			p.responses.size() == 2
			println p.responses
	}
}

