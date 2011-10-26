package frontlinesms2.smartgroup

import frontlinesms2.*
import frontlinesms2.contact.PageContactShow

class SmartGroupListSpec extends SmartGroupBaseSpec {
	def 'smartgroups list is not visible if there are no smart groups'() {
		when:
			to PageContactShow
		then:
			smartGroupsListItems.size() == 0
			noSmartGroupsMessage.displayed
	}
	
	def 'smartgroups list is visible if there are smart groups created'() {
		when:
			launchCreateDialog()
			ruleValues[0].value('+44')
			finishButton.click()
		then:
			waitFor { smartGroupsListItems.size() > 0 }
			!noSmartGroupsMessage.displayed	
	}
	
	def 'CREATE NEW SMARTGROUP button is available when there are no smart groups'() {
		when:
			to PageContactShow
		then:
			createSmartGroupButton.displayed
	}

	def 'CREATE NEW SMARTGROUP button is available when there are smart groups'() {
		given:
			new SmartGroup(name:'Test Group 1', contactName:'Jeremiah').save(failOnError:true, flush:true)
		when:
			to PageContactShow
		then:
			createSmartGroupButton.displayed
	}
	
	@spock.lang.IgnoreRest
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
	
	private def goToSmartGroupPage(SmartGroup g) {
		go "smartGroup/$g.id"
	}
	
	private def menuItemHighlighted(SmartGroup g) {
		$("smartgroup-link-$g.id").hasClass('selected')
	}
}