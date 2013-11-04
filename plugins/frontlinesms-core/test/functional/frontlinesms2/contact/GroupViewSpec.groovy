package frontlinesms2.contact

import frontlinesms2.*

class GroupViewSpec extends GroupBaseSpec {
	def 'Group menu is displayed'() {
		given:
			createTestGroups()
		when:
			to PageContactShow
		then:
			bodyMenu.groupSubmenuLinks == ['Friends', 'Listeners', 'contact.create.group']
	}

	def 'Group menu item is highlighted when viewing corresponding group'() {
		given:
			createTestGroups()
		when:
			to PageGroupShow, 'Friends'
		then:
			bodyMenu.selectedMenuItem == 'friends'
	}

	def 'Group members list is displayed when viewing corresponding group'() {
		given:
			createTestGroupsAndContacts()
		when:
			to PageGroupShow, 'Friends'
		then:
			contactList.contacts.containsAll(['Bobby -', 'Duchamps -'])
	}

	def 'Group members list has correct href when viewing corresponding group'() {
		given:
			createTestGroupsAndContacts()
		when:
			to PageGroupShow, 'Friends'
			def links = contactList.contactsLink
		then:
			links.size() == 2
			links.every() { it ==~ '/group/show/\\d+/contact/show/\\d+\\?.+' }
	}

	def 'group members list is paginated'() {
		given:
			createTestGroups()
			createManyContactsAddToGroups()
		when:
			to PageGroupShow, 'Friends'
		then:
			def contactNames = contactList.contacts - "Select All"
			def expectedNames = (11..60).collect{ "Contact${it} 987654321${it}" }
			contactNames == expectedNames
	}

	def "should remain on the same page when a contact is selected from a group"() {
		given:
			createTestGroups()
			createManyContactsAddToGroups()
		when:
			to PageGroupShow, 'Friends'
			footer.prevPage.disabled
			footer.nextPage.click()
		then:
			waitFor { !footer.prevPage.disabled }
		when:
			contactList.selectContact 1
		then:
			waitFor { !footer.prevPage.disabled }
	}
}

