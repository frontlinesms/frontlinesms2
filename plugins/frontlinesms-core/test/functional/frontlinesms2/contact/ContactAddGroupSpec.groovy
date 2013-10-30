package frontlinesms2.contact

import frontlinesms2.*
import geb.navigator.EmptyNavigator

class ContactAddGroupSpec extends ContactBaseSpec {

	def setup() {
		createTestContacts()
		createTestGroups()
	}

	def 'groups that selected contact belongs to are shown in contact details'() {
		when:
			to PageContactShow, remote { Contact.findByName('Bob').id }
		then:
			singleContactDetails.groupList == ['Test', 'three']
	}

	def 'existing groups that contact is not a member of can be selected from dropdown and are then added to list'() {
		setup:
			def bobId = remote { Contact.findByName('Bob').id }
			def otherGroupId = remote { Group.findByName('Others').id }
		when:
			to PageContactShow, bobId
		then:
			singleContactDetails.otherGroupOptions.containsAll(['contact.add.to.group', 'Others', 'four'])
		when:
			singleContactDetails.addToGroup otherGroupId.toString()
		then:
			waitFor { singleContactDetails.groupList.sort() == ['Others', 'Test', 'three'] }
			singleContactDetails.otherGroupOptions == ['contact.add.to.group', 'four']
	}

	def 'clicking X next to group in list removes group from visible list and from the database'() {
		given:
			def bobId = remote { Contact.findByName('Bob').id }
			def testGroupId = remote { Group.findByName('Test').id }
		when:
			to PageContactShow, bobId
		then:
			singleContactDetails.groupList.size() == 2
			singleContactDetails.groupList.containsAll(['Test', 'three'])
		when:
			singleContactDetails.removeGroup testGroupId.toString()
		then:
			waitFor { singleContactDetails.groupList.size() == 1 }
		when:
			to PageContactShow, bobId
		then:
			waitFor { singleContactDetails.groupList.size() == 1 }
	}

	def 'Adding a contact to groups persists the changes to the database'() {
		setup:
			def bobId = remote { Contact.findByName('Bob').id }
			def otherGroupId = remote { Group.findByName('Others').id.toString() }
		when:
			to PageContactShow, bobId
			singleContactDetails.addToGroup otherGroupId
		then:
			at PageContactShow
			remote { Contact.findByName('Bob') in Group.findByName('Others').members }
	}

	def 'Adding and removing a contact to or from groups (from same edit page) persists the changes to the database'() {
		setup:
			def bobId = remote { Contact.findByName('Bob').id }
			def otherGroupId = remote { Group.findByName('Others').id.toString() }
		when:
			to PageContactShow, bobId
			singleContactDetails.addToGroup otherGroupId
		then:
			at PageContactShow
			remote { Contact.findByName('Bob') in Group.findByName('Others').members }
		when:
			singleContactDetails.removeGroup otherGroupId
		then:
			remote { !(Contact.findByName('Bob') in Group.findByName('Others').members) }
	}

	def 'clicking save actually adds multiple contacts to newly selected groups'() {
		when:
			to PageContactShow
			contactList.selectContact 1
		then:
			waitFor { singleContactDetails.name.value() == 'Bob' }
		when:
			contactList.selectContact 0
		then:
			waitFor { multipleContactDetails.otherMultiGroupOptions.size() > 1 }
		when:
			multipleContactDetails.addToGroup remote { Group.findByName('Others').id.toString() }
			multipleContactDetails.update.jquery.trigger("click")
		then:
			waitFor {
				remote {
					Group.findByName('Others').members*.name.containsAll(['Bob', 'Alice'])
				}
			}
	}

	def 'clicking save removes multiple contacts from selected groups'() {
		given:
			remote {
				def bob = Contact.findByName("Bob")
				def alice = Contact.findByName('Alice')
				def otherGroup = Group.findByName('Others')
				bob.addToGroups(otherGroup,true)
				alice.addToGroups(otherGroup,true)
				assert bob.isMemberOf(otherGroup)
				assert alice.isMemberOf(otherGroup)
			}
		when:
			to PageContactShow
			contactList.selectContact 1
		then:
			waitFor { singleContactDetails.name.value() == 'Bob' }
		when:
			contactList.selectContact 0
		then:
			waitFor { multipleContactDetails.multiGroupList.contains('Others') }
		when:
			multipleContactDetails.removeMultiGroup(remote { Group.findByName('Others').id.toString() })
			multipleContactDetails.update.jquery.trigger("click")
		then:
			waitFor { notifications.flashMessage.displayed }
			remote { Group.findByName('Others').members == [] }
	}

	def 'clicking save removes contact from newly removed groups'() {
		when:
			to PageContactShow, remote { Contact.findByName('Bob').id }
			remote {
				GroupMembership.countMembers(Group.findByName('Test')) == 1
			}
			singleContactDetails.removeGroup(remote { Group.findByName('Test').id.toString() })
			singleContactDetails.name.focus()
		then:
			at PageContactShow
			remote { Group.findByName('Test').members == [] }
	}
}

