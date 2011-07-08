package frontlinesms2.contact

import frontlinesms2.*

import geb.Browser
import org.openqa.selenium.firefox.FirefoxDriver

class SubscriptionSpec extends GroupGebSpec  {

	def setup() {
		createTestGroups()
	}

	def cleanup() {
		Group.list()*.delete(flush: true)
	}
	              
	def "should be able to set subscription keywords for a group"() {
		when:
			goToManageSubscriptions()
		then:
			$("input", name:"id")[0].click()
		when:
			$(".next").click()
	    then:
		    inputKeywords('subscriptionKey', 'ADD')
		    inputKeywords('unsubscriptionKey', 'REMOVE')
			$("input", type:"submit").click()
		    waitFor({title == 'Contacts'})
			$('div.flash').text().contains('Group updated successfully')
			def groupUpdated = Group.findByName("Listeners").refresh()
			groupUpdated.subscriptionKey == "ADD"
			groupUpdated.unsubscriptionKey == "REMOVE"
	}

	def "should validate if all the elements are present"() {
		when:
			goToManageSubscriptions()
			$(".next").click()
			$("input", type:"submit").click()
		then:
			$('.error').text().contains("please enter all the details")
	}

	def "should not submit the form if the group is not selected"() {
		when:
			goToManageSubscriptions()
			$(".next").click()
			inputKeywords('subscriptionKey', 'ADD')
			inputKeywords('unsubscriptionKey', 'REMOVE')
			$("input", type:"submit").click()
		then:
			$('.error').text().contains("please enter all the details")
	}

	def "should not submit the form if subscription key is not entered"() {
		when:
			goToManageSubscriptions()
			$("input", name:"id")[0].click()
			$(".next").click()
			inputKeywords('unsubscriptionKey', 'REMOVE')
			$("input", type:"submit").click()
		then:
			$('.error').text().contains("please enter all the details")
	}

	def "should not submit the form if unsubscription key is not entered"() {
		when:
			goToManageSubscriptions()
			$("input", name:"id")[0].click()
			$(".next").click()
			inputKeywords('subscriptionKey', 'ADD')
			$("input", type:"submit").click()
		then:
			$('.error').text().contains("please enter all the details")
	}

	private def goToManageSubscriptions() {
		go "/frontlinesms2/contact"
		$("#manage-subscription a").click()
		waitFor { $('div#tabs-1').displayed}
	}

	private def inputKeywords(keyName, keyValue) {
		$("input", name:keyName).value(keyValue)

	}



}

