package frontlinesms2.subscription

import frontlinesms2.*
import frontlinesms2.contact.*
import frontlinesms2.message.*

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

	def 'Deleting a group that is used in a subscription should fail with an appropriate error'() {
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

	def 'Moving a message to a subscription launches the categorize dialog'() {
		given:
			createTestSubscriptions()
			def g = Group.findByName("Camping Group")
			def c1 = new Contact(name:'prudence', mobile:'+12321').save(flush:true, failOnError:true)
			def c2 = new Contact(name:'wilburforce', mobile:'+1232123').save(flush:true, failOnError:true)
			g.addToContacts(c1)
			def m1 = Fmessage.build(text:'I want to leave', src:'prudence', read:true)
			def m2 = Fmessage.build(text:'I want to join', src:'wilburforce', read:true)
		when:
			to PageMessageInbox, m1
			singleMessageDetails.moveTo(Subscription.findByGroup(g))
		then:
			waitFor { at SubscriptionCategoriseDialog } // TODO: add this page, extending PageMediumPopup
	}

	def 'When a message is categorised with the dialog, it appears in the correct category and the contact membership is updated'() {
		given:
			createTestSubscriptions()
			def g = Group.findByName("Camping Group")
			def c1 = new Contact(name:'prudence', mobile:'+12321').save(flush:true, failOnError:true)
			def c2 = new Contact(name:'wilburforce', mobile:'+1232123').save(flush:true, failOnError:true)
			g.addToContacts(c1)
			def m1 = Fmessage.build(text:'I want to go away', src:'prudence', read:true)
			def m2 = Fmessage.build(text:'I want to come in', src:'wilburforce', read:true)
		when:
			to PageMessageInbox, m1
			singleMessageDetails.moveTo(Subscription.findByGroup(g))
		then:
			waitFor { at SubscriptionCategoriseDialog } // TODO: add this page, extending PageMediumPopup
		when:
			leave.click()
		then:
			waitFor { at PageMessageInbox }
		when:
			to PageMessageInbox, m2
			singleMessageDetails.moveTo(Subscription.findByGroup(g))
		then:
			waitFor { at SubscriptionCategoriseDialog } // TODO: add this page, extending PageMediumPopup
		when:
			join.click()
			to PageSubscriptionShow, Subscription.findByGroup(g)
		then:
			waitFor { at PageMessageInbox }
		when:
			to PageSubscriptionShow, Subscription.findByGroup(g)
		then:
			messageList.sources.containsAll(["prudence", "wilburforce"])
			messageList.sources.length == 2
			messageList.messages.each { messageRow ->
				if (messageRow.source == "prudence")
					assert messageRow.text.contains("leave")
				else
					assert messageRow.text.contains("join")
			}
	}
}