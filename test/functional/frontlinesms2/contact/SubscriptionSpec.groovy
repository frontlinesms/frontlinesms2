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
			println $("input", name:"id")
			$("input", name:"id")[0].click()
		when:
			$(".next").click()
	    then:
			$("input", name:"subscribeKeyword").value("ADD")
			$("input", name:"unsubscribeKeyword").value("REMOVE")
			$("input", type:"submit").click()
		    waitFor({title = 'Contacts'})
			$('div.flash').contains('Group updated successfully')
	}
}

