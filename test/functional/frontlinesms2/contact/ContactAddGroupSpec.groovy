package frontlinesms2.contact

import frontlinesms2.Contact
import frontlinesms2.Group
import geb.navigator.EmptyNavigator

class ContactAddGroupSpec extends ContactGebSpec {
	def setup() {
		createTestContacts()
		createTestGroups()
	}

	def cleanup() {
		deleteTestContacts()
		deleteTestGroups()
	}

	def 'groups that selected contact belongs to are shown in contact details'() {
		given:
			def bob = Contact.findByName('Bob')
		when:
			go "http://localhost:8080/frontlinesms2/contact/show/${bob.id}"
			def lstGroups = $("#group-list")
		then:
			lstGroups.children()*.text() == ['Test']
	}

	def 'existing groups that contact is not a member of can be selected from dropdown and are then added to list'() {
		given:
			def bob = Contact.findByName('Bob')
		when:
			go "http://localhost:8080/frontlinesms2/contact/show/${bob.id}"
			def groupSelector = $("#group-dropdown")
			def lstGroups = $("#group-list")
			groupSelector.find("option", name: 'groups').value('Others')

		then:
			assert lstGroups.children().collect() { it.text() } == ['Test', 'Others']

	}

	def 'clicking "x" next to group in list removes group from visible list, but does not remove contact from group if no other action is taken'() {
		given:
			def lstGroups = $("#group-list")
			def bob = Contact.findByName('Bob')
		when:
			go "http://localhost:8080/frontlinesms2/contact/show/${bob.id}"
		then:
			lstGroups.children().size() == 2
		when:
			lstGroups.find('a').first().click()
		then:
			lstGroups.children().size() == 1
		when:
			lstGroups.find('a').first().click()
		then:
			$("#group-list").text() == 'Not part of any Groups'
	}

	def 'clicking save actually adds contact to newly selected groups'() {
		given:
			def bob = Contact.findByName('Bob')
		when:
			go "http://localhost:8080/frontlinesms2/contact/show/${bob.id}"
			def btn = $("#contactDetails .save")
			btn.click()
		then:
			at ContactListPage
			Group.findByName('Test').members.collect() { it.text() } == 'Bob'
	}

	def 'clicking save removes contact from newly removed groups'() {
		given:
			def bob = Contact.findByName('Bob')
			assert bob != null
		when:
			go "http://localhost:8080/frontlinesms2/contact/show/${bob.id}"
			def lstGroups = $("#group-list")
			def btnCancel = lstGroups.find('a').first()
			assert btnCancel != null && !(btnCancel instanceof EmptyNavigator)
			btnCancel.click()
			def btn = $("#contactDetails .save")
			assert btn != null && !(btn instanceof EmptyNavigator)
			btn.click()
		then:
			at ContactListPage
			Group.findByName('Test').members == null
	}

	// TODO test cancel button?
}
