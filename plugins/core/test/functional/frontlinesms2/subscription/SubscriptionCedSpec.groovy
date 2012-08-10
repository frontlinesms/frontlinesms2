package frontlinesms2.subscription

import frontlinesms2.*
import frontlinesms2.page.*
import frontlinesms2.message.PageMessageInbox

class SubscriptionCedSpec extends grails.plugin.geb.GebSpec  {
	def "can launch subscription wizard from create new activity link" () {
		when:
			to PageMessageInbox
			bodyMenu.newActivity.click()
		then:
			waitFor { at CreateActivityDialog}
		when:
			subscription.click()
		then:
			waitFor { at SubscriptionDialog }
	}


}
