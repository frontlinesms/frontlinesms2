package frontlinesms2.contact

import frontlinesms2.*
import frontlinesms2.popup.*
import spock.lang.*

class GroupCedSpec extends GroupBaseSpec {
	
	def 'button to save new group is displayed and works'() {
		when:
			to PageContactShow
			def initNumGroups = Group.count()
			bodyMenu.newGroup.click()
			waitFor { at GroupPopup }
			groupName.value('People')
			ok.jquery.trigger("click")
		then:
			at PageContactShow
			waitFor { bodyMenu.getGroupLink "People" }
			assert Group.count() == (initNumGroups + 1)
	}

	def 'More action dropdown has option to rename the group and it works'() {
		given:
			createTestGroupsAndContacts()
			def friendsGroup = Group.findByName("Friends")
		when:
			to PageContactShow, friendsGroup
		then:
			waitFor { header.groupHeaderSection.displayed }
		when:
			header.moreGroupActions.value("rename").click()
		then:
			waitFor { at RenameGroupPopup }
		when:
			groupName.value("Renamed Group")
			ok.jquery.trigger("click")
		then:
			at PageContactShow
			waitFor { bodyMenu.getGroupLink "Renamed Group" }
			header.groupHeaderTitle.text()?.equalsIgnoreCase('Renamed Group (2)')
	}
	
	def 'More action dropdown has option to delete the group and opens a confirmation popup'(){
		given:
			createTestGroupsAndContacts()
			def friendsGroup = Group.findByName("Friends")
		when:
			to PageContactShow, friendsGroup
		then:
			waitFor { header.groupHeaderSection.displayed }
		when:
			header.moreGroupActions.value("delete").click()
		then:
			waitFor{ at DeleteGroupPopup }
		when:
			warningMessage == 'Are you sure you want to delete Friends? WARNING: This cannot be undone'
			ok.jquery.trigger("click")
		then:
			at PageContactShow
			bodyMenu.groupSubmenuLinks == ['Listeners', 'Create new group']
	}

}


