package frontlinesms2.contact

import frontlinesms2.*
import geb.navigator.EmptyNavigator

class ContactAddGroupSpec extends ContactGebSpec {

	def setup() {
		createTestContacts()
		createTestGroups()
	}
	
	def cleanup() {
		deleteTestGroups()
		deleteTestContacts()
	}

	def 'groups that selected contact belongs to are shown in contact details'() {
		given:
			def bob = Contact.findByName("Bob")
		when:
			go "contact/show/${bob.id}"
		then:
			def memberOf = $("#group-list li").children('input').collect() { it.value() }.sort()
			memberOf == ['Test', 'three']
	}

	def 'existing groups that contact is not a member of can be selected from dropdown and are then added to list'() {
		given:
			def bob = Contact.findByName("Bob")
		when:
			go "contact/show/${bob.id}"
			def groupSelecter = $("#contact-details").find('select', name:'group-dropdown')
			def nonMemberOf = groupSelecter.children().collect() { it.text() }.sort()
		then:
			nonMemberOf == ['Add to group...', 'Others', 'four']
			
		when:
			$('#group-dropdown').value("${Group.findByName('Others').id}")
			def updatedMemberOf = $("#group-list").children().children('input').collect() { it.value() }.sort()
		then:
			updatedMemberOf == ['Others', 'Test', 'three']
			assert groupSelecter.children().collect() { it.text() } == ['Add to group...', 'four']
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
		given:
			def bob = Contact.findByName("Bob")
		when:
			go "contact/show/${bob.id}"
			def groupSelecter = $("#contact-details").find('select', name:'group-dropdown')
			groupSelecter.find(name: 'group-dropdown').value('Others')
			$("#contact-details .save").click()
		then:
			at ContactListPage
			Group.findByName('Test').getMembers().contains(Contact.findByName('Bob'))
	}

	def 'clicking save removes contact from newly removed groups'() {
		when:
			to BobsContactPage
			def btnRemoveFromGroup = $("#group-list").find('a').first()
		    def groupDeletedFrom = $("#group-list").find('input').first().value()
			assert btnRemoveFromGroup != null && !(btnRemoveFromGroup instanceof EmptyNavigator)
			btnRemoveFromGroup.click()
			def btnUpdate = $("#contact-details .update")
			assert btnUpdate != null && !(btnUpdate instanceof EmptyNavigator)
			btnUpdate.click()
		then:
			at ContactListPage
			Group.findByName(groupDeletedFrom).refresh().members.size() == 0
	}

	// TODO test cancel button - remove from 1 group
	// TODO test cancel button - add to one group
}

// TODO use bob's contact page... once the tests are passing!
//
class BobsContactPage extends geb.Page {
	static bobby = Contact.findByName("Bob")
	static url = "contact/show/${bobby.id}"
	static content = {
		selectedMenuItem { $('#contacts-menu .selected') }
		groupsList { $('#groups-submenu') }
	}
}
