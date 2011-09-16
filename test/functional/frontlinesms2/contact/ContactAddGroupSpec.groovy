package frontlinesms2.contact

import frontlinesms2.*
import geb.navigator.EmptyNavigator

class ContactAddGroupSpec extends ContactGebSpec {

	def setup() {
		createTestContacts()
		createTestGroups()
	}
	
	def 'groups that selected contact belongs to are shown in contact details'() {
		when:
			to BobsContactPage
		then:
			def memberOf = $("#group-list li").children('input')*.value().sort()
			memberOf == ['Test', 'three']
	}

	def 'existing groups that contact is not a member of can be selected from dropdown and are then added to list'() {
		when:
			to BobsContactPage
		then:
			$("#group-dropdown").children()*.text().sort() == ['Add to group...', 'Others', 'four']
		when:
			$("#group-dropdown").value("${Group.findByName('Others').id}")
		then:
			$("#group-list").children().children('input')*.value().sort() == ['Others', 'Test', 'three']
			$("#group-dropdown").children()*.text() == ['Add to group...', 'four']
	}

	def 'clicking X next to group in list removes group from visible list, but does not change database iff no other action is taken'() {
		given:
			def bob = Contact.findByName("Bob")
			def bobsDatabaseGroups = bob.groups
			def bobsGroups = bobsDatabaseGroups
		when:
			go "contact/show/${bob.id}"
			def lstGroups = $("#group-list")
		then:
			lstGroups.children().children('input').size() == 2
			def groupsText = lstGroups.children().children('input').collect() { it.value() }
			groupsText.containsAll(['Test', 'three'])
		when:
			lstGroups.find('a').first().click()
			bobsGroups = bob.groups
		then:
			lstGroups.children().children('input').size() == 1
			lstGroups.children().children('input').value() == groupsText[1]
			bobsGroups == bobsDatabaseGroups

		when:
			lstGroups.find('a').first().click()
			bobsGroups = bob.getGroups()
		then:
			lstGroups.children().children('input').size() == 0
			bobsGroups == bobsDatabaseGroups
			$("#group-list").text() == 'Not part of any Groups'
			bobsGroups == bobsDatabaseGroups
	}

	def 'clicking save actually adds contact to newly selected groups'() {
		when:
			to BobsContactPage
			$('#contact-details select', name:'group-dropdown').value('Others')
			$("#contact-details .save").click()
		then:
			at ContactListPage
			Contact.findByName('Bob') in Group.findByName('Test').members
	}
	
	def 'clicking save actually adds multiple contacts to newly selected groups'() {
		when:
			to ContactListPage
			contactSelect[1].click()
		then:
			waitFor { $('input', name:'name').value() == 'Bob' }
		when:
			contactSelect[0].click()
		then:
			waitFor { multiGroupSelect.displayed && multiGroupSelect.find('option').size() > 1 }
		when:
			multiGroupSelect.value("${Group.findByName('Others').id}")
			updateAll.click()
		then:
			Group.findByName('Others').members*.name.containsAll(['Bob', 'Alice'])
	}
	
	def 'clicking save removes multiple contacts from selected groups'() {
		given:
			def bob = Contact.findByName("Bob")
			def alice = Contact.findByName('Alice')
			def otherGroup = Group.findByName('Others')
			bob.addToGroups(otherGroup,true)
			alice.addToGroups(otherGroup,true)
			assert bob.isMemberOf(otherGroup)
			assert alice.isMemberOf(otherGroup)
		when:
			to ContactListPage
			contactSelect[1].click()
		then:
			waitFor { $('input', name:'name').value() == 'Bob' }
		when:
			contactSelect[0].click()
		then:
			waitFor { $("#multi-group-list #remove-group-${otherGroup.id}").displayed }
		when:
			$("#multi-group-list #remove-group-${otherGroup.id}").click()
			updateAll.click()
		then:
			waitFor { flashMessage.displayed }
		when:
			otherGroup.refresh()
		then:
			otherGroup.members == []
	}
	
	def 'clicking save removes contact from newly removed groups'() {
		when:
			def otherGroup = Group.findByName('Others')
			to BobsContactPage
			def btnRemoveFromGroup = $("#remove-group-${otherGroup.id}")
			btnRemoveFromGroup.click()
			def btnUpdate = $("#single-contact #update-single")
			btnUpdate.click()
		then:
			at ContactListPage
			otherGroup.refresh()
			GroupMembership.countMembers(otherGroup) == 0
	}

	// TODO test cancel button - remove from 1 group
	// TODO test cancel button - add to one group
}

class BobsContactPage extends geb.Page {
	static def getUrl() { "contact/show/${Contact.findByName("Bob").id}" }
	static content = {
		selectedMenuItem { $('#contacts-menu .selected') }
		groupsList { $('#groups-submenu') }
	}
}
