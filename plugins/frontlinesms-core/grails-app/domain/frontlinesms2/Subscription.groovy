package frontlinesms2

class Subscription extends Activity {
//> CONSTANTS
	static String getShortName() { 'subscription' }

//> PROPERTIES
	enum Action { TOGGLE, JOIN, LEAVE }
	String name
	Group group
	Action defaultAction = Action.TOGGLE
	String joinAutoreplyText
	String leaveAutoreplyText
	def subscriptionService

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

	def processJoin(TextMessage message){
		println "I AM ABOUT TO CALL DO JOIN ON $subscriptionService"
		this.addToMessages(message)
		this.save(failOnError:true)
		subscriptionService.doJoin(this, message)
	}

	def processLeave(TextMessage message){
		this.addToMessages(message)
		this.save(failOnError:true)
		println "I AM ABOUT TO CALL DO LEAVE ON $subscriptionService"
		subscriptionService.doLeave(this, message)
	}

	def processToggle(TextMessage message){
		this.addToMessages(message)
		this.save(failOnError:true)
		println "I AM ABOUT TO CALL DO TOGGLE ON $subscriptionService"
		subscriptionService.doToggle(this, message)
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

	def processKeyword(TextMessage message, Keyword k) {
		// TODO: Should add message to activity at this point
		this.addToMessages(message)
		this.save(failOnError:true)
		def action = getAction(k)
		if(action == Action.JOIN){
			processJoin(message)
		}else if(action == Action.LEAVE) {
			processLeave(message)
		}else if(action == Action.TOGGLE) {
			processToggle(message)
		}
	}

	Action getAction(Keyword k) {
		def actionText = k?.ownerDetail
		println "### OwnerDetail ## ${k?.ownerDetail}"
		if(actionText == Action.JOIN.toString()){
			return Action.JOIN
		} else if(actionText == Action.LEAVE.toString()){
			return Action.LEAVE
		}else {
			return defaultAction
		}
	}

	def hasAtLeastOneAlias(aliases,message) {
		aliases && aliases.toUpperCase().split(",").contains(message.substring(keyword.value.length()))	
	}

	def getDisplayText(TextMessage msg) {
		if ((msg.messageOwner.id == this.id) && msg.ownerDetail && msg.inbound) {
			return (msg.ownerDetail?.toLowerCase() + ' ("' + msg.text + '")').truncate(50) // FIXME probably shouldn't truncate here
		} else
			return msg.text
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
}


