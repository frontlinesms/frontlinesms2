package frontlinesms2.search

import frontlinesms2.*

class SearchSpec extends grails.plugin.geb.GebSpec {
	def setup() {
		createTestGroups()
		createTestPollsAndFolders()
		createTestMessages()
	}
	
	def "clicking on the search button links to the result show page"() {
		setup:
			new Fmessage(src: "src", text:"sent", dst: "dst", status: MessageStatus.SENT, dateReceived: new Date()-1).save(flush: true)
			new Fmessage(src: "src", text:"send_pending", dst: "dst", status: MessageStatus.SEND_PENDING, dateReceived: new Date()-1).save(flush: true)
			new Fmessage(src: "src", text:"send_failed", dst: "dst", status: MessageStatus.SEND_FAILED, dateReceived: new Date()-1).save(flush: true)
		when:
			to SearchingPage
			searchBtn.present()
			searchBtn.click()
		then:
			at SearchingPage
			$("table#messages tbody tr").collect {it.find("td:nth-child(4)").text()}.containsAll(['hi alex',
																'meeting at 11.00', 'sent', 'send_pending', 'send_failed'])
	}
	
	def "group list and activity lists are displayed when they exist"() {
		when:
			to SearchingPage
		then:
			searchFrm.find('select', name:'groupId').children().collect() { it.text() } == ['Select group','Listeners', 'Friends']
			searchFrm.find('select', name:'activityId').children().collect() { it.text() } == ['Select activity / folder', "Miauow Mix", 'Work']
	}
	
	def "search description is shown in header when searching by group"() {
		when:
			to SearchingPage
			searchBtn.present()
			searchFrm.searchString = "test"
			searchBtn.click()
		then:
			waitFor {searchDescription}
			searchDescription.text() == 'Searching "test", include archived messages'
	}
	
	def "search string is still shown on form submit and consequent page reload"() {
		given:
			to SearchingPage
			searchFrm.searchString = 'bacon'
		when:
			searchBtn.click()
		then:
			searchFrm.searchString == 'bacon'
	}
	
	def "selected activity is still selected on form submit and consequent page reload"() {
		given:
			to SearchingPage
			def a = Folder.findByName("Work")
			searchFrm.activityId = "folder-${a.id}"
		when:
			searchBtn.click()
		then:
			searchFrm.activityId == ["folder-${a.id}"]
	}
	
	def "can search in archive or not, is enabled by default"() {
		when:
			to SearchingPage
		then:
			searchFrm.inArchive == 'on'
		when:
			searchFrm.inArchive = null
			searchBtn.click()
		then:
			searchFrm.inArchive == null
	}
	
	def "'Export Results' link is disabled when search is null "() {
		when:
			to SearchingPage
		then:
			!$('h2:nth-child(2) div#export-results a').present();
	}

	def "should fetch all inbound messages alone"() {
		given:
			to SearchingPage
			searchFrm.messageStatus = "INBOUND"
		when:
			searchBtn.click()
			sleep(2000)
			waitFor{searchBtn.displayed}
		then:
			searchFrm.messageStatus == ['INBOUND']
			$("table#messages tbody tr").collect {it.find("td:nth-child(4)").text()}.containsAll(['hi alex', 'meeting at 11.00'])
	}
	
	def "should fetch all sent messages alone"() {
		given:
			new Fmessage(src: "src", text:"sent", dst: "dst", status: MessageStatus.SENT, dateReceived: new Date()-1).save(flush: true)
			new Fmessage(src: "src", text:"send_pending", dst: "dst", status: MessageStatus.SEND_PENDING, dateReceived: new Date()-1).save(flush: true)
			new Fmessage(src: "src", text:"send_failed", dst: "dst", status: MessageStatus.SEND_FAILED, dateReceived: new Date()-1).save(flush: true)
			to SearchingPage
			searchFrm.messageStatus = "SENT, SEND_PENDING, SEND_FAILED"
		when:
			searchBtn.click()
			sleep(2000)
			waitFor{searchBtn.displayed}
		then:
			searchFrm.messageStatus == ['SENT, SEND_PENDING, SEND_FAILED']
			$("table#messages tbody tr").collect {it.find("td:nth-child(4)").text()}.containsAll(["sent", "send_pending", "send_failed"]) 
	}
	
	//@spock.lang.IgnoreRest
	def "should clear search results" () {
		when:
			to SearchingPage
			searchBtn.present()
			searchBtn.click()
			waitFor{searchBtn.displayed}
			$("a", text:"Clear search").click()
		then:
			waitFor{ !$("#search-description").displayed }
	}
	
	def "should return to the same search results when message is deleted" () {
		setup:
			new Fmessage(src: "src", text:"sent", dst: "dst", status: MessageStatus.SENT).save(flush: true)
			new Fmessage(src: "src", text:"send_pending", dst: "dst", status: MessageStatus.SEND_PENDING).save(flush: true)
			new Fmessage(src: "src", text:"send_failed", dst: "dst", status: MessageStatus.SEND_FAILED).save(flush: true)
		when:
			to SearchingPage
			searchBtn.present()
			searchBtn.click()
		then:
			at SearchingPage
		when:
			$("table#messages tbody tr:nth-child(3) td:nth-child(3)").click()
			$("#message-delete").click()
		then:
			at SearchingPage
			$("table#messages tbody tr").collect {it.find("td:nth-child(4)").text()}.containsAll(['hi alex', 'sent', 'send_pending', 'meeting at 11.00'])
			waitFor { $('.flash').displayed }
			
	}
	
//	@spock.lang.IgnoreRest
	def "should have the start date not set, then as the user set one the result page should contain his start date"(){
		when:
			to SearchingPage
			searchBtn.present()
		then:
			searchFrm.startDate_day == ['none']
			searchFrm.startDate_month == ['none']
			searchFrm.startDate_year == ['none']
		when:
			searchFrm.startDate_day = '4'
			searchFrm.startDate_month = '9'
			searchFrm.startDate_year = '2010'
			searchBtn.click()
			waitFor {searchDescription}
		then:
			searchFrm.startDate_day == ['4']
			searchFrm.startDate_month == ['9']
			searchFrm.startDate_year == ['2010']
	}
	
//	@spock.lang.IgnoreRest
	def "should expand the more option and select a contactName then the link to add contactName is hiden"(){
		when:
		createTestContactsAndCustomFieldsAndMessages()
		to SearchingPage
		searchMoreOptionLink.click()
	then:
		waitFor { expendedSearchOption.displayed }
		contactNameLink.displayed
		townCustomFieldLink.displayed
		likeCustomFieldLink.displayed
		ikCustomFieldLink.displayed
	when:
		contactNameLink.click()
		//SearchBtn.click()
	then:
		waitFor { contactNameField.displayed }
		!expendedSearchOption.displayed
	when:
		searchMoreOptionLink.click()
	then:
		waitFor { expendedSearchOption.displayed }
		!contactNameLink.displayed
	}

	def "should expand the more option and select a customField then the link to custom field is hiden"(){
		when:
		createTestContactsAndCustomFieldsAndMessages()
		to SearchingPage
		searchMoreOptionLink.click()
	then:
		waitFor { expendedSearchOption.displayed }
		contactNameLink.displayed
		townCustomFieldLink.displayed
		likeCustomFieldLink.displayed
		ikCustomFieldLink.displayed
	when:
		townCustomFieldLink.click()
	then:
		waitFor { townCustomFieldField.displayed }
	when:
		searchMoreOptionLink.click()
	then:
		waitFor { expendedSearchOption.displayed }
		!townCustomFieldLink.displayed
	}
	
	def "should show the contact name that have been fillin after a search"(){
		when:
			createTestContactsAndCustomFieldsAndMessages()
			to SearchingPage
			searchMoreOptionLink.click()
			waitFor { expendedSearchOption.displayed }
			contactNameLink.click()
			waitFor { contactNameField.displayed }
			searchFrm.contactString = "toto"
			searchBtn.click()
		then:
			waitFor { contactNameField.displayed }
			searchFrm.contactString == "toto"		
	}
	
	
	def "when clicking on a remove button on a more search option, the field should be hiden and cleared then the link should appear"(){
		when:
			createTestContactsAndCustomFieldsAndMessages()
			to SearchingPage
			searchMoreOptionLink.click()
			waitFor { expendedSearchOption.displayed }
			contactNameLink.click()
			waitFor { contactNameField.displayed }
			searchFrm.contactString = "toto"
			contactNameField.children('a').click()
			waitFor { !contactNameField.displayed }
			searchMoreOptionLink.click()
			waitFor {contactNameLink.displayed }
			contactNameLink.click()
		then:
			waitFor { contactNameField.displayed }
			searchFrm.contactString == null
	}
	
	
	private createTestGroups() {
		new Group(name: 'Listeners').save(flush: true)
		new Group(name: 'Friends').save(flush: true)
	}
	
	private createTestMessages() {
		[new Fmessage(src:'Doe', dst:'+254987654', text:'meeting at 11.00', dateReceived: new Date()-1),
				new Fmessage(src:'Alex', dst:'+254987654', text:'hi alex', dateReceived: new Date()-1)].each() {
			it.status = MessageStatus.INBOUND
			it.save(failOnError:true)
		}
	}
	
	private createTestPollsAndFolders() {
		def chickenResponse = new PollResponse(value:'chicken')
		def liverResponse = new PollResponse(value:'liver')
		new Fmessage(src:'Joe', dst:'+254987654', text:'eat more cow', messageOwner:'chickenResponse')
		Poll p = new Poll(title:'Miauow Mix', responses:[chickenResponse, liverResponse]).save(failOnError:true, flush:true)
		Folder f = new Folder(name: "Work").save(failOnError:true, flush:true)
		
	}
	
	private createTestContactsAndCustomFieldsAndMessages(){
		def firstContact = new Contact(name:'Alex', primaryMobile:'+254987654').save(failOnError:true)
		def secondContact = new Contact(name:'Mark', primaryMobile:'+254333222').save(failOnError:true)
		def thirdContact = new Contact(name:"Toto", primaryMobile:'+666666666').save(failOnError:true)
		
		[new CustomField(name:'town', value:'Paris', contact: firstContact),
			new CustomField(name:'like', value:'cake', contact: secondContact),
			new CustomField(name:'ik', value:'car', contact: secondContact),
			new CustomField(name:'like', value:'ake', contact: thirdContact),
			new Fmessage(src:'+666666666', dst:'+2549', text:'finaly i stay in bed', status:MessageStatus.INBOUND)].each {
		it.save(failOnError:true)
		}
	}
}

class SearchingPage extends geb.Page {
	static url = 'search'
	static at = {
		title.startsWith('Results')
	}
	static content = {
		searchFrm { $('#search-details') }
		searchBtn { $('#search-details .buttons .search') }
		searchDescription { $('#search-description') }
		searchMoreOptionLink { $('#more-search-options')}
		townCustomFieldLink(required:false) { $('#more-option-link-custom-field-town')}
		townCustomFieldField(required:false) { $('#more-option-field-custom-field-town')}
		likeCustomFieldLink(required:false) { $('#more-option-link-custom-field-like')}
		ikCustomFieldLink(required:false) { $('#more-option-link-custom-field-ik')}
		contactNameLink(required:false) {$('#more-option-link-contact-name')}
		contactNameField(required:false) {$('#more-option-field-contact-name')}
		expendedSearchOption(required:false) {$('#expanded-search-options')}
	}
}
