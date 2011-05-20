package frontlinesms2.contact

import frontlinesms2.*

class GroupViewSpec extends GroupGebSpec {
	def cleanup() {
		Group.findAll().each() {
			it.refresh()
			it.delete(failOnError:true, flush:true)	
		}
	}

	def 'Group menu is displayed'() {
		given:
			createTestGroups()
			def groupNames = ['Listeners', 'Friends']
		when:
			to ContactListPage
		then:
			groupsList.children().collect() {
				it.text()
			} == groupNames
	}

	def 'Group menu item is highli ghted when viewing corresponding group'() {
		given:
			createTestGroups()
		when:
			to FriendsGroupPage
		then:
			selectedMenuItem.text() == 'Friends'
	}

	def 'Group members list is displayed when viewing corresponding group'() {
		given:
			createTestGroupsAndContacts()
			def friendsContactNames = ['Bobby', 'Duchamps']
		when:
			to FriendsGroupPage
		then:
			contactsList.children().collect() { it.text() }.sort() == friendsContactNames
	}
}

class FriendsGroupPage extends geb.Page {
	static getUrl() { "group/show/${Group.findByName('Friends').id}" }
	static content = {
		selectedMenuItem { $('#contacts-menu .selected') }
		contactsList { $('#contacts') }
	}
}
