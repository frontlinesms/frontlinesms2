package frontlinesms2.subscription

import frontlinesms2.*
import frontlinesms2.contact.*

class SubscriptionViewSpec extends SubscriptionBaseSpec {

	def "subscription page should show the details of the subscription in the header"(){
		/*
		link to group
		join autoreply and keyword
		leave autoreply and keyword
		toggle enables
		group members count

		*/
	}
	def "clicking the group link shoud redirect to the group page"(){}

	// FIXME this is a test skeleton that needs to be fleshed out
	def "Clicking the Quick Message button brings up the Quick Message Dialog with the group prepopulated as recipients"() {
		given:
			createTestSubscriptions() // TODO create a SubscriptionBaseSpec with appropriate test data
		when:
			to PageSubscriptionShow, mySubscription
			waitFor { quickMessageButton.displayed }
			quickMessageButton.click()
		then:
			waitFor { at QuickMessageDialog }
		when:
			compose.textArea << "some test message"
			next.click()
		then:
			waitFor { recipients.displayed }
			// TODO: appropriate group checkbox is ticked
			// TODO: recipient count matches number of contacts in group
	}

	def 'Deleting a group that is used in a subscription should fail with an appropriate error'(){
		given:
			def friendsGroup = new Group(name: "Friends").save()
			def subscription = new Subscription(group:friendsGroup, name:"sign-me-up") // TODO populate with appropriate args
		when:
			to PageContactShow, friendsGroup
		then:
			waitFor { header.groupHeaderSection.displayed }
		when:
			header.moreGroupActions.value("delete").click()
		then:
			waitFor{ at DeleteGroupPopup }
		when:
			warningMessage == 'Are you sure you want to delete Friends? WARNING: This cannot be undone'
			ok.jquery.trigger("click")
		then:
			at PageContactShow
			bodyMenu.groupSubmenuLinks.contains("Friends")
			notifications.flashMessagesText.contains("Cannot delete group Friends: is used by sign-me-up Subscription")
	}
}