package frontlinesms2

import groovy.time.*

class Interaction {

	static belongsTo = [messageOwner:MessageOwner]
	static transients = ['hasSent', 'hasPending', 'hasFailed', 'displayName' ,'outboundContactList', 'read', 'receivedOn']
	
	Date date = new Date() // No need for dateReceived since this will be the same as date for received messages and the Dispatch will have a dateSent
	Date dateCreated // This is unused and should be removed, but doing so throws an exception when running the app and I cannot determine why
	
	String src
	String outboundContactName
	String inboundContactName
	Long connectionId
	boolean rd
	boolean starred
	boolean archived
	boolean isDeleted
	boolean inbound

	static mapping = {
		table 'fmessage'
		tablePerHierarchy true
		sort date:'desc'
		inboundContactName formula:'(SELECT c.name FROM contact c WHERE c.mobile=src)'
		outboundContactName formula:'(SELECT MAX(c.name) FROM contact c, dispatch d WHERE c.mobile=d.dst AND d.message_id=id)'
		version false
	}
	
	static constraints = {
		messageOwner nullable:true
		src(nullable:true, validator: { val, obj ->
				val || !obj.inbound
		})
		inboundContactName nullable:true
		outboundContactName nullable:true
		archived(nullable:true, validator: { val, obj ->
				obj.messageOwner == null || obj.messageOwner.archived == val
		})
		connectionId nullable:true
	}

	def beforeInsert = {
		if(!this.inbound) this.read = true
	}

	def getReceivedOn() {
		Fconnection.get(this.connectionId)
	}

	static namedQueries = {
		deleted { getOnlyStarred=false ->
			and {
				eq("isDeleted", true)
				eq("archived", false)
				if(getOnlyStarred)
					eq('starred', true)
			}
		}
		owned { MessageOwner owner, boolean getOnlyStarred=false, getSent=null ->
			and {
				eq("isDeleted", false)
				eq("messageOwner", owner)
				if(getOnlyStarred)
					eq("starred", true)
				if(getSent != null)
					eq("inbound", getSent)
			}
		}
		unread { MessageOwner owner=null ->
			and {
				eq("isDeleted", false)
				eq("archived", false)
				eq('rd', false)
				if(owner == null)
					isNull("messageOwner")
				else
					eq("messageOwner", owner)
			}
		}
		totalUnread {
			and {
				eq("isDeleted", false)
				eq("archived", false)
				eq('rd', false)
			}
		}
		search { ids ->
			'in'('id', ids)
		}
	}

	def getDisplayName() {
		return "Override me"
	}

	private boolean isMoveAllowed() {
		if(this.messageOwner){
			return !(this.messageOwner?.archived)
		} else {
			return (!this.isDeleted && !this.archived)
		}
	}

	public boolean isRead() { return this.rd }
	public boolean setRead(boolean read) { this.rd = read }
	
	static def countUnreadMessages() {
		Interaction.unread.count()
	}

	static def countUnreadMessages(owner) {
		Interaction.unread(owner).count()
	}

	static def countTotalUnreadMessages() {
		Interaction.totalUnread.count()
	}
	
	//TODO: Remove in Groovy 1.8 (Needed for 1.7)
	private static def countAnswer(final Map<Object, Integer> answer, Object mappedKey) {
		if (!answer.containsKey(mappedKey)) {
			answer.put(mappedKey, 0)
		}
		int current = answer.get(mappedKey)
		answer.put(mappedKey, current + 1)
	}

	private def getOwnerType(owner) {
		owner instanceof Step ? MessageDetail.OwnerType.STEP : MessageDetail.OwnerType.ACTIVITY
	}
}

