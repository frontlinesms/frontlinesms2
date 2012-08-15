package frontlinesms2.subscription

import frontlinesms2.*
import frontlinesms2.page.*
import frontlinesms2.popup.*
import frontlinesms2.message.PageMessageInbox

class SubscriptionCedSpec extends SubscriptionBaseSpec  {
	def "can launch subscription wizard from create new activity link" () {
		when:
			to PageMessageInbox
			bodyMenu.newActivity.click()
		then:
			waitFor { at CreateActivityDialog }
		when:
			subscription.click()
		then:
			waitFor { at SubscriptionCreateDialog }
	}

	def "Can create a new subscription" () {
		setup:
			new Group(name:"Friends").save(failOnError:true)
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
			group.addToGroup Group.findByName('Friends').id
			group.keywordText = 'FRIENDS'
			next.click()
		then:
			waitFor { aliases.displayed }
		when:
			aliases.joinAliases = 'join, start'
			aliases.leaveAliases = 'leave, stop'
			aliases.defaultAction = "join"
			next.click()
		then:
			waitFor {autoreply.displayed}
		when:
			autoreply.enableJoinAutoreply.click()
			autoreply.joinAutoreplyText = "You have been successfully subscribed to Friends group"
			autoreply.enableLeaveAutoreply.click()
			autoreply.leaveAutoreplyText = "You have been unsubscribed from Friends group"
			next.click()
		then:
			waitFor { confirm.subscriptionName.displayed }
		when:
			confirm.subscriptionName.value("Friends subscription")
			submit.click()
		then:
			waitFor { summary.message.displayed }
		when:
			submit.click()
		then:
			waitFor { at PageMessageSubscription }
	}

	def "Can edit an existing subscription"() {
		setup:
			createTestSubscriptions()
		when:
			to PageMessageInbox
			bodyMenu.activityLinks[0].click()//click on the subscription
		then:
			waitFor { at PageMessageSubscription }
		when:
			moreActions.value("edit").click()
		then:
			waitFor { at EditSubscriptionDialog }
		when:
			group.addToGroup Group.findByName('Not cats').id.toString()
			group.keywordText = 'NONECATS'
			next.click()
		then:
			waitFor {aliases.displayed}
		when:
			next.click()
			autoreply.enableJoinAutoreply.click()
			autoreply.joinAutoreplyText = "You have been successfully subscribed to Not cats group"
			autoreply.enableLeaveAutoreply.click()
			autoreply.leaveAutoreplyText = "You have been unsubscribed from Not cats group"
		then:
			waitFor { confirm.subscriptionName.displayed }
		when:
			confirm.subscriptionName.value("Not cats subscription")
			submit.click()
		then:
			waitFor { summary.message.displayed }
	}

	def "Should not proceed if subscription not named"() {
		setup:
			new Group(name:"Friends").save(failOnError:true)
		when:
			launchSubscriptionPopup()
			waitFor { at SubscriptionCreateDialog }
			group.addToGroup Group.findByName('Friends').id.toString()
			group.keywordText = 'FRIENDS'
			next.click()
			aliases.joinAliases = 'join, start'
			aliases.leaveAliases = 'leave, stop'
			next.click()
			autoreply.enableJoinAutoreply.click()
			autoreply.joinAutoreplyText = "You have been successfully subscribed to Friends group"
			autoreply.enableLeaveAutoreply.click()
			autoreply.leaveAutoreplyText = "You have been unsubscribed from Friends group"
			next.click()
		then:
			waitFor { confirm.subscriptionName.displayed }
		when:
			submit.click()
		then:
			waitFor {error.text().contains('This field is required.')}
			at SubscriptionCreateDialog
	}

	def "Subscriptions must be associated with a group" () {
		when:
			launchSubscriptionPopup()
			waitFor { at SubscriptionCreateDialog }
			group.addToGroup('Select group')
			group.keywordText = 'FRIENDS'
			next.click()
		then:
			waitFor {error.text().contains('Subscriptions must have a group')}
			at SubscriptionCreateDialog
	}

	def "keyword must be provided in a subscription"() {
		setup:
			new Group(name:"Friends").save(failOnError:true)
		when:
			launchSubscriptionPopup()
			waitFor { at SubscriptionCreateDialog }
			group.addToGroup Group.findByName('Friends').id
			next.click()
		then:
			waitFor {error.text().contains('This field is required')}
			group.keywordText.displayed
			at SubscriptionCreateDialog
	}

	// TODO: double check this, not sure the test case is valid
	def "keyword aliases must be provided in a subscription if toggle not enabled"() {
		when:
			launchSubscriptionPopup()
			group.addToGroup Group.findByName('Friends').id.toString()
			group.keywordText = 'FRIENDS'
			next.click()
			aliases.joinAliases = ''
			aliases.leaveAliases = ''
			next.click()
		then:
			waitFor {error.text().contains('Please provide aliases')}
			aliases.joinAliases.displayed
			at SubscriptionCreateDialog
	}

	def "autoreply text must be provided if join/leave autoreply is enabled"() {
		when:
			launchSubscriptionPopup()
			group.addToGroup Group.findByName('Friends').id.toString()
			group.keywordText = 'FRIENDS'
			next.click()
			aliases.joinAliases = 'join, start'
			aliases.leaveAliases = 'leave, stop'
			next.click()
		then:
			waitFor {autoreply.displayed}
		when:
			autoreply.enableJoinAutoreply.click()
			autoreply.joinAutoreplyText = "You have been successfully subscribed to Friends group"
			autoreply.enableLeaveAutoreply.click()
			next.click()
		then:
			waitFor {error.text().contains('Please enter autoreply text')}
			autoreply.enableLeaveAutoreply.displayed
	}

	def launchSubscriptionPopup() {
		to PageMessageInbox
		bodyMenu.newActivity.click()
		waitFor('slow') { at CreateActivityDialog }
		subscription.click()
		waitFor('slow') { at SubscriptionCreateDialog }
	}
}
