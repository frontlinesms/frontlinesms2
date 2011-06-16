package frontlinesms2.search

import frontlinesms2.*

class SearchSpec extends grails.plugin.geb.GebSpec{
	
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
			createTestPollsAndFolders()
		when:
			to SearchPage
		then:
			$("#search-details").find('select', name:'groupList').children().collect() { it.text() } == ['Select group','Listeners', 'Friends']
			$("#search-details").find('select', name:'activityList').children().collect() { it.text() } == ['Select activity / folder', "Miauow Mix", 'Work']
		cleanup:
			deleteTestGroups()
			deleteTestPollsAndFolders()
	}
	
	def "search description is shown in header"() {
		given:
			createTestGroups()
			createTestPollsAndFolders()
		when:
			to SearchPage
		then:
			$('#search-description').text() == 'Start new search on the left'
		when:
			searchFrm.keywords = "test"
			searchBtn.click()
		then:
			$('#search-description').text() == 'Searching in all messages'
		when:
			searchFrm.keywords = "test"
			$("#search-details").find('select', name:'groupList').value("${Group.findByName("Listeners").id}")
			$("#search-details").find('select', name:'activityList').value("${Poll.findByTitle("Miauow Mix").id}")
			searchBtn.click()
		then:
			$('#search-description').text() == "Searching in 'Listeners' and 'Miauow Mix'"
		cleanup:
			deleteTestGroups()
			deleteTestPollsAndFolders()
	}
	
	def "message list returned from a search operation is displayed"() {
		given:
			createTestMessages()
		when:
			searchFrm.keywords = "alex"
			searchBtn.click()
			def rowContents = $('#messages tbody tr:nth-child(1) td')*.text()
		then:
			rowContents[0] == 'Alex'
			rowContents[1] == 'hi alex'
			rowContents[2] ==~ /[0-9]{2}-[A-Z][a-z]{2}-[0-9]{4} [0-9]{2}:[0-9]{2}/
		cleanup:
			deleteTestMessages()
	}
	
//	def 'message actions menu is displayed for all individual messages'() {
//		given:
//			createTestMessages()
//		when:
//			searchFrm.keywords = "Bob"
//			searchBtn.click()
//			def rowContents = $('#messages tbody tr:nth-child(1) td')*.text()
//			def actions = $('#message-actions li').children('a')*.text()
//		then:
//			actions[0] == 'Miauow Mix'
//		cleanup:
//			deleteTestMessages()
//	}
//	
	
	def createTestGroups() {
		new Group(name: 'Listeners').save(flush: true)
		new Group(name: 'Friends').save(flush: true)
	}
	
	static createTestMessages() {
		[new Fmessage(src:'Doe', dst:'+254987654', text:'meeting at 11.00'),
			new Fmessage(src:'Alex', dst:'+254987654', text:'hi alex')].each() {
					it.inbound = true
					it.save(failOnError:true)
				}
	}
	
	static createTestPollsAndFolders() {
		def chickenResponse = new PollResponse(value:'chicken')
		def liverResponse = new PollResponse(value:'liver')
		Poll p = new Poll(title:'Miauow Mix', responses:[chickenResponse, liverResponse]).save(failOnError:true, flush:true)
		Folder f = new Folder(value: "Work").save(failOnError:true, flush:true)
	}
	static deleteTestPollsAndFolders() {
		Poll.findAll()*.delete(flush:true, failOnError:true)
		Folder.findAll()*.delete(flush:true, failOnError:true)
	}
	
	static deleteTestMessages() {
		Fmessage.findAll()*.delete(flush:true, failOnError:true)
	}
	
	static deleteTestGroups() {
		Group.findAll()*.delete(flush:true, failOnError:true)
	}
}

class SearchPage extends geb.Page {
	static url = 'search/list'
	static at = {
		title.startsWith('Search')
	}
	static content = {
		searchFrm(required: false) { $('#search-details') }
		searchBtn(required: false) { $('.buttons .search') }
	}
}