package frontlinesms2

import spock.lang.*
import grails.plugin.spock.*

class SearchSpec extends UnitSpec {
	def 'check that name cannot be null'() {
		setup:
			mockForConstraintsTests(Search)
		when:
			Search search = new Search(name: null)
		then:
			search.name != null || !search.validate()
	}
	
	def 'check that name cannot be blank'() {
		setup:
			mockForConstraintsTests(Search)
		when:
			Search search = new Search(name: "")
		then:
			search.name != null || !search.validate() || search.name != ""
	}
	
	def 'check that searchString cannot be null'() {
		setup:
			mockForConstraintsTests(Search)
		when:
			Search search = new Search(searchString: null)
		then:
			search.searchString != null || !search.validate()
	}
	
}

