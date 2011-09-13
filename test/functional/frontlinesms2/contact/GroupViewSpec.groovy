package frontlinesms2.contact

import frontlinesms2.*

class GroupViewSpec extends GroupGebSpec {
	def 'Group menu is displayed'() {
		given:
			createTestGroups()
		when:
			to ContactListPage
		then:
			groupsList.children()*.text() == ['Listeners', 'Friends', 'Create new group']
	}

	def 'Group menu item is highlighted when viewing corresponding group'() {
		given:
			createTestGroups()
		when:
			to FriendsGroupPage
		then:
			selectedMenuItem.text() == 'Friends'
		when:
			Contact c = new Contact(name:'Mildred').save(failOnError:true, flush:true)
			c.addToGroups(Group.findByName('Friends'))
			c.save(failOnError:true, flush:true)
			to FriendsGroupPage
		then:
			selectedMenuItem.text() == 'Friends'
	}

	def 'Group members list is displayed when viewing corresponding group'() {
		given:
			createTestGroupsAndContacts()
		when:
			to FriendsGroupPage
		then:
			contactsList.children().collect()*.text().sort() == ['Bobby', 'Duchamps']
	}

	def 'Group members list has correct href when viewing corresponding group'() {
		given:
			createTestGroupsAndContacts()
		when:
			to FriendsGroupPage
			def links = contactsList.find('a')
		then:
			links.size() == 2
			links.each() {
				assert it.@href ==~ '/frontlinesms2/group/show/\\d+/contact/show/\\d+'
			}
	}
}

class FriendsGroupPage extends geb.Page {
	static getUrl() { "group/show/${Group.findByName('Friends').id}" }
	static content = {
		selectedMenuItem { $('#contacts-menu .selected') }
		contactsList { $('#contact-list') }
	}
}
