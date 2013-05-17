package frontlinesms2.smartgroup

import frontlinesms2.*
import frontlinesms2.contact.PageContactShow
import frontlinesms2.popup.*

class SmartGroupListSpec extends SmartGroupBaseSpec {
	def 'smartgroups list is not visible if there are no smart groups'() {
		when:
			to PageSmartGroup
		then:
			!bodyMenu.smartGroupSubmenuLinks.size()
	}

	def 'smartgroups list is visible if there are smart groups created'() {
		when:
			launchCreateDialog()
			ruleValues[0].value('+44')
			submit.click()
		then:
			at PageSmartGroup
			waitFor { bodyMenu.smartGroupSubmenuLinks.size() == 1 }
	}

	def 'CREATE NEW SMARTGROUP button is available when there are no smart groups'() {
		when:
			to PageSmartGroup
		then:
			bodyMenu.createSmartGroupButton.displayed
	}

	def 'CREATE NEW SMARTGROUP button is available when there are smart groups'() {
		given:
			remote { new SmartGroup(name:'Test Group 1', contactName:'Jeremiah').save(failOnError:true, flush:true); null }
		when:
			to PageSmartGroup
		then:
			bodyMenu.createSmartGroupButton.displayed
	}

	def 'selected smartgroup should be highlighted in the smartgroup menu'() {
		given:
			def a = remote { def g = new SmartGroup(name:'Test Group A', contactName:'A').save(failOnError:true, flush:true); [id:g.id, name:g.name] }
			def b = remote { def g = new SmartGroup(name:'Test Group B', contactName:'B').save(failOnError:true, flush:true); [id:g.id, name:g.name] }
		when:
			goToSmartGroupPage(a.id)
		then:
			menuItemHighlighted(a.name)
			!menuItemHighlighted(b.name)
		when:
			goToSmartGroupPage(b.id)
		then:
			!menuItemHighlighted(a.name)
			menuItemHighlighted(b.name)
	}

	def "renaming a smart group displays a confirmation popup"() {
		given:
			def aId = remote { new SmartGroup(name:'Test Group A', contactName:'A').save(failOnError:true, flush:true).id }
		when:
			goToSmartGroupPage(aId)
		then:
			header.moreGroupActions.displayed
		when:
			header.moreGroupActions.value("rename")
		then:
			waitFor { at RenameSmartGroupPopup }
		when:
			smartGroupName.value('Renamed smart group')
			ok.click()
		then:
			remote { !SmartGroup.findByName("Test Group A") }
	}

	def "deleting a smart group displays a confirmation popup"() {
		given:
			def aId = remote { new SmartGroup(name:'Test Group A', contactName:'A').save(failOnError:true, flush:true).id }
		when:
			goToSmartGroupPage(aId)
		then:
			header.moreGroupActions.displayed
		when:
			header.moreGroupActions.value("delete")
		then:
			waitFor { at DeleteGroupPopup }
		when:
			ok.click()
		then:
			remote { !SmartGroup.findByName("Test Group A") }
	}

	private def goToSmartGroupPage(Long gId) {
		to PageSmartGroupShow, gId
	}
}

