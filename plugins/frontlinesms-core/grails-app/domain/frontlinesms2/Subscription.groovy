package frontlinesms2

class Subscription extends Activity{
//> CONSTANTS
	static String getShortName() { 'subscription' }

//> PROPERTIES
	enum Action { TOGGLE, JOIN, LEAVE }
	String name
	Group group
	Action defaultAction = Action.TOGGLE
	String joinAliases
	String leaveAliases
	String joinAutoreplyText
	String leaveAutoreplyText

//> SERVICES
	def messageSendService

	static constraints = {
		name(blank:false, maxSize:255, validator: { val, obj ->
				if(obj?.deleted || obj?.archived) return true
				def identical = Subscription.findAllByNameIlike(val)
				if(!identical) return true
				else if (identical.any { it.id != obj.id && !it?.archived && !it?.deleted }) return false
				else return true
			})
		joinAutoreplyText nullable:true, blank:false
		leaveAutoreplyText nullable:true, blank:false
	}

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

	def sendAutoreplyMessage(Contact foundContact, autoreplyText) {
		def params = [:]
		params.addresses = foundContact.mobile
		params.messageText = autoreplyText
		def outgoingMessage = messageSendService.createOutgoingMessage(params)
		this.addToMessages(outgoingMessage)
		this.save()
		messageSendService.send(outgoingMessage)
	}

	// TODO this should just call processJoin or processLeave
	def processToggle(Fmessage message){
		this.addToMessages(message)
		this.save()
		message.ownerDetail = Action.TOGGLE.toString()
		message.save(failOnError:true)
		withEachCorrespondent(message, { phoneNumber ->
			def foundContact = Contact.findByMobile(phoneNumber)
			if(foundContact){
				if(foundContact.isMemberOf(group)) {
					foundContact.removeFromGroup(group)
					if(leaveAutoreplyText)
						sendAutoreplyMessage(foundContact, leaveAutoreplyText)
				} else {
					group.addToMembers(foundContact);
					if(joinAutoreplyText)
						sendAutoreplyMessage(foundContact, joinAutoreplyText)
				}
			} else {
				foundContact = new Contact(name:"", mobile:phoneNumber).save(failOnError:true)
				group.addToMembers(foundContact);
				if(joinAutoreplyText)
					sendAutoreplyMessage(foundContact, joinAutoreplyText)
			}
		})
	}

	def processKeyword(Fmessage message, Keyword k) {
		def action = getAction(k)
		message.ownerDetail = action.toString()
		if(action == Action.JOIN){
			processJoin(message)
		}else if(action == Action.LEAVE) {
			processLeave(message)
		}else if(action == Action.TOGGLE) {
			processToggle(message)
		}
	}

	Action getAction(Keyword k) {
		def actionText = k.ownerDetail
		println "### OwnerDetail ## ${k.ownerDetail}"
		if(actionText == Action.JOIN.toString()){
			return Action.JOIN
		} else if(actionText == Action.LEAVE.toString()){
			return Action.LEAVE
		}else if(actionText == null){
			return defaultAction
		}
	}

	def hasAtLeastOneAlias(aliases,message) {
		aliases && aliases.toUpperCase().split(",").contains(message.substring(keyword.value.length()))	
	}

	def getDisplayText(Fmessage msg) {
		if ((msg.messageOwner.id == this.id) && msg.ownerDetail) {
			return (msg.ownerDetail?.toLowerCase() + ' ("' + msg.text + '")').truncate(50) // FIXME probably shouldn't truncate here
		} else
			return msg.text
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
}


