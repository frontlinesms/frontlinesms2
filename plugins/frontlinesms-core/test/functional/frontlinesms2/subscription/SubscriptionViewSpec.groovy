package frontlinesms2.subscription

import frontlinesms2.*
import frontlinesms2.contact.*
import frontlinesms2.message.*
import frontlinesms2.page.*
import frontlinesms2.popup.*
import frontlinesms2.announcement.*
import spock.lang.*

class SubscriptionViewSpec extends SubscriptionBaseSpec {
	def setup() {
		createTestSubscriptions()
		createTestActivities()
		createTestMessages(Subscription.findByName("Camping Subscription"))
	}

	@Unroll
	def "subscription page should show the details of the subscription in the header"() {
		setup:
			def subscription  = Subscription.findByName("Camping Subscription")
		when:
			to PageMessageSubscription, subscription
		then:
			waitFor { title?.toLowerCase().contains("subscription") }
			header[item] == value
		where:
			item               | value
			'title'            | "camping subscription subscription"
			'groupMemberCount' | '2 members'
			'group'            | 'Group: Camping'
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
			waitFor {
				// TODO when this is supported by Geb, we want to do:
				//at PageGroupShow, Group.findByName("Camping")
				title.contains("Camping") }
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
			notifications.flashMessageText == "Activity archived"
	}

	def "clicking the edit option opens the Subscription Dialog for editing"() {
		when:
			to PageMessageSubscription, Subscription.findByName("Camping Subscription")
		then:
			waitFor { header.displayed }
		when:
			header.moreActions.value("edit").jquery.click()
		then:
			waitFor("veryslow") { at SubscriptionCreateDialog }
	}

	def "Clicking the Quick Message button brings up the Quick Message Dialog with the group prepopulated as recipients"() {
		when:
			to PageMessageSubscription, Subscription.findByName("Camping Subscription")
			waitFor { header.quickMessage.displayed }
			header.quickMessage.click()
		then:
			waitFor('veryslow'){ at QuickMessageDialog }
			waitFor{ compose.textArea.displayed }
		when:
			compose.textArea = "Message"
			next.click()
		then:
			waitFor { recipients.displayed }
			waitFor { recipients.groupCheckboxesChecked*.value().contains("group-${Group.findByName('Camping').id}".toString()) }
	}

	def 'Deleting a group that is used in a subscription should fail with an appropriate error'() {
		given:
			def friendsGroup = Group.build(name: "Friends")
			def subscription = Subscription.build(group:friendsGroup)
		when:
			to PageContactShow, friendsGroup
		then:
			waitFor { header.groupHeaderSection.displayed }
		when:
			header.moreGroupActions.value("delete").click()
		then:
			waitFor { at DeleteGroupPopup }
		when:
			warningMessage == 'Are you sure you want to delete Friends? WARNING: This cannot be undone'
			ok.jquery.trigger("click")
		then:
			at PageContactShow
			bodyMenu.groupSubmenuLinks.contains("Friends")
			notifications.flashMessageText.contains("Unable to delete group. In use by a subscription")
	}

	def 'Moving a message to a subscription launches the categorize dialog'() {
		given:
			def g = Group.findByName("Camping")
			def c1 = Contact.build(name:'prudence', mobile:'+12321')
			def c2 = Contact.build(name:'wilburforce', mobile:'+1232123')
			g.addToMembers(c1)
			def m1 = Fmessage.build(text:'I want to leave', src:'prudence', read:true)
			def m2 = Fmessage.build(text:'I want to join', src:'wilburforce', read:true)
			def subscription = Subscription.findByName('Camping Subscription')
		when:
			to PageMessageInbox, m1
			singleMessageDetails.moveTo(subscription.id)
		then:
			waitFor { at SubscriptionCategoriseDialog }
	}

	def 'When a message is categorised as a join with the dialog, it appears in the correct category and the contact membership is updated'() {
		given:
			def g = Group.findByName("Camping")
			def c1 = Contact.build(name:'prudence', mobile:'+12321')
			def c2 = Contact.build(name:'wilburforce', mobile:'+1232123')
			g.addToMembers(c1)
			def m1 = Fmessage.build(text:'I want to go away', src:'+12321', read:true)
			def m2 = Fmessage.build(text:'I want to come in', src:'+1232123', read:true)
			def subscription = Subscription.findByName('Camping Subscription')
		when:
			to PageMessageInbox, m1
			singleMessageDetails.moveTo(subscription.id)
		then:
			waitFor { at SubscriptionCategoriseDialog }
		when:
			join.click()
			ok.click()
		then:
			waitFor("veryslow") { at PageMessageInbox }
		when:
			to PageMessageInbox, m2
			singleMessageDetails.moveTo(subscription.id)
		then:
			waitFor { at SubscriptionCategoriseDialog }
		when:
			join.click()
			ok.click()
		then:
			waitFor("veryslow") { at PageMessageInbox }
		when:
			to PageMessageSubscription, Subscription.findByName('Camping Subscription')
		then:
			waitFor { at PageMessageSubscription }
			messageList.messages*.source.containsAll(["prudence", "wilburforce"])
			messageList.messages.each { messageRow ->
				if (messageRow.source == "prudence") {
					assert messageRow.text().contains("join")
				}
			}
		when:
			header.groupLink.click()
		then:
			waitFor { title.contains("Camping") }
			at PageContactShow
			contactList.contacts.containsAll(['wilburforce', 'prudence'])
	}

	def 'When a message is categorised as a leave with the dialog, it appears in the correct category and the contact membership is updated'() {
		given:
			def g = Group.findByName("Camping")
			def c1 = Contact.build(name:'prudence', mobile:'+12321')
			def c2 = Contact.build(name:'wilburforce', mobile:'+1232123')
			g.addToMembers(c1)
			def m1 = Fmessage.build(text:'I want to go away', src:'+12321', read:true)
			def m2 = Fmessage.build(text:'I want to come in', src:'+1232123', read:true)
			def subscription = Subscription.findByName('Camping Subscription')
		when:
			to PageMessageInbox, m1
			singleMessageDetails.moveTo(subscription.id)
		then:
			waitFor { at SubscriptionCategoriseDialog }
		when:
			leave.click()
			ok.click()
		then:
			waitFor("veryslow") { at PageMessageInbox }
		when:
			to PageMessageInbox, m2
			singleMessageDetails.moveTo(subscription.id)
		then:
			waitFor { at SubscriptionCategoriseDialog }
		when:
			leave.click()
			ok.click()
		then:
			waitFor("veryslow") { at PageMessageInbox }
		when:
			to PageMessageSubscription, Subscription.findByName('Camping Subscription')
		then:
			waitFor { at PageMessageSubscription }
			messageList.messages*.source.containsAll(["prudence", "wilburforce"])
			messageList.messages.each { messageRow ->
				if (messageRow.source == "prudence") {
					assert messageRow.text().contains("leave")
				}
			}
		when:
			header.groupLink.click()
		then:
			waitFor { title.contains("Camping") }
			at PageContactShow
			!contactList.contacts.containsAll(['wilburforce'])
			!contactList.contacts.containsAll(['prudence'])
	}

	def 'When a message is categorised as a toggle with the dialog, it appears in the correct category and the contact membership is updated'() {
		given:
			def g = Group.findByName("Camping")
			def c1 = Contact.build(name:'prudence', mobile:'+12321')
			def c2 = Contact.build(name:'wilburforce', mobile:'+1232123')
			g.addToMembers(c1)
			def m1 = Fmessage.build(text:'I want to go away', src:'+12321', read:true)
			def m2 = Fmessage.build(text:'I want to come in', src:'+1232123', read:true)
			def subscription = Subscription.findByName('Camping Subscription')
		when:
			to PageMessageInbox, m1
			singleMessageDetails.moveTo(subscription.id)
		then:
			waitFor { at SubscriptionCategoriseDialog }
		when:
			toggle.click()
			ok.click()
		then:
			waitFor("veryslow") { at PageMessageInbox }
		when:
			to PageMessageInbox, m2
			singleMessageDetails.moveTo(subscription.id)
		then:
			waitFor { at SubscriptionCategoriseDialog }
		when:
			toggle.click()
			ok.click()
		then:
			waitFor("veryslow") { at PageMessageInbox }
		when:
			to PageMessageSubscription, Subscription.findByName('Camping Subscription')
		then:
			waitFor { at PageMessageSubscription }
			messageList.messages*.source.containsAll(["prudence", "wilburforce"])
			messageList.messages.each { messageRow ->
				if (messageRow.source == "prudence") {
					assert messageRow.text().contains("toggle")
				}
			}
		when:
			header.groupLink.click()
		then:
			waitFor { title.contains("Camping") }
			at PageContactShow
			contactList.contacts.containsAll(['wilburforce'])
			!contactList.contacts.containsAll(['prudence'])

	}

	def 'Categorisation with the dialog works for sent messages as well, adding/removing the recipients to/from the group'() {
		given:
			def g = Group.findByName("Camping")
			def c1 = Contact.build(name:'prudence', mobile:'+12321')
			def c2 = Contact.build(name:'wilburforce', mobile:'+1232123')
			g.addToMembers(c1)
			def m1 = new Fmessage(src:'src', hasSent:true, inbound:false, text:'hi prudence and wilburforce! You are signed up by force').addToDispatches(dst:"+12321", status:DispatchStatus.SENT, dateSent:new Date()).save(flush: true, failOnError:true)
			m1.addToDispatches(dst:"+1232123", status:DispatchStatus.SENT, dateSent:new Date()).save(flush: true, failOnError:true)
			def subscription = Subscription.findByName('Camping Subscription')
		when:
			to PageMessageSent, m1
			singleMessageDetails.moveTo(subscription.id)
		then:
			waitFor { at SubscriptionCategoriseDialog }
		when:
			join.click()
			ok.click()
		then:
			waitFor("veryslow") { at PageMessageInbox }
		when:
			to PageMessageSubscription, Subscription.findByName('Camping Subscription')
		then:
			waitFor { at PageMessageSubscription }
			["join", "prudence", "wilburforce"].each {
				assert messageList.messages[0].text().contains(it)
			}
		when:
			header.groupLink.click()
		then:
			waitFor { title.contains("Camping") }
			at PageContactShow
			contactList.contacts.containsAll(['prudence', 'wilburforce'])
	}

	def "clicking the rename option opens the rename small popup"() {
		when:
			to PageMessageSubscription, Subscription.findByName("Camping Subscription")
		then:
			waitFor { header.displayed }
		when:
			header.moreActions.value("rename").jquery.click()
		then:
			waitFor { at RenameSubscriptionDialog }
			waitFor { subscriptionName.jquery.val().contains("Camping Subscription") }
	}

	def "clicking the delete option opens the confirm delete small popup"() {
		when:
			to PageMessageSubscription, Subscription.findByName("Camping Subscription")
		then:
			waitFor { header.displayed }
		when:
			header.moreActions.value("delete").jquery.click()
		then:
			waitFor { at DeleteActivity }
	}

	def "clicking the export option opens the export dialog"() {
		when:
			to PageMessageSubscription, Subscription.findByName("Camping Subscription")
		then:
			waitFor { header.displayed }
		when:
			header.moreActions.value("export").jquery.click()
		then:
			waitFor { at ExportDialog }
	}

	def "selecting a single message reveals the single message view"() {
		when:
			to PageMessageSubscription, Subscription.findByName("Camping Subscription")
		then:
			waitFor { messageList.displayed }
		when:
			messageList.messages[0].checkbox.click()
		then:
			waitFor { singleMessageDetails.displayed }
			waitFor { singleMessageDetails.text == "Test message 0" }
	}

	def "selecting multiple messages reveals the multiple message view"() {
		when:
			to PageMessageSubscription, Subscription.findByName("Camping Subscription")
		then:
			waitFor { messageList.displayed }
		when:
			messageList.messages[0].checkbox.click()
			waitFor { singleMessageDetails.displayed }
			messageList.messages[1].checkbox.click()		
		then:
			waitFor { multipleMessageDetails.displayed }
			waitFor { multipleMessageDetails.text?.toLowerCase() == "2 messages selected" }
	}

	def "clicking on a message reveals the single message view with clicked message"() {
		when:
			to PageMessageSubscription, Subscription.findByName("Camping Subscription")
		then:
			waitFor { messageList.displayed }
		when:
			messageList.messages[3].checkbox.click()
		then:
			waitFor { singleMessageDetails.displayed }
			messageList.messages[3].hasClass("selected")
			singleMessageDetails.text == "Test message 3"
	}

	def "delete single message action works "() {
		when:
			to PageMessageSubscription, Subscription.findByName("Camping Subscription")
		then:
			waitFor { messageList.displayed }
		when:
			messageList.messages[0].checkbox.click()
		then:
			waitFor { singleMessageDetails.displayed }
		when:
			singleMessageDetails.delete.click()
		then:
			waitFor { messageList.displayed }
			!messageList.messages*.text.contains("Test message 0")
	}

	def "delete multiple message action works for multiple select"(){
		when:
			to PageMessageSubscription, Subscription.findByName("Camping Subscription")
		then:
			waitFor { messageList.displayed }
		when:
			messageList.messages[0].checkbox.click()
			waitFor {singleMessageDetails.displayed }
			messageList.messages[1].checkbox.click()
		then:
			waitFor { multipleMessageDetails.displayed }
		when:
			multipleMessageDetails.deleteAll.click()
		then:
			waitFor { messageList.displayed }
			!messageList.messages*.text.containsAll("Test message 0", "Test message 1")
	}

	def "move single message action works"() {
		when:
			to PageMessageSubscription, Subscription.findByName("Camping Subscription")
		then:
			waitFor { messageList.displayed }
		when:
			messageList.messages[0].checkbox.click()
		then:
			waitFor { singleMessageDetails.displayed }
			waitFor { singleMessageDetails.text == "Test message 0" }
		when:
			singleMessageDetails.moveTo(Activity.findByName("Sample Announcement").id).click()
		then:
			waitFor("veryslow") { at PageMessageSubscription }
			waitFor { notifications.flashMessageText.contains("updated") }
			!messageList.messages*.text.contains("Test message 0")
		when:
			to PageMessageAnnouncement, Activity.findByName("Sample Announcement")
		then:
			waitFor { messageList.displayed }
			messageList.messages*.text.contains("Test message 0")
	}

	def "move multiple message action works"() {
		when:
			to PageMessageSubscription, Subscription.findByName("Camping Subscription")
		then:
			waitFor { messageList.displayed }
		when:
			messageList.messages[0].checkbox.click()
			waitFor {singleMessageDetails.displayed }
			messageList.messages[1].checkbox.click()
		then:
			waitFor { multipleMessageDetails.displayed }
		when:
			multipleMessageDetails.moveTo(Activity.findByName("Sample Announcement").id).click()
		then:
			waitFor("veryslow") { notifications.flashMessageText.contains("updated") }
			!messageList.messages*.text.containsAll("Test message 0", "Test message 1")
		when:
			to PageMessageAnnouncement, Activity.findByName("Sample Announcement")
		then:
			waitFor { messageList.displayed }
			messageList.messages*.text.containsAll("Test message 0", "Test message 1")
	}

	def "moving a message from another activity to a subscription opens the categorise popup for the chosen subscription"() {
		setup:
			def activity = Activity.findByName("Sample Announcement")
			def m = Fmessage.findBySrc("announce")
			def subscription = Subscription.findByName('Camping Subscription')
		when:
			to PageMessageAnnouncement, activity.id, m.id
		then:
			waitFor { singleMessageDetails.displayed }
		when:
			singleMessageDetails.moveTo(subscription.id)
		then:
			waitFor { at SubscriptionCategoriseDialog }
	}
}
