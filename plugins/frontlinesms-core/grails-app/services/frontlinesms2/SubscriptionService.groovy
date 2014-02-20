package frontlinesms2

// TODO TODO TODO this class needs a serious refactor as there's masses of copy.pasted code
class SubscriptionService {
	def messageSendService

	def saveInstance(Subscription subscriptionInstance, params) {
		subscriptionInstance.group = Group.get(params.subscriptionGroup)
		if(subscriptionInstance.keywords)
			subscriptionInstance.keywords.clear()

		def defaultAction = params.defaultAction ? params.defaultAction.toUpperCase() : Subscription.Action.JOIN.toString()
		subscriptionInstance.defaultAction = Subscription.Action."${defaultAction}"
		subscriptionInstance.joinAutoreplyText = params.joinAutoreplyText
		subscriptionInstance.leaveAutoreplyText = params.leaveAutoreplyText
		subscriptionInstance.name = params.name

		subscriptionInstance.save(failOnError:true, flush:true)
		
		if(params.topLevelKeywords?.trim()) {
			params.topLevelKeywords.toUpperCase().replaceAll(/\s/, "").split(",").each {
				subscriptionInstance.addToKeywords(new Keyword(value: it, isTopLevel:true))
			}
		}
		if(params.joinKeywords?.trim()){
			params.joinKeywords.toUpperCase().replaceAll(/\s/, "").split(",").each {
				subscriptionInstance.addToKeywords(new Keyword(value: it, isTopLevel:!params.topLevelKeywords, ownerDetail: Subscription.Action.JOIN.toString()))
			}
		}
		if(params.leaveKeywords?.trim()){
			params.leaveKeywords.toUpperCase().replaceAll(/\s/, "").split(",").each {
				subscriptionInstance.addToKeywords(new Keyword(value: it, isTopLevel:!params.topLevelKeywords, ownerDetail: Subscription.Action.LEAVE.toString()))
			}
		}
		subscriptionInstance.save(flush:true, failOnError: true)
		return subscriptionInstance
	}

	def doJoin(subscriptionOrActionStep, message) {
		message.setMessageDetail(subscriptionOrActionStep, Subscription.Action.JOIN.toString())
		message.save(failOnError:true)
		def group = subscriptionOrActionStep.group
		def foundContact
		withEachCorrespondent(message, { phoneNumber ->
			foundContact = Contact.findByMobile(phoneNumber)
			if(!foundContact) {
				foundContact = new Contact(name:"", mobile:phoneNumber).save(failOnError:true)
				group.addToMembers(foundContact);
			} else {
				if(!(foundContact.isMemberOf(group))){
					group.addToMembers(foundContact);
				}
			}
			if(subscriptionOrActionStep instanceof Activity && subscriptionOrActionStep.joinAutoreplyText) {
				sendAutoreplyMessage(foundContact, subscriptionOrActionStep.joinAutoreplyText, message)
			}
		})
	}

	def doLeave(subscriptionOrActionStep, message) {
		message.setMessageDetail(subscriptionOrActionStep, Subscription.Action.LEAVE.toString())
		message.save(failOnError:true)
		def group = subscriptionOrActionStep.group
		def foundContact
		withEachCorrespondent(message, { phoneNumber ->
			foundContact = Contact.findByMobile(phoneNumber)
			if(foundContact) {
				if((foundContact.isMemberOf(group))){
					foundContact?.removeFromGroup(group)
				}
				if(subscriptionOrActionStep instanceof Activity && subscriptionOrActionStep.leaveAutoreplyText) {
					sendAutoreplyMessage(foundContact, subscriptionOrActionStep.leaveAutoreplyText, message)
				}
			}
		})
	}

	def doToggle(subscriptionOrActionStep, message) {
		message.setMessageDetail(subscriptionOrActionStep, Subscription.Action.TOGGLE.toString())
		message.save(failOnError:true)
		def group = subscriptionOrActionStep.group
		def foundContact
		withEachCorrespondent(message, { phoneNumber ->
			foundContact = Contact.findByMobile(phoneNumber)
			if(foundContact){
				if(foundContact.isMemberOf(group)) {
					foundContact.removeFromGroup(group)
					if(subscriptionOrActionStep instanceof Activity && subscriptionOrActionStep.leaveAutoreplyText)
						sendAutoreplyMessage(foundContact, subscriptionOrActionStep.leaveAutoreplyText, message)
				} else {
					group.addToMembers(foundContact);
					if(subscriptionOrActionStep instanceof Activity && subscriptionOrActionStep.joinAutoreplyText)
						sendAutoreplyMessage(foundContact, subscriptionOrActionStep.joinAutoreplyText, message)
				}
			} else {
				foundContact = new Contact(name:"", mobile:phoneNumber).save(failOnError:true)
				group.addToMembers(foundContact);
				if(subscriptionOrActionStep instanceof Activity && subscriptionOrActionStep.joinAutoreplyText)
					sendAutoreplyMessage(foundContact, subscriptionOrActionStep.joinAutoreplyText, message)
			}
		})
	}

	def withEachCorrespondent(TextMessage message, Closure c) {
		def phoneNumbers = []
		if (message.inbound)
			phoneNumbers << message.src
		else {
			message.dispatches.each { d->
				phoneNumbers << d.dst
			}
		}
		if (phoneNumbers.size() > 0) {
			phoneNumbers.each { phoneNumber ->
				c phoneNumber
			}
		}
	}

	def sendAutoreplyMessage(Contact foundContact, autoreplyText, incomingMessage) {
		def subscription = incomingMessage.messageOwner
		def params = [:]
		params.addresses = foundContact.mobile
		params.messageText = autoreplyText
		def outgoingMessage = messageSendService.createOutgoingMessage(params)
		subscription.addToMessages(outgoingMessage)
		subscription.save(failOnError:true)
		outgoingMessage.setMessageDetail(subscription, incomingMessage.id)
		outgoingMessage.save(failOnError:true)
		messageSendService.send(outgoingMessage)
	}
}

