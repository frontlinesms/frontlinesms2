package frontlinesms2.contact

import frontlinesms2.*

class GroupCreateSpec extends grails.plugin.geb.GebSpec {
	def cleanup() {
		Group.findAll().each() {
			it.refresh()
			it.delete(failOnError:true, flush:true)
		}
	}

	def 'button to save new group is displayed and works'() {
		when:
			to CreateGroupPage
			def initNumGroups = Group.count()
			$("li#create-group a").click()
			$('input', name: "name").value('People')
			btnSave.click()
		then:
			at ContactListPage
			assert Group.count() == (initNumGroups + 1)
	}

	def 'Errors are displayed when group fails to save'() {
		when:
			to CreateGroupPage
			$("li#create-group a").click()
			btnSave.click()
		then:
			errorMessages.present
	}
}

class CreateGroupPage extends geb.Page {
	static url = 'contact'
	static at = {
		title.endsWith('Create Group')
	}

	static content = {
		btnSave { $("#done") }
		errorMessages { $('.flash.message') }
	}
}
