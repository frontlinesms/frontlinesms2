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
			contactsList.children()*.text().sort() == ['Bobby', 'Duchamps']
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
	
	def 'group members list is paginated'() {
		given:
			createTestGroups()
			createManyContacts()
		when:
			go "/frontlinesms2/group/show/${Group.findByName('Friends').id}"
		then:
			def contactList = $('#contact-list')
			def contactNames = contactList.children()*.text()
			def expectedNames = (11..60).collect{"Contact${it}"}
			contactNames == expectedNames
	}
	
	def createManyContacts() {
		(11..90).each {
			def c = new Contact(name: "Contact${it}", primaryMobile: "987654321${it}", notes: 'notes').save(failOnError:true, flush:true)
			c.addToGroups(Group.findByName('Friends')).save(failOnError:true, flush:true)
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
