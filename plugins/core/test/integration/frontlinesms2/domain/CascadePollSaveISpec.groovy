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
						new PollResponse(value:'barcelona'), new PollResponse(value:'Unknown')]).save(failOnError:true, flush:true)
		then:
			p.name == 'Football Teams'
			p.responses.size() == 3
			PollResponse.findByValue('manchester')
	}
}
