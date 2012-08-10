package frontlinesms2.subscription

import frontlinesms2.*
import frontlinesms2.page.*
import frontlinesms2.message.PageMessageInbox

class SubscriptionCedSpec extends gSubscriptionBaseSpec  {
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

	def "Can create a new subscription" () {
		when:
			to PageMessageInbox
			bodyMenu.newActivity.click()
		then:
			waitFor {at CreateActivityDialog}
		when:
			subscription.click()
		then:
			waitFor {at SubscriptionDialog}
		when:
			group.value("friends")
			keywordText = 'friend'
			enableJoinKeyword.click()
			joinAliases = 'join, start'
			enableLeaveKeyword.click()
			leaveAliases = 'leave, stop'
			next.click()
		then:
			waitFor {autoreply.displayed}
		when:
			enableJoinAutoreply.click()
			joinAutoreplyText = "You have been successfully subscribed to Friends group"
			enableLeaveAutoreply.click()
			leaveAutoreplyText = "You have been unsubscribed from Friends group"
		then:
			waitFor { confirm.subscriptionName.displayed }
		when:
			confirm.subscriptionName.value("Friends subscription")
			submit.click()
		then:
			waitFor { summary.message.displayed }
	}

}
