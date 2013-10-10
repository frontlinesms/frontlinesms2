package frontlinesms2.contact

import frontlinesms2.*
import frontlinesms2.popup.*
import spock.lang.*

class GroupCedSpec extends GroupBaseSpec {
	def 'button to save new group is displayed and works'() {
		when:
			to PageContactShow
			def initNumGroups = remote { Group.count() }
			bodyMenu.newGroup.click()
		then:
			waitFor { at GroupPopup }
		when:
			groupName.value('People')
			ok.jquery.trigger("click")
		then:
			at PageContactShow
			waitFor { bodyMenu.getGroupLink "People" }
			remote { Group.count() } == initNumGroups + 1
	}

	def 'More action dropdown has option to rename the group and it works'() {
		given:
			createTestGroupsAndContacts()
			def friendsGroupId = remote { Group.findByName("Friends").id }
		when:
			to PageGroupShow, friendsGroupId
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
			at PageGroupShow
			waitFor { bodyMenu.getGroupLink "Renamed Group" }
			header.groupHeaderTitle.text()?.equalsIgnoreCase('Renamed Group (2)')
	}

	def 'More action dropdown has option to delete the group and opens a confirmation popup'() {
		given:
			createTestGroupsAndContacts()
			def friendsGroupId = remote { Group.findByName("Friends").id }
		when:
			to PageGroupShow, friendsGroupId
		then:
			waitFor { header.groupHeaderSection.displayed }
		when:
			header.moreGroupActions.value("delete").click()
		then:
			waitFor { at DeleteGroupPopup }
		when:
			warningMessage == 'Are you sure you want to delete Friends? WARNING: This cannot be undone'
			ok.jquery.trigger("click")
		then:
			at PageContactShow
			bodyMenu.groupSubmenuLinks == ['Listeners', 'contact.create.group']
	}

}


