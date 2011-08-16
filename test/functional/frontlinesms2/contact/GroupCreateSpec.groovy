package frontlinesms2.contact

import frontlinesms2.*

class GroupCreateSpec extends grails.plugin.geb.GebSpec {
	def cleanup() {
		Group.findAll().each() {
			it.refresh()
			it.delete(failOnError:true, flush:true)
		}
	}

//	FIXME
//	def 'Errors are displayed when group fails to save'() {
//		when:
//			to CreateGroupPage
//			$("li#create-group a").click()
//			$("#done").click()
//			sleep(1000)
//			waitFor { $('.flash').displayed }
//		then:
//			$('.flash').text() == 'error'
//	}
	
	def 'button to save new group is displayed and works'() {
		when:
			to CreateGroupPage
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

class CreateGroupPage extends geb.Page {
	static url = 'contact'
	static at = {
		title.endsWith('Create Group')
	}
	
	static content = {
		errorMessages { $('.flash .message') }
	}
}
