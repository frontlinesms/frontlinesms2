package frontlinesms2.subscription

import frontlinesms2.*
import frontlinesms2.page.*
import frontlinesms2.message.PageMessageInbox

class SubscriptionCedSpec extends SubscriptionBaseSpec  {
	def "can launch subscription wizard from create new activity link" () {
		when:
			to PageMessageInbox
			bodyMenu.newActivity.click()
		then:
			waitFor { at CreateActivityDialog}
		when:
			subscription.click()
		then:
			waitFor { at SubscriptionCreateDialog }
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
			waitFor {at SubscriptionCreateDialog}
		when:
			group.addToGroup Group.findByName('Friends').id.toString()
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
		when:
			ok.click()
		then:
			waitFor { at PageMessageSubscription }
	}

	def "Can edit an existing subscription"() {
		setup:
			createTestSubscriptions()
		when:
			to PageMessageInbox
			bodyMenu.activityLinks[].click()//click on the subscription
		then:
			waitFor { at PageMessageSubscription }
		when:
			moreActions.value("edit").click()
		then:
			waitFor { at EditSubscriptionDialog }
		when:
			group.addToGroup Group.findByName('Not cats').id.toString()
			keywordText = 'nonecats'
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
			confirm.subscriptionName.value("Not cats subscription")
			submit.click()
		then:
			waitFor { summary.message.displayed }
	}

	def "Should not proceed if subscription not named"() {
		when:
			launchSubscriptionPopup()
			group.addToGroup Group.findByName('Friends').id.toString()
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
			submit.click()
		then:
			waitFor { error }
			at SubscriptionCreateDialog
	}

	def launchSubscriptionPopup() {
		to PageMessageInbox
		bodyMenu.newActivity.click()
		waitFor('slow') { at CreateActivityDialog }
		subscription.click()
		waitFor('slow') { at SubscriptionCreateDialog }
	}
}
