package frontlinesms2.search

class SearchSpec extends SearchGebSpec{
	
	def "the 'search form' is visible within the search page view"(){
		when:
			to SearchPage
		then:
			$('#search-details').present
			$('#btnSearch').present
	}
	
	def 'keywords found within message content return a list of related messages'() {
		given:
			createSearchTestMessages()
		when:
			to SearchPage
			def searchFrm = $('#search-details')
			def searchBtn = $('#btnSearch')
			searchFrm.keyword = "meet"
			searchBtn.click()
		then:
			at SearchPage
			def messageSources = $('#messages tbody tr td:first-child')*.text()
		then:
			messageSources == ['Alex', 'Michael']
		cleanup:
			deleteTestMessages()
	}
	
	def "a notification message is shown when no messages are found containing the given keywords"() {
		
	}
	
	def "messageContent search operation returns a valid response for any given keyword"() {
		
	}
	
	def "message searches can be restricted to a contact group"() {
		
	}
	
	def "a list of existing activities and contact groups is displayed"() {
		
	}
	
	def "message searches can be restricted to an activity"() {
		
	}
	
	def "message searches can be restricted to both contact groups and activities"() {
		
	}
}

class SearchPage extends geb.Page {
	static url = 'search'
	static content = {
//		selectedMenuItem { $('#messages-menu .selected') }
		messagesList { $('#messages') }
	}
}