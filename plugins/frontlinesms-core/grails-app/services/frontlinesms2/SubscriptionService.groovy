package frontlinesms2

import frontlinesms2.*

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
		message.setMessageDetailValue(subscriptionOrActionStep, Subscription.Action.JOIN.toString())
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
			if(subscriptionOrActionStep.joinAutoreplyText) {
				sendAutoreplyMessage(foundContact, subscriptionOrActionStep.joinAutoreplyText)
			}
		})
	}

	def doLeave(subscriptionOrActionStep, message) {
		message.setMessageDetailValue(subscriptionOrActionStep, Subscription.Action.JOIN.toString())
		message.save(failOnError:true)
		def group = subscriptionOrActionStep.group
		def foundContact
		withEachCorrespondent(message, { phoneNumber ->
			foundContact = Contact.findByMobile(phoneNumber)
			if(foundContact) {
				if((foundContact.isMemberOf(group))){
					foundContact?.removeFromGroup(group)
				}
			}
			if(subscriptionOrActionStep.leaveAutoreplyText) {
				sendAutoreplyMessage(foundContact, subscriptionOrActionStep.leaveAutoreplyText)
			}
		})
	}

	def doToggle(subscriptionOrActionStep, message) {
		message.setMessageDetailValue(subscriptionOrActionStep, Subscription.Action.TOGGLE.toString())
		message.save(failOnError:true)
		def group = subscriptionOrActionStep.group
		def foundContact
		withEachCorrespondent(message, { phoneNumber ->
			foundContact = Contact.findByMobile(phoneNumber)
			if(foundContact){
				if(foundContact.isMemberOf(group)) {
					foundContact.removeFromGroup(group)
					if(subscriptionOrActionStep.leaveAutoreplyText)
						sendAutoreplyMessage(foundContact, subscriptionOrActionStep.leaveAutoreplyText)
				} else {
					group.addToMembers(foundContact);
					if(subscriptionOrActionStep.joinAutoreplyText)
						sendAutoreplyMessage(foundContact, subscriptionOrActionStep.joinAutoreplyText)
				}
			} else {
				foundContact = new Contact(name:"", mobile:phoneNumber).save(failOnError:true)
				group.addToMembers(foundContact);
				if(subscriptionOrActionStep.joinAutoreplyText)
					sendAutoreplyMessage(foundContact, subscriptionOrActionStep.joinAutoreplyText)
			}
		})
	}

	def withEachCorrespondent(Fmessage message, Closure c) {
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

	def sendAutoreplyMessage(Contact foundContact, autoreplyText, addToActivity=false, subscription=null) {
		def params = [:]
		params.addresses = foundContact.mobile
		params.messageText = autoreplyText
		def outgoingMessage = messageSendService.createOutgoingMessage(params)
		if(addToActivity) {
			subscription.addToMessages(outgoingMessage)
			subscription.save(failOnError:true)
		}
		else
			outgoingMessage.save(failOnError:true)
		messageSendService.send(outgoingMessage)
	}
}

