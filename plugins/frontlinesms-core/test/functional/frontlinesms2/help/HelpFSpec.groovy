package frontlinesms2.help

import frontlinesms2.popup.*
import frontlinesms2.*
import frontlinesms2.message.PageMessageInbox

class HelpFSpec extends grails.plugin.geb.GebSpec{
	def 'help link should open the help dialog'() {
		given: 'the inbox is open'
			to PageMessageInbox
		when: 'the help link is clicked'
			systemMenu.help.click()
		then: 'the help dialog is visible'
			at PageHelp
	}

	def 'submenus should be collapsed'(){
		given: 'the inbox is open'
			to PageMessageInbox
		when: 'the help link is clicked'
			systemMenu.help.click()
		then: 'the help dialog is visible'
			at PageHelp
		when:
			//something
	}
}
