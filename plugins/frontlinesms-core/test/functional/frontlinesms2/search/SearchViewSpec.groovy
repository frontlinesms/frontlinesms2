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
		when:
			to PageNewSearch
			waitFor("veryslow") { searchsidebar.searchBtn.displayed }
			searchsidebar.searchBtn.click()
		then:
			waitFor("veryslow") { messageList.messages.size() == 3 }
	}
	
	def "group list and activity lists are displayed when they exist"() {
		when:
			to PageNewSearch
		then:
			searchsidebar.searchForm.find('select', name:'groupId').children('option')*.text() == ['Select group', 'Friends', 'Listeners']
			searchsidebar.searchForm.find('select', name:'activityId').children('option')*.text() == ['Select activity/folder', "Miauow Mix poll", 'Work folder']
	}
	
	def "search description is shown in header"() {
		when:
			to PageNewSearch
			searchsidebar.searchBtn.displayed
			searchsidebar.searchBtn.click()
		then:
			waitFor('veryslow') { header.searchDescription == 'Searching all messages, including archived messages' }
	}

	def "search string is still shown on form submit and consequent page reload"() {
		given:
			to PageNewSearch
			searchsidebar.searchField = 'bacon'
		when:
			searchsidebar.searchBtn.click()
		then:
			waitFor("veryslow") { header.searchDescription.contains('bacon') }
			waitFor { searchsidebar.searchString == "bacon" }
	}
	
	def "selected activity is still selected on form submit and consequent page reload"() {
		given:
			to PageNewSearch
			def a = Folder.findByName("Work")
			searchsidebar.activityId = "folder-$a.id"
		when:
			searchsidebar.searchBtn.click()
		then:
			waitFor("veryslow") { searchsidebar.activityId.jquery.val() == "folder-$a.id" }
	}
	
	def "can search in archive or not, is enabled by default"() {
		when:
			to PageNewSearch
		then:
			searchsidebar.inArchive == "on"
		when:
			searchsidebar.archive.click()
			searchsidebar.searchBtn.click()
		then:
			!searchsidebar.inArchive
	}

	def "'Export Results' link is disabled when search is null "() {
		when:
			to PageNewSearch
		then:
			header.export.hasClass('disabled');
	}

	def "should fetch all inbound messages alone"() {
		given:
			to PageNewSearch
			searchsidebar.messageStatus = "inbound"
		when:
			searchsidebar.searchBtn.click()
		then:	
			waitFor { searchsidebar.searchBtn.displayed }
			searchsidebar.messageStatus == 'inbound'
			messageList.messages.text.containsAll(['hi alex', 'meeting at 11.00'])
	}
	
	def "should fetch all sent messages alone"() {
		given:
			def m1 = new Fmessage(src:"src", text:"sent", date:new Date()-1)
			m1.addToDispatches(dst:'123', status: DispatchStatus.SENT, dateSent:new Date())
			m1.save(failOnError:true, flush:true)

			def m2 = new Fmessage(src: "src", text:"send_pending", date:new Date()-1)
			m2.addToDispatches(dst:'123', status:DispatchStatus.PENDING)
			m2.save(failOnError:true, flush:true)

			def m3 = new Fmessage(src: "src", text:"send_failed", date:new Date()-1)
			m3.addToDispatches(dst:'123', status:DispatchStatus.FAILED)
			m3.save(failOnError:true, flush:true)

			Fmessage.build(text:"received", date:new Date()-1)
			
			to PageNewSearch
			searchsidebar.messageStatus = "outbound"
		when:
			searchsidebar.searchBtn.click()
		then:
			waitFor{ searchsidebar.searchBtn.displayed }
			searchsidebar.messageStatus == 'outbound'
			messageList.messages.size() == 3
	}

	def "should clear search results" () {
		when:
			to PageNewSearch
			searchsidebar.searchBtn.displayed
			searchsidebar.searchBtn.click()
		then:
			waitFor{ searchsidebar.searchBtn.displayed }
		when:
			searchsidebar.clearSearchLink.click()
		then:
			waitFor { messageList.noContent.displayed }
	}

	def "should return to the same search results when message is deleted" () {
		setup:
			Fmessage.build(src:"src", text:"received1")
			Fmessage.build(src:"src", text:"received2", date:new Date()-1)
			Fmessage.build(src:"src3", text:"send_failed", date:new Date()-2)
		when:
			to PageNewSearch
			searchsidebar.searchBtn.displayed
			searchsidebar.searchBtn.click()
		then:
			waitFor { messageList.messages.size() == 6 }
			messageList.messages[0].checkbox.click()
			waitFor("veryslow") { singleMessageDetails.text == "received1" }
			singleMessageDetails.delete.click()
		then:
			waitFor("veryslow") { messageList.messages.size() == 5 }
			notifications.flashMessage.text().contains("Message moved to trash")
	}

	def "should have the start date not set, then as the user set one the result page should contain his start date"() {
		when:
			def date = new Date()
			to PageNewSearch
			searchsidebar.searchBtn.displayed
		then:
			searchsidebar.startDate_day == 'none'
			searchsidebar.startDate_month == 'none'
			searchsidebar.startDate_year == 'none'
			searchsidebar.endDate_day == 'none'
			searchsidebar.endDate_month == 'none'
			searchsidebar.endDate_year == 'none'
		when:
			searchsidebar.startDate_day = '4'
			searchsidebar.startDate_month = '9'
			searchsidebar.startDate_year = '2010'
			searchsidebar.datePickerDiv.jquery.hide()
			waitFor { !searchsidebar.datePickerDiv.displayed }
			searchsidebar.searchBtn.click()
			waitFor { header.searchDescription.contains("Searching all messages, including archived messages") }
		then:
			searchsidebar.startDate_day == '4'
			searchsidebar.startDate_month == '9'
			searchsidebar.startDate_year == '2010'
	}

	def "archiving message should not break message navigation "() {
		when:
			to PageSearchResult, "alex"
		then:
			messageList.messages[0].checkbox.click()
			waitFor { singleMessageDetails.text == "hi alex" }
		when:
			singleMessageDetails.archive.click()
		then:
			waitFor { searchsidebar.searchString == "alex" }
	}

	def "should expand the more option and select a contactName then the link to add contactName is hidden"() {
		when:
			createTestContactsAndCustomFieldsAndMessages()
			to PageSearchResult, ""
			searchsidebar.searchMoreOptionLink.click()
		then:
			waitFor { searchsidebar.expandedSearchOption.displayed }
			searchsidebar.contactNameLink.displayed
			searchsidebar.addTownCustomFieldLink.displayed
			searchsidebar.likeCustomFieldLink.displayed
			searchsidebar.ikCustomFieldLink.displayed
		when:
			searchsidebar.contactNameLink.click()
		then:
			waitFor { searchsidebar.contactNameField.displayed }
			!searchsidebar.contactNameLink.displayed
	}

	def "Can select a customField in More options"() {
		when:
			createTestContactsAndCustomFieldsAndMessages()
			to PageSearchResult, ""
			searchsidebar.searchMoreOptionLink.click()
		then:
			waitFor { searchsidebar.expandedSearchOption.displayed }
		when:
			searchsidebar.addTownCustomFieldLink.click()
		then:
			waitFor { searchsidebar.townCustomField.displayed }
			!searchsidebar.addTownCustomFieldLink.displayed
	}

	def "should show the contact name that has been filled in after a search"(){
		given:
			createTestContactsAndCustomFieldsAndMessages()
		when:
			to PageSearchResult, ""
			searchsidebar.searchMoreOptionLink.click()
		then:
			waitFor { searchsidebar.expandedSearchOption.displayed }
		when:
			searchsidebar.contactNameLink.click()
		then:
			waitFor { searchsidebar.contactNameField.displayed }
		when:
			searchsidebar.contactString = "toto"
			searchsidebar.searchBtn.click()
		then:
			waitFor { searchsidebar.contactNameField.displayed }
			searchsidebar.contactString == "toto"
	}
	
	
	def "when clicking on a remove button on a more search option, the field should be hidden and cleared then the link should appear"() {
		given:
			createTestContactsAndCustomFieldsAndMessages()
		when:
			to PageSearchResult, ""
			searchsidebar.searchMoreOptionLink.click()
		then:
			waitFor { searchsidebar.expandedSearchOption.displayed }
		when:
			searchsidebar.contactNameLink.click()
		then:
			waitFor { searchsidebar.contactNameField.displayed }
		when:
			searchsidebar.contactString = "toto"
			searchsidebar.contactNameField.children('a').click()
		then:
			waitFor { !searchsidebar.contactNameField.displayed }
			searchsidebar.contactNameLink.displayed
		when:
			searchsidebar.contactNameLink.click()
		then:
			waitFor { searchsidebar.contactNameField.displayed }
			!searchsidebar.contactString
	}
	
	def "should update message count when in search tab"() {
		when:
			to PageSearchResult, ""
			def message = Fmessage.build(src:'+254999999', text:'message count')
		then:
			tabs.unreadcount == 2
		when:
			js.refreshMessageCount()
		then:
			waitFor('very slow') { tabs.unreadcount == 3 }
	}

	def "moveaction drop down should not be visible if only one archived message is seleted"(){
		when:
			def m2 = Fmessage.build(src:'+25499934', text:'archived2')
			def m1 = Fmessage.build(src:'+25499912', text:'archived1', archived:true)
			to PageSearchResult, "archived", "inArchive=true"
		then:
			messageList.messages[0].checkbox.click()
		when:
			waitFor { singleMessageDetails.text == "archived1" }
		then:
			!singleMessageDetails.single_moveActions.displayed
		when:
			messageList.messages[1].checkbox.click()
		then:
			multipleMessageDetails.multiple_moveActions.displayed
	}

	def "ensure dispatch count in message results is correct"(){
		given:
			def message = new Fmessage(text:"experiment")
			message.addToDispatches(dst:'333', status:DispatchStatus.PENDING)
			message.addToDispatches(dst:'332', status:DispatchStatus.PENDING)
			message.addToDispatches(dst:'222', status:DispatchStatus.PENDING)
			message.save(flush:true, failOnError:true)

			to PageNewSearch
			searchsidebar.searchField = '33'
		when:
			searchsidebar.searchBtn.click()
		then:
			waitFor{ searchsidebar.searchBtn.displayed }
			messageList.messages.size() == 1
			messageList.messages[0].text == 'experiment'
			messageList.messages[0].source == 'To: 3 recipients'
	}
}
