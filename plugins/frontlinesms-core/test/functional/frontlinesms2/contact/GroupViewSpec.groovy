package frontlinesms2.contact

import frontlinesms2.*

class GroupViewSpec extends GroupBaseSpec {
	def 'Group menu is displayed'() {
		given:
			createTestGroups()
		when:
			to PageContactShow
		then:
			bodyMenu.groupSubmenuLinks == ['Friends', 'Listeners', 'Create new group']
	}

	def 'Group menu item is highlighted when viewing corresponding group'() {
		given:
			createTestGroups()
			def friendsGroup = Group.findByName("Friends")
		when:
			to PageContactShow, friendsGroup
		then:
			bodyMenu.selectedMenuItem == 'friends'
		when:
			Contact c = new Contact(name:'Mildred').save(failOnError:true, flush:true)
			c.addToGroups(friendsGroup)
			c.save(failOnError:true, flush:true)
		then:
			bodyMenu.selectedMenuItem == 'friends'
	}

	def 'Group members list is displayed when viewing corresponding group'() {
		given:
			createTestGroupsAndContacts()
			def friendsGroup = Group.findByName("Friends")
		when:
			to PageContactShow, friendsGroup
		then:
			contactList.contacts.containsAll(['Bobby', 'Duchamps'])
	}

	def 'Group members list has correct href when viewing corresponding group'() {
		given:
			createTestGroupsAndContacts()
			def friendsGroup = Group.findByName("Friends")
		when:
			to PageContactShow, friendsGroup
			def links = contactList.contactsLink
		then:
			links.size() == 2
			links.each() {
				assert it ==~ '/group/show/\\d+/contact/show/\\d+\\?.+'
			}
	}
	
	def 'group members list is paginated'() {
		given:
			createTestGroups()
			createManyContactsAddToGroups()
			def friendsGroup = Group.findByName("Friends")
		when:
			to PageContactShow, friendsGroup 
		then:
			def contactNames = contactList.contacts - "Select All"
			def expectedNames = (11..60).collect{"Contact${it}"}
			contactNames == expectedNames
	}
	
	def "should remain on the same page when a contact is selected from a group"() {
		given:
			createTestGroups()
			createManyContactsAddToGroups()
			def friendsGroup = Group.findByName("Friends")
		when:
			to PageContactShow, friendsGroup 
			footer.prevPage.disabled
			footer.nextPage.click()
		then:
			!footer.prevPage.disabled
		when:
			contactList.selectContact 1
		then:
			!footer.prevPage.disabled
	}
	
}


