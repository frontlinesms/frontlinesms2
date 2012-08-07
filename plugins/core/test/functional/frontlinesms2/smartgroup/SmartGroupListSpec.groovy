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
			new SmartGroup(name:'Test Group 1', contactName:'Jeremiah').save(failOnError:true, flush:true)
		when:
			to PageSmartGroup
		then:
			bodyMenu.createSmartGroupButton.displayed
	}
	
	def 'selected smartgroup should be highlighted in the smartgroup menu'() {
		given:
			def a = new SmartGroup(name:'Test Group A', contactName:'A').save(failOnError:true, flush:true)
			def b = new SmartGroup(name:'Test Group B', contactName:'B').save(failOnError:true, flush:true)
		when:
			goToSmartGroupPage(a)
		then:
			menuItemHighlighted(a)
			!menuItemHighlighted(b)
		when:
			goToSmartGroupPage(b)
		then:
			!menuItemHighlighted(a)
			menuItemHighlighted(b)
	}

	def "renaming a smart group displays a confirmation popup"() {
		given:
			def a = new SmartGroup(name:'Test Group A', contactName:'A').save(failOnError:true, flush:true)
		when:
			goToSmartGroupPage(a)
		then:
			moreActions.displayed
		when:
			moreActionsSelect("rename")
		then:
			waitFor{ dialogIsDisplayed}
		when:
			inputValue("smartgroupname", "Renamed Smart Group")
			done.click()
		then:
			!SmartGroup.findByName("Test Group A")
	}
	
	def "deleting a smart group displays a confirmation popup"() {
		given:
			def a = new SmartGroup(name:'Test Group A', contactName:'A').save(failOnError:true, flush:true)
		when:
			goToSmartGroupPage(a)
		then:
			moreActions.displayed
		when:
			moreActionsSelect("delete")
		then:
			waitFor{ dialogIsDisplayed }
		when:
			done.click()
		then:
			!SmartGroup.findByName("Test Group A")
	}
	
	private def goToSmartGroupPage(SmartGroup g) {
		to PageSmartGroupShow, g
	}
}