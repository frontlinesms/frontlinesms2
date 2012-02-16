package frontlinesms2.search

import frontlinesms2.*
import org.openqa.selenium.Keys

class SearchViewSpec extends SearchBaseSpec {
	def setup() {
		createTestGroups()
		createTestPollsAndFolders()
		createTestMessages2()
	}
	
	def "clicking on the search button links to the result show page"() {
		setup:
			new Fmessage(src:"src", text:"sent", inbound:true, date:new Date()-1).save(flush: true)
			new Fmessage(src:"src", text:"send_pending", inbound:true, date:new Date()-1).save(flush: true)
			new Fmessage(src:"src", text:"send_failed", inbound:true, date:new Date()-1).save(flush: true)
		when:
			to PageSearch
			searchBtn.present()
			searchBtn.click()
		then:
			at PageSearchResult
			$("#messages tbody tr td:nth-child(5)")*.text().containsAll(['hi alex',
					'meeting at 11.00', 'sent', 'send_pending', 'send_failed'])
	}
	
	def "group list and activity lists are displayed when they exist"() {
		when:
			to PageSearch
		then:
			searchFrm.find('select', name:'groupId').children('option')*.text() == ['Select group','Listeners', 'Friends']
			searchFrm.find('select', name:'activityId').children('option')*.text() == ['Select activity / folder', "Miauow Mix", 'Work']
	}
	
	def "search description is shown in header"() {
		when:
			to PageSearch
			searchBtn.present()
			searchBtn.click()
		then:
			waitFor {searchDescription}
			searchDescription.text().contains('Searching all messages, including archived messages')
	}
	
	def "search string is still shown on form submit and consequent page reload"() {
		given:
			to PageSearch
			searchFrm.searchString = 'bacon'
		when:
			searchBtn.click()
		then:
			searchFrm.searchString == 'bacon'
	}
	
	def "selected activity is still selected on form submit and consequent page reload"() {
		given:
			to PageSearch
			def a = Folder.findByName("Work")
			searchFrm.activityId = "folder-$a.id"
		when:
			searchBtn.click()
		then:
			searchFrm.activityId == "folder-$a.id"
	}
	
	def "can search in archive or not, is enabled by default"() {
		when:
			to PageSearch
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
			to PageSearch
		then:
			!$('h2:nth-child(2) div#export-results a').present()
	}

	def "should fetch all inbound messages alone"() {
		given:
			to PageSearch
			searchFrm.messageStatus = "inbound"
		when:
			searchBtn.click()
		then:	
			waitFor { searchBtn.displayed }
			searchFrm.messageStatus == 'inbound'
			$("#messages tbody tr .message-text-cell a")*.text().containsAll(['hi alex', 'meeting at 11.00'])
	}
	
	def "should fetch all sent messages alone"() {
		given:
			def d1 = new Dispatch(dst: '123', status: DispatchStatus.SENT, dateSent: new Date())
			def d2 = new Dispatch(dst: '123', status: DispatchStatus.PENDING)
			def d3 = new Dispatch(dst: '123', status: DispatchStatus.FAILED)
			def m1 = new Fmessage(src: "src", text:"sent", hasSent:true, date: new Date()-1)
			m1.addToDispatches(d1)
			m1.save(flush: true)
			def m2 = new Fmessage(src: "src", text:"send_pending", hasPending:true, date: new Date()-1)
			m2.addToDispatches(d2)
			m2.save(flush: true)
			def m3 = new Fmessage(src: "src", text:"send_failed", hasFailed:true, date: new Date()-1)
			m3.addToDispatches(d3)
			m3.save(flush: true)
			new Fmessage(src: "src", text:"send_failed", inbound:true, date: new Date()-1).save(flush: true)
			
			to PageSearch
			searchFrm.messageStatus = "outbound"
		when:
			searchBtn.click()
		then:
			waitFor{ searchBtn.displayed }
			searchFrm.messageStatus == 'outbound'
			$("#messages tbody tr .message-text-cell a")*.text().containsAll(["sent", "send_pending", "send_failed"]) 
	}
	
	def "should clear search results" () {
		when:
			to PageSearch
			searchBtn.present()
			searchBtn.click()
		then:
			waitFor{ searchBtn.displayed }
		when:
			$("a", text:"Clear search").click()
		then:
			waitFor{ !$("#search-description").displayed }
	}
	
	def "should return to the same search results when message is deleted" () {
		setup:
			new Fmessage(src: "src", text:"sent", date: new Date(), inbound:true).save(flush: true)
			new Fmessage(src: "src", text:"send_pending", date: new Date()-1, inbound:true).save(flush: true)
			new Fmessage(src: "src3", text:"send_failed", date: new Date()-2, inbound:true).save(flush: true)
		when:
			to PageSearch
			searchBtn.present()
			searchBtn.click()
		then:
			at PageSearchResult
		when:
			$("a.displayName-${Fmessage.findBySrc('src3').id}").click()
			$("#delete-msg").click()
		then:
			at PageSearchResult
			$("#messages tbody tr .message-text-cell a")*.text().containsAll(["hi alex", "sent", "send_pending", "meeting at 11.00"])
			$('.flash').displayed
	}
	
	def "should have the start date not set, then as the user set one the result page should contain his start date"(){
		when:
			def date = new Date()
			to PageSearch
			searchBtn.present()
		then:
			searchFrm.startDate_day == 'none'
			searchFrm.startDate_month == 'none'
			searchFrm.startDate_year == 'none'
			searchFrm.endDate_day == 'none'
			searchFrm.endDate_month == 'none'
			searchFrm.endDate_year == 'none'
		when:
			 searchFrm.startDate_day = '4'
			 searchFrm.startDate_month = '9'
			 searchFrm.startDate_year = '2010'
			$("#ui-datepicker-div").jquery.hide()
			waitFor { !$("#ui-datepicker-div").displayed }
			searchBtn.click()
			waitFor {searchDescription}
		then:
			searchFrm.startDate_day == '4'
			searchFrm.startDate_month == '9'
			searchFrm.startDate_year == '2010'
	}
	
	def "archiving message should not break message navigation "() {
		when:
			to PageSearch
			searchBtn.present()
			searchBtn.click()
		then:
			at PageSearchResult
		when:
			$("a.displayName-${Fmessage.findByText('hi alex').id}").click()
			$("#archive-msg").click()
		then:
			at PageSearchResult
		when:
			$("a.displayName-${Fmessage.findByText('hi alex').id}").click()
		then:
			at PageSearchResult
			$("#message-detail-content").text() == 'hi alex'
	}
	
	def "should expand the more option and select a contactName then the link to add contactName is hidden"(){
		when:
			createTestContactsAndCustomFieldsAndMessages()
			to PageSearch
			searchMoreOptionLink.click()
		then:
			waitFor { expandedSearchOption.displayed }
			contactNameLink.displayed
			addTownCustomFieldLink.displayed
			likeCustomFieldLink.displayed
			ikCustomFieldLink.displayed
		when:
			contactNameLink.click()
		then:
			waitFor { contactNameField.displayed }
			!contactNameLink.displayed
	}

	def "Can select a customField in More options"(){
		when:
			createTestContactsAndCustomFieldsAndMessages()
			to PageSearch
			searchMoreOptionLink.click()
		then:
			waitFor { expandedSearchOption.displayed }
		when:
			addTownCustomFieldLink.click()
		then:
			waitFor { townCustomField.displayed }
			!addTownCustomFieldLink.displayed
	}
	
	def "should show the contact name that has been filled in after a search"(){
		given:
			createTestContactsAndCustomFieldsAndMessages()
		when:
			to PageSearch
			searchMoreOptionLink.click()
		then:
			waitFor { expandedSearchOption.displayed }
		when:
			contactNameLink.click()
		then:
			waitFor { contactNameField.displayed }
		when:
			searchFrm.contactString = "toto"
			searchBtn.click()
		then:
			waitFor { contactNameField.displayed }
			searchFrm.contactString == "toto"		
	}
	
	
	def "when clicking on a remove button on a more search option, the field should be hidden and cleared then the link should appear"() {
		given:
			createTestContactsAndCustomFieldsAndMessages()
		when:
			to PageSearch
			searchMoreOptionLink.click()
		then:
			waitFor { expandedSearchOption.displayed }
		when:
			contactNameLink.click()
		then:
			waitFor { contactNameField.displayed }
		when:
			searchFrm.contactString = "toto"
			contactNameField.children('a').click()
		then:
			waitFor { !contactNameField.displayed }
			contactNameLink.displayed
		when:
			contactNameLink.click()
		then:
			waitFor { contactNameField.displayed }
			!searchFrm.contactString
	}
	
	def "should update message count when in search tab"() {
		when:
			to PageSearch
			def message = new Fmessage(src:'+254999999', dst:'+254112233', text: "message count", inbound:true).save(flush: true, failOnError:true)
		then:
			$("#message-tab-link").text().equalsIgnoreCase("Messages\n2")
		when:
			js.refreshMessageCount()
		then:
			waitFor { $("#message-tab-link").text().equalsIgnoreCase("Messages\n3") }
	}
	
}


