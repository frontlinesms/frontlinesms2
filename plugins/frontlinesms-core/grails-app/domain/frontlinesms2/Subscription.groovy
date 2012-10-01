package frontlinesms2

class Subscription extends Activity{
//> CONSTANTS
	static String getShortName() { 'subscription' }

//> PROPERTIES
	enum Action { TOGGLE, JOIN, LEAVE }
	String name
	static hasOne = [keyword: Keyword]
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
		keyword nullable:true
	}

	def processJoin(Fmessage message){
		this.addToMessages(message)
		this.save()
		message.ownerDetail = Action.JOIN.toString()
		message.save(failOnError:true, flush:true)
		withEachCorrespondent(message, { phoneNumber ->
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
		message.save(failOnError:true, flush:true)
		withEachCorrespondent(message, { phoneNumber ->
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

	def processKeyword(Fmessage message, boolean exactMatch) {
		def action = getAction(message.text,exactMatch)
		message.ownerDetail = action.toString()
		if(action == Action.JOIN){
			processJoin(message)
		}else if(action == Action.LEAVE) {
			processLeave(message)
		}else if(action == Action.TOGGLE) {
			processToggle(message)
		}
		this.save(failOnError:true)
	}

	Action getAction(String messageText, boolean exactMatch) {
		def words =  messageText.trim().toUpperCase().split(/\s+/)
		if(words.size() == 1){
			if(hasAtLeastOneAlias(joinAliases,words[0])){
				return Action.JOIN
			}else if(hasAtLeastOneAlias(leaveAliases,words[0])){
				return Action.LEAVE
			}else if (exactMatch){
				return defaultAction
			}else if(!exactMatch){
				println "################## Why are you called? You are not a match."
				return defaultAction;
			}
		}
		else if (words.size() > 1 && exactMatch) {
			if(joinAliases.toUpperCase().split(",").contains(words[1].toUpperCase().trim())) {
				return Action.JOIN
		 	} else if(leaveAliases.toUpperCase().split(",").contains(words[1].toUpperCase().trim())) {
		 		return Action.LEAVE
		 	} else {
		 		return defaultAction
		 	}
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


