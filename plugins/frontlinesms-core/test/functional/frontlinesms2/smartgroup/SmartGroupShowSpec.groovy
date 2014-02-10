package frontlinesms2.smartgroup

import frontlinesms2.*
import frontlinesms2.contact.PageContactShow
import frontlinesms2.popup.*

class SmartGroupShowSpec extends SmartGroupBaseSpec {
	def 'clicking a smartgroup in the menu should load it in the view'() {
		when:
			launchCreateDialog()
			setRuleValue(0, '+44')
			submit.click()
		then:
			at PageSmartGroup
			waitFor { bodyMenu.getSmartGroupLink('English Contacts').displayed }
		when:
			bodyMenu.getSmartGroupLink('English Contacts').click()
		then:
			at PageSmartGroup
			header.title.startsWith("english contacts")
	}

	def 'changing contact selection in a smartgroup should keep user in smartgroup view'() {
		given:
			remote {
				def c = 0
				['Algernon', 'Bertie'].each {
					new Contact(name:it, mobile:"+44789012345${++c}").save(failOnError:true, flush:true)
				}
			}
		when:
			launchCreateDialog()
			setRuleValue(0, '+44')
			submit.click()
		then:
			at PageSmartGroup
			waitFor { bodyMenu.getSmartGroupLink('English Contacts').displayed }
		when:
			at PageSmartGroup
			bodyMenu.getSmartGroupLink('English Contacts').click()
		then:
			header.title.startsWith("english contacts")
			waitFor {
				contactList.contact[1].displayed
			}
		when:
			contactList.contact[1].click()
		then:
			waitFor {
				header.title.startsWith("english contacts")
			}
	}

	def 'user can edit an existing smartGroup'() {
		setup:
			remote { new SmartGroup(name:'English Contacts', mobile:'+254').save(flush:true, failOnError:true); null }
		when:
			to PageSmartGroupShow, remote { SmartGroup.findByName("English Contacts").id }
			bodyMenu.getSmartGroupLink('English Contacts').click()
		then:
			at PageSmartGroup
			header.title.startsWith('english contacts')
		when:
			header.moreGroupActions.value('edit')
			waitFor { at SmartGroupEditDialog}
			setRuleValue(0, "+44")
			editButton.click()
		then:
			remote { SmartGroup.count() == 1 }
			waitFor { '+44' == remote { SmartGroup.findByName('English Contacts')?.mobile } }
	}
}

