package frontlinesms2

import frontlinesms2.*

class SubscriptionService {

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

	def doJoin(subscriptionOrJoinActionStep, message) {
	}

	def doLeave(subscriptionOrLeaveActionStep, message) {
	}

	def doToggle(subscription, message) {
	}

	// TODO: REMOVE THESE!!!:

	def processJoin(Fmessage message){
		this.addToMessages(message)
		this.save()
		message.ownerDetail = Action.JOIN.toString()
		message.save(failOnError:true)
		withEachCorrespondent(message, { phoneNumber ->
			println "##### >>>>> ${Contact.findByMobile(phoneNumber)}"
			def foundContact = Contact.findByMobile(phoneNumber)
			if(!foundContact) {
				foundContact = new Contact(name:"", mobile:phoneNumber).save(failOnError:true)
				group.addToMembers(foundContact);
			} else {
				if(!(foundContact.isMemberOf(group))){
					group.addToMembers(foundContact);
				}
			}
			if(joinAutoreplyText) {
				sendAutoreplyMessage(foundContact, joinAutoreplyText)
			}
		})
	}

	def processLeave(Fmessage message){
		this.addToMessages(message)
		this.save()
		message.ownerDetail = Action.LEAVE.toString()
		message.save(failOnError:true)
		withEachCorrespondent(message, { phoneNumber ->
			println "##### >>>>> ${Contact.findByMobile(phoneNumber)}"
			def foundContact = Contact.findByMobile(phoneNumber)
			foundContact?.removeFromGroup(group)
			if(leaveAutoreplyText && foundContact) {
				sendAutoreplyMessage(foundContact, leaveAutoreplyText)
			}
		})
	}
}

