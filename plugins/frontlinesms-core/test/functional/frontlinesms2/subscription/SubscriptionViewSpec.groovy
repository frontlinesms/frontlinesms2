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
		createTestMessages('Camping Subscription')
	}

	@Unroll
	def "subscription page should show the details of the subscription in the header"() {
		when:
			to PageMessageSubscription, 'Camping Subscription'
		then:
			waitFor { title?.toLowerCase().contains("subscription") }
			header[item] == value
		where:
			item               | value
			'title'            | 'subscription.title[camping subscription]'
			'groupMemberCount' | 'subscription.info.groupMemberCount[2]'
			'group'            | 'subscription.info.group[Camping]'
			'keyword'          | 'subscription.info.keyword[CAMPING]'
			'joinAliases'      | 'subscription.info.joinKeywords[JOIN,IN,START]'
			'leaveAliases'     | 'subscription.info.leaveKeywords[LEAVE,OUT,STOP]'
	}

	def "clicking the group link shoud redirect to the group page"() {
		when:
			to PageMessageSubscription, 'Camping Subscription'
		then:
			waitFor { header.displayed }
		when:
			header.groupLink.click()
		then:
			waitFor { title.contains("Camping") }
	}

	def "clicking the archive button archives the subscription and redirects to inbox "() {
		when:
			to PageMessageSubscription, 'Camping Subscription'
		then:
			waitFor { header.displayed }
		when:
			header.archive.click()
		then:
			waitFor { at PageMessageInbox }
			notifications.flashMessageText == "default.archived[activity.label]"
	}

	def "clicking the edit option opens the Subscription Dialog for editing"() {
		when:
			to PageMessageSubscription, 'Camping Subscription'
		then:
			waitFor { header.displayed }
		when:
			header.moreActions.value("edit").jquery.click()
		then:
			waitFor("veryslow") { at SubscriptionCreateDialog }
	}

	def "Clicking the Quick Message button brings up the Quick Message Dialog with the group prepopulated as recipients"() {
		when:
			to PageMessageSubscription, 'Camping Subscription'
			waitFor { header.quickMessage.displayed }
			header.quickMessage.click()
		then:
			waitFor('veryslow'){ at QuickMessageDialog }
			waitFor{textArea.displayed }
		when:
			textArea = "Message"
		then:
			waitFor { recipients.displayed }
			waitFor { recipients.getRecipients('group') == [remote { Group.findByName('Camping').id }.toString() ] }
	}

	def 'Deleting a group that is used in a subscription should fail with an appropriate error'() {
		given:
			def friendsGroup = remote {
				def g = Group.build(name: "Friends")
				Subscription.build(group:g)
				return g.id
			}
		when:
			to PageGroupShow, "Friends"
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
			notifications.flashMessageText == 'group.delete.fail'
	}

	def 'Moving a message to a subscription launches the categorize dialog'() {
		given:
			def data = remote { 
				def g = Group.findByName("Camping")
				def c1 = Contact.build(name:'prudence', mobile:'+12321')
				def c2 = Contact.build(name:'wilburforce', mobile:'+1232123')
				g.addToMembers(c1)
				def m1 = TextMessage.build(text:'I want to leave', src:'prudence', read:true)
				sleep 2000
				def m2 = TextMessage.build(text:'I want to join', src:'wilburforce', read:true)
				def s = Subscription.findByName('Camping Subscription')
				[s:s.id, m:m1.id]
			}
		when:
			to PageMessageInbox, data.m
			singleMessageDetails.moveTo(data.s)
		then:
			waitFor { at SubscriptionCategoriseDialog }
	}

	def 'When a message is categorised as a join with the dialog, it appears in the correct category and the contact membership is updated'() {
		given:
			def data = remote {
				def g = Group.findByName("Camping")
				def c1 = Contact.build(name:'prudence', mobile:'+12321')
				def c2 = Contact.build(name:'wilburforce', mobile:'+1232123')
				g.addToMembers(c1)
				def m1 = TextMessage.build(text:'I want to go away', src:'+12321', read:true)
				sleep 2000
				def m2 = TextMessage.build(text:'I want to come in', src:'+1232123', read:true)
				def s = Subscription.findByName('Camping Subscription')
				[s:s.id, m1:m1.id, m2:m2.id]
			}
		when:
			to PageMessageInbox, data.m1
			singleMessageDetails.moveTo(data.s)
		then:
			waitFor { at SubscriptionCategoriseDialog }
		when:
			join.click()
			ok.click()
		then:
			waitFor("veryslow") { at PageMessageInbox }
		when:
			to PageMessageInbox, data.m2
			singleMessageDetails.moveTo(data.s)
		then:
			waitFor { at SubscriptionCategoriseDialog }
		when:
			join.click()
			ok.click()
		then:
			waitFor("veryslow") { at PageMessageInbox }
		when:
			to PageMessageSubscription, 'Camping Subscription'
		then:
			waitFor { at PageMessageSubscription }
			messageList.messageSource(0) == 'wilburforce'
			messageList.messageSource(1) == 'prudence'
			messageList.messageText(1).startsWith('join ')
		when:
			header.groupLink.click()
		then:
			waitFor { title.contains("Camping") }
			at PageContactShow
			contactList.contacts.containsAll(['wilburforce +1232123', 'prudence +12321'])
	}

	def 'When a message is categorised as a leave with the dialog, it appears in the correct category and the contact membership is updated'() {
		given:
			def data = remote {
				def g = Group.findByName("Camping")
				def c1 = Contact.build(name:'prudence', mobile:'+12321')
				def c2 = Contact.build(name:'wilburforce', mobile:'+1232123')
				g.addToMembers(c1)
				def m1 = TextMessage.build(text:'I want to go away', src:'+12321', read:true)
				sleep 2000
				def m2 = TextMessage.build(text:'I want to come in', src:'+1232123', read:true)
				def subscription = Subscription.findByName('Camping Subscription')
				[m1:m1.id, m2:m2.id, s:subscription.id]
			}
		when:
			to PageMessageInbox, data.m1
			singleMessageDetails.moveTo(data.s)
		then:
			waitFor { at SubscriptionCategoriseDialog }
		when:
			leave.click()
			ok.click()
		then:
			waitFor("veryslow") { at PageMessageInbox }
		when:
			to PageMessageInbox, data.m2
			singleMessageDetails.moveTo(data.s)
		then:
			waitFor { at SubscriptionCategoriseDialog }
		when:
			leave.click()
			ok.click()
		then:
			waitFor("veryslow") { at PageMessageInbox }
		when:
			to PageMessageSubscription, 'Camping Subscription'
		then:
			waitFor { at PageMessageSubscription }
			messageList.messageSource(0) == 'wilburforce'
			messageList.messageSource(1) == 'prudence'
			messageList.messageText(1).startsWith('leave ')
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
			def data = remote {
				def g = Group.findByName("Camping")
				def c1 = Contact.build(name:'prudence', mobile:'+12321')
				def c2 = Contact.build(name:'wilburforce', mobile:'+1232123')
				g.addToMembers(c1)
				def m1 = TextMessage.build(text:'I want to go away', src:'+12321', read:true)
				sleep 2000
				def m2 = TextMessage.build(text:'I want to come in', src:'+1232123', read:true)
				def subscription = Subscription.findByName('Camping Subscription')
				[m1:m1.id, m2:m2.id, s:subscription.id]
			}
		when:
			to PageMessageInbox, data.m1
			singleMessageDetails.moveTo(data.s)
		then:
			waitFor { at SubscriptionCategoriseDialog }
		when:
			toggle.click()
			ok.click()
		then:
			waitFor("veryslow") { at PageMessageInbox }
		when:
			to PageMessageInbox, data.m2
			singleMessageDetails.moveTo(data.s)
		then:
			waitFor { at SubscriptionCategoriseDialog }
		when:
			toggle.click()
			ok.click()
		then:
			waitFor("veryslow") { at PageMessageInbox }
		when:
			to PageMessageSubscription, 'Camping Subscription'
		then:
			waitFor { at PageMessageSubscription }
			messageList.messageSource(0) == 'wilburforce'
			messageList.messageSource(1) == 'prudence'
			messageList.messageText(1).startsWith('toggle ') // TODO should these really say TOGGLE?  or rather 'join' or 'leave' depending on what the toggle caused?
		when:
			header.groupLink.click()
		then:
			waitFor { title.contains("Camping") }
			at PageContactShow
			contactList.contacts.containsAll(['wilburforce +1232123'])
			!contactList.contacts.containsAll(['prudence +12321'])

	}

	def 'Categorisation with the dialog works for sent messages as well, adding/removing the recipients to/from the group'() {
		given:
			def data = remote {
				def g = Group.findByName("Camping")
				def c1 = Contact.build(name:'prudence', mobile:'+12321')
				def c2 = Contact.build(name:'wilburforce', mobile:'+1232123')
				g.addToMembers(c1)
				def m1 = new TextMessage(src:'src', hasSent:true, inbound:false, text:'hi prudence and wilburforce! You are signed up by force').addToDispatches(dst:"+12321", status:DispatchStatus.SENT, dateSent:new Date()).save(flush:true, failOnError:true)
				m1.addToDispatches(dst:"+1232123", status:DispatchStatus.SENT, dateSent:new Date()).save(flush:true, failOnError:true)
				def subscription = Subscription.findByName('Camping Subscription')
				[m1:m1.id, s:subscription.id]
			}
		when:
			to PageMessageSent, data.m1
			singleMessageDetails.moveTo(data.s)
		then:
			waitFor { at SubscriptionCategoriseDialog }
		when:
			join.click()
			ok.click()
		then:
			waitFor("veryslow") { at PageMessageInbox }
		when:
			to PageMessageSubscription, 'Camping Subscription'
		then:
			waitFor { at PageMessageSubscription }
			["join", "prudence", "wilburforce"].each {
				messageList.messageText(0)?.contains(it)
			}
		when:
			header.groupLink.click()
		then:
			waitFor { title.contains("Camping") }
			at PageContactShow
			contactList.contacts.containsAll(['prudence +12321', 'wilburforce +1232123'])
	}

	def "clicking the rename option opens the rename small popup"() {
		when:
			to PageMessageSubscription, 'Camping Subscription'
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
			to PageMessageSubscription, 'Camping Subscription'
		then:
			waitFor { header.displayed }
		when:
			header.moreActions.value("delete").jquery.click()
		then:
			waitFor { at DeleteActivity }
	}

	def "clicking the export option opens the export dialog"() {
		when:
			to PageMessageSubscription, 'Camping Subscription'
		then:
			waitFor { header.displayed }
		when:
			header.moreActions.value("export").jquery.click()
		then:
			waitFor { at ExportDialog }
	}

	def "selecting a single message reveals the single message view"() {
		when:
			to PageMessageSubscription, 'Camping Subscription'
		then:
			waitFor { messageList.displayed }
		when:
			messageList.toggleSelect(0)
		then:
			waitFor { singleMessageDetails.displayed }
			waitFor { singleMessageDetails.text == "Test message 0" }
	}

	def "selecting multiple messages reveals the multiple message view"() {
		when:
			to PageMessageSubscription, 'Camping Subscription'
		then:
			waitFor { messageList.displayed }
		when:
			messageList.toggleSelect(0)
			waitFor { singleMessageDetails.displayed }
			messageList.toggleSelect(1)
		then:
			waitFor { multipleMessageDetails.displayed }
			waitFor { multipleMessageDetails.text == 'message.multiple.selected[2]' }
	}

	def "clicking on a message reveals the single message view with clicked message"() {
		when:
			to PageMessageSubscription, 'Camping Subscription'
		then:
			waitFor { messageList.displayed }
		when:
			messageList.toggleSelect(3)
		then:
			waitFor { singleMessageDetails.displayed }
			messageList.hasClass(3, "selected")
			singleMessageDetails.text == "Test message 3"
	}

	def "delete single message action works"() {
		when:
			to PageMessageSubscription, 'Camping Subscription'
		then:
			waitFor { messageList.displayed }
		when:
			messageList.toggleSelect(0)
		then:
			waitFor { singleMessageDetails.displayed }
		when:
			singleMessageDetails.delete.click()
		then:
			waitFor { messageList.displayed }
			messageList.messageText(0) != 'Test message 0'
	}

	def "delete multiple message action works for multiple select"() {
		when:
			to PageMessageSubscription, 'Camping Subscription'
		then:
			waitFor { messageList.displayed }
		when:
			messageList.toggleSelect(0)
			waitFor { singleMessageDetails.displayed }
			messageList.toggleSelect(1)
		then:
			waitFor { multipleMessageDetails.displayed }
		when:
			multipleMessageDetails.deleteAll.click()
		then:
			waitFor { messageList.displayed }
			!(messageList.messageText(0) in ['Test message 0', 'Test message 1'])
	}

	def "move single message action works"() {
		when:
			to PageMessageSubscription, 'Camping Subscription'
		then:
			waitFor { messageList.displayed }
		when:
			messageList.toggleSelect(0)
		then:
			waitFor { singleMessageDetails.displayed }
			waitFor { singleMessageDetails.text == "Test message 0" }
		when:
			singleMessageDetails.moveTo(remote { Activity.findByName("Sample Announcement").id }).click()
		then:
			waitFor("veryslow") { at PageMessageSubscription }
			waitFor { notifications.flashMessageText.contains("updated") }
			messageList.messageText(0) != 'Test message 0'
		when:
			to PageMessageAnnouncement, 'Sample Announcement'
		then:
			waitFor { messageList.displayed }
			messageList.messageText(0) == 'Test message 0'
	}

	def "move multiple message action works"() {
		when:
			to PageMessageSubscription, 'Camping Subscription'
		then:
			waitFor { messageList.displayed }
		when:
			messageList.toggleSelect(0)
			waitFor {singleMessageDetails.displayed }
			messageList.toggleSelect(1)
		then:
			waitFor { multipleMessageDetails.displayed }
		when:
			multipleMessageDetails.moveTo(remote { Activity.findByName("Sample Announcement").id }).click()
		then:
			waitFor("veryslow") { notifications.flashMessageText.contains("updated") }
			!(messageList.messageText(0) in ['Test message 0', 'Test message 1'])
			!(messageList.messageText(1) in ['Test message 0', 'Test message 1'])
		when:
			to PageMessageAnnouncement, remote { Activity.findByName("Sample Announcement").id }
		then:
			waitFor { messageList.displayed }
			messageList.messageText(0) == 'Test message 0'
			messageList.messageText(1) == 'Test message 1'
	}

	def "moving a message from another activity to a subscription opens the categorise popup for the chosen subscription"() {
		setup:
			def activity = remote { Activity.findByName("Sample Announcement").id }
			def m = remote { TextMessage.findBySrc("announce").id }
			def subscription = remote { Subscription.findByName('Camping Subscription').id }
		when:
			to PageMessageAnnouncement, activity, m
		then:
			waitFor { singleMessageDetails.displayed }
		when:
			singleMessageDetails.moveTo(subscription)
		then:
			waitFor { at SubscriptionCategoriseDialog }
	}
}

