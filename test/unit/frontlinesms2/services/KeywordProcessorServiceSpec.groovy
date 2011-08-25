package frontlinesms2.services

import spock.lang.*
import grails.plugin.spock.*

class KeywordProcessorServiceSpec extends UnitSpec {
	def "test matches"() {
		given:
			// TODO create football poll
			def matchingMessageTexts = ['footballa', 'football a', ' football a', ' footballa   ', '''football
   a'''] // TODO add some difference cases
		when:
			def results = matchingMessageTexts.collect {
				service.matches(it)
			}
		then:
			results.each { r ->
				assert r
			}
	}
}

