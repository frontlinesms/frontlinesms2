package frontlinesms2.search

class SearchSpec extends SearchGebSpec{
	
	def "clicking on the search button links to the result show page"(){
		when:
			to SearchPage
			searchBtn.present()
			searchBtn.click()
		then:
			at SearchPage
	}
	
	def "group list and activity lists are displayed when they exist"() {
		given:
			createTestGroups()
			createTestMessages()
		when:
			to SearchPage
		then:
			//$('#group-dropdown').value("${Group.findByName('Listeners').id}")
			$("#search-details").find('select', name:'group-dropdown').children().collect() { it.text() } == ['Listeners', 'Friends']
			$("#search-details").find('select', name:'poll-dropdown').children().collect() { it.text() } == ["Miauow Mix"]
		cleanup:
			deleteTestGroups()
			deleteTestMessages()
	}
	
	def 'keywords found within message content return a list of related messages'() {
		given:
			createTestMessages()
		when:
			to SearchPage
			searchFrm.keywords = "meet"
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
	static url = 'search/show'
	static at = {
		title.startsWith('Search')
	}
	static content = {
		searchFrm(required: false) { $('#search-details') }
		searchBtn(required: false) { $('.buttons .search') }
	}
}