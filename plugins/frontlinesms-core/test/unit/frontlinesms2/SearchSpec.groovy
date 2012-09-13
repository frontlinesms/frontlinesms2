package frontlinesms2

import spock.lang.*
import grails.test.mixin.*

@TestFor(Search)
class SearchSpec extends Specification {
	def 'check that name cannot be null'() {
		when:
			Search search = new Search(name: null)
		then:
			search.name != null || !search.validate()
	}
	
	def 'check that name cannot be blank'() {
		when:
			Search search = new Search(name: "")
		then:
			search.name != null || !search.validate() || search.name != ""
	}
	
	def 'check that searchString cannot be null'() {
		when:
			Search search = new Search(searchString: null)
		then:
			search.searchString != null || !search.validate()
	}
}

