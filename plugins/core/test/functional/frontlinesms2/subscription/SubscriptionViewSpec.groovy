package frontlinesms2.subscription

import frontlinesms2.*
import frontlinesms2.contact.*
import frontlinesms2.message.*
import frontlinesms2.popup.*
import spock.lang.*

class SubscriptionViewSpec extends SubscriptionBaseSpec {
	def setup() {
		createTestSubscriptions()
		createTestActivities()
		createTestMessages(Subscription.findByName("Camping Subscription"))
	}

	@Unroll
	@IgnoreRest
	def "subscription page should show the details of the subscription in the header"() {
		setup:
			def subscription  = Subscription.findByName("Camping Subscription")
		when:
			to PageMessageSubscription, subscription
		then:
			waitFor { title.toLowerCase().contains("subscription") }
			header[item] == value
		where:
			item               | value
			'title'            | "camping subscription subscription"
			'group'            | 'Group: Camping'
			'groupMemberCount' | '2 members'
			'keyword'          | 'Keyword: CAMPING'
			'joinAliases'      | 'Join: JOIN,IN,START'
			'leaveAliases'     | 'Leave: LEAVE,OUT,STOP'
	}

	def "clicking the group link shoud redirect to the group page"() {
		when:
			to PageMessageSubscription, Subscription.findByName("Camping Subscription")
		then:
			waitFor { header.displayed }
		when:
			header.groupLink.click()
		then:
			waitFor { at PageGroup, Group.findByName("camping").id }
	}

	def "clicking the archive button archives the subscription and redirects to inbox "() {
		when:
			to PageMessageSubscription, Subscription.findByName("Camping Subscription")
		then:
			waitFor { header.displayed }
		when:
			header.archive.click()
		then:
			waitFor { at PageMessageInbox }
			notifications.flashmessage.contains("subscription has been archived")
	}

	def "clicking the edit option opens the Subscription Dialog for editing"(){
		when:
			to PageMessageSubscription, Subscription.findByName("Camping Subscription")
		then:
			waitFor { header.displayed }
		when:
			header.moreAction("edit").jquery.click()
		then:
			waitFor { at EditSubscriptionDialog }
	}

	def "clicking the group link shoud redirect to the group page"(){}

	// FIXME this is a test skeleton that needs to be fleshed out
	def "Clicking the Quick Message button brings up the Quick Message Dialog with the group prepopulated as recipients"() {
		given:
			def allrounderBobby = new Contact(name:'Bobby', mobile:"987654321").save(failOnError:true)
			def campingGroup = new Group(name:"Campers").save(failOnError:true)
			def campingKeyword = new Keyword(value: 'CAMP')
			def campingSub = new Subscription(name:"Campers Subscription", group:campingGroup, joinAliases:"JOIN,IN,START", leaveAliases:"LEAVE,OUT,STOP",
				defaultAction:Subscription.Action.JOIN, keyword:campingKeyword).save(failOnError:true)
			campingGroup.addToMembers(allrounderBobby)
		when:
			to PageMessageSubscription, campingSub
			waitFor { header.quickMessage.displayed }
			header.quickMessage.click()
		then:
			waitFor('slow'){ at QuickMessageDialog }
			waitFor{compose.textArea.displayed}
		when:
			compose.textArea = "Message"
			next.click()
		then:
			waitFor { recipients.displayed }
			waitFor { recipients.groupCheckboxes[2].checked }
			waitFor { recipients.count == 1 }
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
			waitFor { at SubscriptionCategoriseDialog }
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
			waitFor { at SubscriptionCategoriseDialog }
		when:
			leave.click()
		then:
			waitFor { at PageMessageInbox }
		when:
			to PageMessageInbox, m2
			singleMessageDetails.moveTo(Subscription.findByGroup(g))
		then:
			waitFor { at SubscriptionCategoriseDialog }
		when:
			join.click()
			to PageMessageSubscription, Subscription.findByGroup(g)
		then:
			waitFor { at PageMessageInbox }
		when:
			to PageMessageSubscription, Subscription.findByGroup(g)
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

	def "clicking the rename option opens the rename small popup"(){
		when:
			to PageMessageSubscription, Subscription.findByName("Camping Subscription")
		then:
			waitFor { header.displayed }
		when:
			header.moreAction("rename").jquery.click()
		then:
			waitFor { at RenameSubscriptionDialog }
			waitFor { subscriptionName == "Camping Subscription" }
	}

	def "clicking the delete option opens the confirm delete small popup"(){
		when:
			to PageMessageSubscription, Subscription.findByName("Camping Subscription")
		then:
			waitFor { header.displayed }
		when:
			header.moreAction("delete").jquery.click()
		then:
			waitFor { at ConfirmDeleteDialog }
	}

	def "clicking the export option opens the export dialog"(){
		when:
			to PageMessageSubscription, Subscription.findByName("Camping Subscription")
		then:
			waitFor { header.displayed }
		when:
			header.moreAction("export").jquery.click()
		then:
			waitFor { at ExportDialog }
	}

	def "selecting a single message reveals the single message view"(){
		when:
			to PageMessageSubscription, Subscription.findByName("Camping Subscription")
		then:
			waitFor { messageList.messages.displayed }
		when:
			messageList.messages[0].checkbox.click()
		then:
			waitFor { singleMessageDetails.displayed }
			waitFor { singleMessageDetails.text == "some text" }
	}

	def "selecting multiple messages reveals the multiple message view"(){
		when:
			to PageMessageSubscription, Subscription.findByName("Camping Subscription")
		then:
			waitFor { messageList.messages.displayed }
		when:
			messageList.messages[0].checkbox.click()
			waitFor { singleMessageDetails.displayed }
			messageList.messages[1].checkbox.click()		
		then:
			waitFor { multipleMessageDetails.displayed }
			waitFor { multipleMessageDetails.text.toLowerCase() == "2 messages selected" }
	}

	def "clicking on a message reveals the single message view with clicked message"(){
		when:
			to PageMessageSubscription, Subscription.findByName("Camping Subscription")
		then:
			waitFor { messageList.messages.displayed }
		when:
			messageList.messages[0].click()
		then:
			waitFor { messageList.displayed }
			messageList.messages[0].hasClass("selected")
			singleMessageDetails.displayed
			singleMessageDetails.text == "some text"
	}

	def "delete single message action works "(){
		when:
			to PageMessageSubscription, Subscription.findByName("Camping Subscription")
		then:
			waitFor { messageList.messages.displayed }
		when:
			messageList.messages[0].click()
		then:
			waitFor { singleMessageDetails.displayed }
		when:
			singleMessageDetails.delete.click()
		then:
			waitFor { messageList.messages.displayed }
			!messageList.messages*.text.contains("the text contents of the previous message")
	}

	def "delete multiple message action works for multiple select"(){
		when:
			to PageMessageSubscription, Subscription.findByName("Camping Subscription")
		then:
			waitFor { messageList.messages.displayed }
		when:
			messageList.messages[0].checkbox.click()
			waitFor {singleMessageDetails.displayed }
			messageList.messages[1].checkbox.click()
		then:
			waitFor { multipleMessageDetails.displayed }
		when:
			multipleMessageDetails.deleteAll.click()
		then:
			waitFor { messageList.messages.displayed }
			!messageList.messages*.text.containsAll("the text contents of the previous message", "the second message message")
	}

	def "move single message action works"(){
		when:
			to PageMessageSubscription, Subscription.findByName("Camping Subscription")
		then:
			waitFor { messageList.messages.displayed }
		when:
			messageList.messages[0].click()
		then:
			waitFor { singleMessageDetails.displayed }
		when:
			singleMessageDetails.moveTo("Activity Awesome").click()
		then:
			waitFor { messageList.messages.displayed }
			!messageList.messages*.text.contains("the text contents of the previous message")
		when:
			to PageMessageActivity, Activity.findByName("Sample Announcement")
		then:
			waitFor { messageList.messages.displayed }
			messageList.messages*.text.contains("the text we just moved")
	}

	def "move multiple message action works"(){
		when:
			to PageMessageSubscription, Subscription.findByName("Camping Subscription")
		then:
			waitFor { messageList.messages.displayed }
		when:
			messageList.messages[0].checkbox.click()
			waitFor {singleMessageDetails.displayed }
			messageList.messages[1].checkbox.click()
		then:
			waitFor { multipleMessageDetails.displayed }
		when:
			multipleMessageDetails.moveTo("ACtivity Awesome").click()
		then:
			waitFor { messageList.messages.displayed }
			!messageList.messages*.text.containsAll("the text contents of the previous message", "the second message message")
		when:
			to PageMessageActivity, Activity.findByName("Sample Announcement")
		then:
			waitFor { messageList.messages.displayed }
			messageList.messages*.text.containsAll("the text we just moved", "the second message we just moved")
	}

	def "moving a message from another activity to a subscription opens the categorise popup for the chosen subscription"(){
		setup:
			def activity = Activity.findByName("Sample Announcement")
			def m = Fmessage.findByScr("Bob")
		when:
			to PageMessageActivity, activity.id, m.id
		then:
			waitFor { singleMessageDetails.displayed }
		when:
			singleMessageDetails.moveTo("My Subscription").click()
		then:
			waitFor { messageList.messages.displayed }
		when:
			to PageMessageSubscription, Subscription.findByName("My Subscription")
		then:
			waitFor { messageList.messages.displayed }
			messageList.messages*.text.contains(m.text)
	}
}
