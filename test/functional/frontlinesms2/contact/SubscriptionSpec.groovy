package frontlinesms2.contact

import frontlinesms2.*

import geb.Browser
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.Keys

class SubscriptionSpec extends GroupGebSpec  {

	def setup() {
		createTestGroups()
	}

	def cleanup() {
		Group.list()*.delete(flush: true)
	}

	def "should move to the next tab if all the values are provided"() {
		when:
			goToManageSubscriptions()
			inputKeywords("subscriptionKey", "ADD")
			inputKeywords("unsubscriptionKey", "REMOVE")
			selectAValueFromDropDown()
			$('.next-validate').click()
			waitFor { $("#tabs-2").displayed }
		then:
			$("#tabs-2").displayed
	}

	def "should be able to set subscription keywords for a group"() {
		when:
			goToManageSubscriptions()
			inputKeywords("subscriptionKey", "ADD")
			inputKeywords("unsubscriptionKey", "REMOVE")
			selectAValueFromDropDown()
			$('.next-validate').click()
			waitFor { $("#tabs-2").displayed }
			$('.next').click()
			waitFor { $("#tabs-3").displayed }
		then:
			$("input", type:"submit").click()
			waitFor({title == 'Contacts'})
			$('div.flash').text().contains('Group updated successfully')
			def groupUpdated = Group.findByName("Listeners").refresh()
			groupUpdated.subscriptionKey == "ADD"
			groupUpdated.unsubscriptionKey == "REMOVE"
	}


	def "should be able to set subscription keyword alone for a group"() {
		when:
			goToManageSubscriptions()
			inputKeywords("subscriptionKey", "ADD")
			selectAValueFromDropDown()
			$('.next-validate').click()
			waitFor { $("#tabs-2").displayed }
			$('.next').click()
			waitFor { $("#tabs-3").displayed }
		then:
			$("input", type:"submit").click()
			waitFor({title == 'Contacts'})
			$('div.flash').text().contains('Group updated successfully')
			def groupUpdated = Group.findByName("Listeners").refresh()
			groupUpdated.subscriptionKey == "ADD"
			!groupUpdated.unsubscriptionKey
	}


	def "should be able to set unsubscription keyword alone for a group"() {
		when:
			goToManageSubscriptions()
			inputKeywords("unsubscriptionKey", "REMOVE")
			selectAValueFromDropDown()
			$('.next-validate').click()
			waitFor { $("#tabs-2").displayed }
			$('.next').click()
			waitFor { $("#tabs-3").displayed }
		then:
			$("input", type:"submit").click()
			waitFor({title == 'Contacts'})
			$('div.flash').text().contains('Group updated successfully')
			def groupUpdated = Group.findByName("Listeners").refresh()
			!groupUpdated.subscriptionKey
			groupUpdated.unsubscriptionKey == "REMOVE"
	}


	def "should not go to the next tab if the subscription checkbox is selected and no value given"() {
		when:
			goToManageSubscriptions()
			inputKeywords("subscriptionKey", "")
			selectAValueFromDropDown()
			$('.next-validate').click()
			waitFor { !($('div.error-panel').text().isEmpty())}
		then:
			$('.error-panel').text().contains("please enter all the details")
	}


	def "should not go to the next tab if the unsubscription checkbox is selected and no value given"() {
		when:
			goToManageSubscriptions()
			inputKeywords("unsubscriptionKey", "")
			selectAValueFromDropDown()
			$('.next-validate').click()
			waitFor { !($('div.error-panel').text().isEmpty())}
		then:
			$('.error-panel').text().contains("please enter all the details")
	}


	def "should check the checkbox on click of subscription key text box"() {
		when:
			goToManageSubscriptions()
			assert !$("input", value: "subscriptionKey").getAttribute("checked")
			$("input", name: "subscriptionKey").click()
		then:
			$("input", value: "subscriptionKey").getAttribute("checked")

	}

	def "should check the checkbox on click of unsubscription key text box"() {
		when:
			goToManageSubscriptions()
			assert !$("input", value: "unsubscriptionKey").getAttribute("checked")
			$("input", name: "unsubscriptionKey").click()
		then:
			$("input", value: "unsubscriptionKey").getAttribute("checked")
	}

	def "should not go to next tab if no group is selected"() {
		when:
			goToManageSubscriptions()
			inputKeywords("subscriptionKey", "ADD")
			inputKeywords("unsubscriptionKey", "REMOVE")
			$('.next-validate').click()
			waitFor { !($('div.error-panel').text().isEmpty())}

		then:
			$('.error-panel').text().contains("please enter all the details")
	}

	def "should not go to the next tab when all the fields are empty"() {
		when:
			goToManageSubscriptions()
			$('.next-validate').click()
			waitFor { !($('div.error-panel').text().isEmpty())}

		then:
			$('.error-panel').text().contains("please enter all the details")
	}


	private def goToManageSubscriptions() {
		go "/frontlinesms2/message"
		$("#manage-subscription a").click()
		waitFor { $('div#tabs-1').displayed}
	}

	private def inputKeywords(keyName, keyValue) {
		 $("input", name:keyName).value(keyValue)

	}
    //FIXME: Need to find a better way to select dropdowns in GEB
	private def selectAValueFromDropDown() {
		$("select", id:"id" ).getJquery().val(Group.list()[0].id.toString());
	}



}

