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
			waitFor("veryslow") { at SubscriptionCreateDialog }
	}

	def "Can create a new subscription" () {
		setup:
			remote { new Group(name:"Friends").save(failOnError:true, flush:true); null }
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
			group.addToGroup(remote { Group.findByName('Friends').id })
			next.click()
		then:
			waitFor { keywords.displayed }
		when:
			keywords.keywordText = 'FRIENDS'
			keywords.joinKeywords = 'join, start'
			keywords.leaveKeywords = 'leave, stop'
			keywords.defaultAction = "join"
			next.click()
		then:
			waitFor { autoreply.displayed }
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
			header.moreActions.value("edit").click()
		then:
			waitFor { at SubscriptionCreateDialog }
		when:
			group.addToGroup(remote { Group.findByName("Camping").id })
			next.click()
		then:
			waitFor {keywords.displayed}
		when:
			keywords.keywordText = 'NOTABOUTFOOTBALL'
			next.click()
			autoreply.enableJoinAutoreply.click()
			autoreply.joinAutoreplyText = "You have been successfully subscribed to some other group"
			autoreply.enableLeaveAutoreply.click()
			autoreply.leaveAutoreplyText = "You have been unsubscribed from some other group"
			next.click()
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
			remote { new Group(name:"Friends").save(failOnError:true, flush:true); null }
		when:
			launchSubscriptionPopup()
			waitFor { at SubscriptionCreateDialog }
			group.addToGroup(remote { Group.findByName('Friends').id })
			next.click()
			keywords.keywordText = 'FRIENDS'
			keywords.joinKeywords = 'join, start'
			keywords.leaveKeywords = 'leave, stop'
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
			waitFor { validationError.text() == 'jquery.validation.required' }
			at SubscriptionCreateDialog
	}

	def "Subscriptions must be associated with a group" () {
		when:
			launchSubscriptionPopup()
			waitFor { at SubscriptionCreateDialog }
			group.addToGroup('search.filter.group')
			next.click()
		then:
			waitFor { validationError.text() == 'subscription.group.required.error' }
			at SubscriptionCreateDialog
	}

	def "Can create a new group from the wizard" () {
		when:
			launchSubscriptionPopup()
			waitFor { at SubscriptionCreateDialog }
		then:
			!group.newGroupName.displayed
		when:
			group.createGroup()
		then:
			group.newGroupName.displayed
		when:
			group.newGroupName << "myTestGroup"
			group.newGroupSubmit.click()
			waitFor {
				!group.newGroupName.displayed
			}
			next.click()
		then:
			waitFor { keywords.displayed }
		when:
			keywords.keywordText = 'FRIENDS'
			keywords.joinKeywords = 'join, start'
			keywords.leaveKeywords = 'leave, stop'
			keywords.defaultAction = "join"
			next.click()
		then:
			waitFor { autoreply.displayed }
		when:
			autoreply.enableJoinAutoreply.click()
			autoreply.joinAutoreplyText = "You have been successfully subscribed to myTestGroup group"
			autoreply.enableLeaveAutoreply.click()
			autoreply.leaveAutoreplyText = "You have been unsubscribed from myTestGroup group"
			next.click()
		then:
			waitFor { confirm.subscriptionName.displayed }
		when:
			confirm.subscriptionName.value("myTestGroup subscription")
			submit.click()
		then:
			waitFor { summary.message.displayed }
		when:
			submit.click()
		then:
			waitFor { at PageMessageSubscription }
	}

	def "Cannot create group if name is already in use" () {
		setup:
			remote { new Group(name:"Friends").save(failOnError:true, flush:true); null }
		when:
			launchSubscriptionPopup()
			waitFor { at SubscriptionCreateDialog }
		then:
			!group.newGroupName.displayed
		when:
			group.createGroup()
		then:
			group.newGroupName.displayed
		when:
			group.newGroupName << "Friends"
			group.newGroupSubmit.click()
		then:
			waitFor { group.groupNameError.displayed }
	}

	def "keyword aliases must be unique if provided"() {
		setup:
			remote { new Group(name:"Friends").save(failOnError:true, flush:true); null }
		when:
			launchSubscriptionPopup()
			waitFor { at SubscriptionCreateDialog }
			group.addToGroup(remote { Group.findByName('Friends').id })
			next.click()
			keywords.keywordText = 'FRIENDS'
			keywords.joinKeywords = 'team'
			keywords.leaveKeywords = 'team'
			next.click()
		then:
			waitFor { validationError.text() == 'poll.keywords.validation.error' }
			keywords.joinKeywords.displayed
			at SubscriptionCreateDialog
	}

	def "keyword aliases must have valid commas seperated values if provided"() {
		setup:
			remote { new Group(name:"Friends").save(failOnError:true, flush:true); null }
		when:
			launchSubscriptionPopup()
			waitFor { at SubscriptionCreateDialog }
			group.addToGroup(remote { Group.findByName('Friends').id })
			next.click()
			keywords.keywordText = 'FRIENDS'
			keywords.joinKeywords = 'team'
			keywords.leaveKeywords = 'team%^&%^%&'
			next.click()
		then:
			waitFor { validationError.text() == 'poll.keywords.validation.error.invalid.keyword' }
			keywords.joinKeywords.displayed
			at SubscriptionCreateDialog
	}

	def "autoreply text must be provided if join/leave autoreply is enabled"() {
		setup:
			remote { new Group(name:"Friends").save(failOnError:true, flush:true); null }
		when:
			launchSubscriptionPopup()
			waitFor { at SubscriptionCreateDialog }
			group.addToGroup(remote { Group.findByName('Friends').id })
			next.click()
			keywords.keywordText = 'FRIENDS'
			keywords.joinKeywords = 'join, start'
			keywords.leaveKeywords = 'leave, stop'
			next.click()
		then:
			waitFor {autoreply.displayed}
		when:
			autoreply.enableJoinAutoreply.click()
			autoreply.joinAutoreplyText = "You have been successfully subscribed to Friends group"
			autoreply.enableLeaveAutoreply.click()
			next.click()
		then:
			waitFor { validationError.text() == 'subscription.leavetext.required' }
			autoreply.enableLeaveAutoreply.displayed
	}

	def "Confirm screen should display all the necessary data" () {
		setup:
			remote { new Group(name:"Friends").save(failOnError:true, flush:true); null }
		when:
			launchSubscriptionPopup()
			waitFor { at SubscriptionCreateDialog }
			group.addToGroup(remote { Group.findByName('Friends').id })
			next.click()
		then:
			waitFor { keywords.displayed }
		when:
			keywords.keywordText = 'FRIENDS'
			keywords.joinKeywords = 'join, start'
			keywords.leaveKeywords = 'leave, stop'
			keywords.defaultAction = "join"
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
			confirm.confirm("keyword-text") ==  "FRIENDS"
			confirm.confirm("join-alias-text") ==  "join, start"
			confirm.confirm("leave-alias-text") ==  "leave, stop"
			confirm.confirm("default-action-text") ==  "join"
			confirm.confirm("join-autoreply-text") ==  "You have been successfully subscribed to Friends group"
			confirm.confirm("leave-autoreply-text") ==  "You have been unsubscribed from Friends group"
	}

	def launchSubscriptionPopup() {
		to PageMessageInbox
		bodyMenu.newActivity.click()
		waitFor('slow') { at CreateActivityDialog }
		subscription.click()
		waitFor('slow') { at SubscriptionCreateDialog }
	}
}

