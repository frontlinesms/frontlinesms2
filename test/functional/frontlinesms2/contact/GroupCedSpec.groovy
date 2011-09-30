package frontlinesms2.contact

import frontlinesms2.*

class GroupCedSpec extends grails.plugin.geb.GebSpec {
	
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
}


