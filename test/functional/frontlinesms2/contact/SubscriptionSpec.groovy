package frontlinesms2.contact

import frontlinesms2.*

import geb.Browser
import org.openqa.selenium.firefox.FirefoxDriver

class SubscriptionSpec extends GroupGebSpec  {
	              
	def "should be able to set subscription keywords for a group"() {
		setup:
			createTestGroups()
		when:
			go "/frontlinesms2/contact"
			$("#manage-subscription a").click()
			waitFor { $('div#tabs-1').displayed}
		then:
			$("input", name:"id")[0].click()
		when:
			$(".next").click()
	    then:
			$("input", name:"subscriptionKey").value("ADD")
			$("input", name:"unsubscriptionKey").value("REMOVE")
			$("input", type:"submit").click()
		    waitFor({title == 'Contacts'})
			$('div.flash').text().contains('Group updated successfully')
			def groupUpdated = Group.findByName("Listeners").refresh()
			groupUpdated.subscriptionKey == "ADD"
			groupUpdated.unsubscriptionKey == "REMOVE"
		cleanup:
			Group.list()*.delete(flush: true)
	}
}

