package frontlinesms2.autoforward

import frontlinesms2.*
import frontlinesms2.page.*
import frontlinesms2.popup.*
import frontlinesms2.message.PageMessageInbox

class AutoforwardCedSpec extends grails.plugin.geb.GebSpec{

	def "can launch autoforward wizard from create new activity link" () {
		given: ' the inbox is opened'
			to PageMessageInbox
		when: 'Activity link is clicked'
			bodyMenu.newActivity.click()
		then: 'the Create Activity Dialog is open'
			waitFor { at CreateActivityDialog }
		when: 'Autoforward is selected'
			autoforward.click()
		then: 'Create Autoforward wizard is open'
			waitFor { at AutoforwardCreateDialog }
	}
}