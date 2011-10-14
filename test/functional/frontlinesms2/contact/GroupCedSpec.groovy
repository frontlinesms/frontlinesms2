package frontlinesms2.contact

import frontlinesms2.*

class GroupCedSpec extends GroupBaseSpec {
	
	def 'button to save new group is displayed and works'() {
		when:
			to PageContactCreateGroup
			def initNumGroups = Group.count()
			$("li#create-group a").click()
			waitFor { $("#modalBox").displayed}
			$('#group-details input', name: "name").value('People')
			$("#done").click()
			waitFor {$("a", text: "People").displayed}
		then:
			assert Group.count() == (initNumGroups + 1)
	}
	
	@spock.lang.IgnoreRest
	def 'More action to rename the group is displayed and open a popup'(){
		given:
			createTestGroupsAndContacts()
		when:
			to PageContactShowGroupFriends
		then:
			$('#group-actions').displayed
		when:
			$('#group-actions').value("renameGroup").click()
		then:
			waitFor{ $('#ui-dialog-title-modalBox').displayed}
		when:
			$("#name").value("Renamed Group")
			$('#done').click()
		then:
			waitFor{$('a', text:'Renamed Group')}
			$('#contact-title h2', text:'Renamed Group')
	}
}


