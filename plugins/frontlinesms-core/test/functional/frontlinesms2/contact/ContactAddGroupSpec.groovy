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
			to PageContactShow, remote { Contact.findByName('Bob') }
		then:
			singleContactDetails.groupList == ['Test', 'three']
	}
	
	def 'existing groups that contact is not a member of can be selected from dropdown and are then added to list'() {
		setup:
			def bob = remote { Contact.findByName('Bob') }
			def otherGroup = remote { Group.findByName('Others') }
		when:
			to PageContactShow, bob
		then:
			singleContactDetails.otherGroupOptions == ['Add to group...', 'Others', 'four']
		when:
			singleContactDetails.addToGroup otherGroup.id.toString()
		then:
			waitFor { singleContactDetails.groupList.sort() == ['Others', 'Test', 'three'] }
			singleContactDetails.otherGroupOptions == ['Add to group...', 'four']
	         
	}

	def 'clicking X next to group in list removes group from visible list, but does not change database if no other action is taken'() {
		given:
			def bob = remote { Contact.findByName("Bob") }
			def bobsGroups
			def testGroup = remote { Group.findByName('Test')}
		when:
			to PageContactShow, bob
		then:
			singleContactDetails.groupList.size() == 2
			singleContactDetails.groupList.containsAll(['Test', 'three'])
		when:
			singleContactDetails.removeGroup testGroup.id.toString()
			bobsGroups = bob.groups
		then:
			waitFor { singleContactDetails.groupList.size() == 1 }
		when:
			to PageContactShow, bob.refresh()
		then:
			waitFor { singleContactDetails.groupList.size() == 2 }
	}

	def 'clicking save actually adds contact to newly selected groups'() {
		setup:
			def bob = remote { Contact.findByName('Bob')}
			def otherGroup = remote { Group.findByName('Others')}
		when:
			to PageContactShow, bob
			singleContactDetails.addToGroup otherGroup.id.toString()
			singleContactDetails.save.click()
		then:
			at PageContactShow
			bob.refresh() in otherGroup.refresh().members
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
			multipleContactDetails.removeMultiGroup remote { Group.findByName('Others').id.toString()}
			multipleContactDetails.update.jquery.trigger("click")
		then:
			waitFor { notifications.flashMessage.displayed }
			remote { Group.findByName('Others').members == [] }
	}
	
	def 'clicking save removes contact from newly removed groups'() {
		when:
			to PageContactShow, remote { Contact.findByName('Bob')}
			remote {
				GroupMembership.countMembers(Group.findByName('Test')) == 1
			}
			singleContactDetails.removeGroup testGroup.id.toString()
			singleContactDetails.save.click()
		then:
			at PageContactShow
			remote {
				testGroup.refresh()
				GroupMembership.countMembers(testGroup) == 0
			}
	}
	
	def "should enable save and cancel buttons when new group is added"() {
		when:
			to PageContactShow, Contact.findByName('Bob')
		then:
			singleContactDetails.save.disabled
		when:
			singleContactDetails.addToGroup Group.findByName('Others').id.toString()
		then:
			waitFor { !singleContactDetails.save.disabled }
			!singleContactDetails.cancel.disabled
			
	}
}
