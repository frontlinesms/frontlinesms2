package frontlinesms2

import frontlinesms2.enums.MessageStatus

class Fmessage {
	String src
	String dst
	String text
	String displaySrc
	Date dateCreated
	Date dateReceived
	boolean contactExists
	MessageStatus status
	boolean read
	boolean deleted
	boolean starred
	static belongsTo = [messageOwner:MessageOwner]
	static transients = ['displaySrc']
	static mapping = {
		sort dateCreated:'desc'
		sort dateReceived:'desc'
	}

	static constraints = {
		src(nullable:true)
		dst(nullable:true)
		text(nullable:true)
		messageOwner(nullable:true)
		dateReceived(nullable:true)
		status(nullable:true)
	}

	def getDisplayText() {
		def p = PollResponse.withCriteria {
			messages {
				eq('deleted', false)
				eq('id', this.id)
			}
		}

		p?.size()?"${p[0].value} (\"${this.text}\")":this.text
	}
	
	def updateDisplaySrc() {
		if(src) {
			def c = Contact.findByAddress(src)
			displaySrc = c? c.name: src
			contactExists = c? true: false
		}
	}
	
	def toDelete() {
		this.deleted = true
		this
	}

	def addStar() {
		this.starred = true
		this
	}
	
	def removeStar() {
		this.starred = false
		this
	}
	static def getFolderMessages(folderId) {
		def folder = Folder.get(folderId)
		def messages = Fmessage.createCriteria().list {
			and {
				eq("deleted", false)
				eq("messageOwner", folder)
			}
			order('dateReceived', 'desc')
		}
		messages
	}

	static def getInboxMessages() {
		def messages = Fmessage.createCriteria().list {
			and {
				eq("deleted", false)
				eq("status", MessageStatus.INBOUND)
				isNull("messageOwner")
			}
			order('dateReceived', 'desc')
		}
		messages
	}

	static def getSentMessages() {
		def messages = Fmessage.createCriteria().list {
			and {
				eq("deleted", false)
				eq("status", MessageStatus.SENT)
				isNull("messageOwner")
			}
			order("dateCreated", "desc")
		}
		messages
	}

	static def getPendingMessages() {
		def messages = Fmessage.createCriteria().list {
			and {
				eq("deleted", false)
				isNull("messageOwner")
				'in'("status", [MessageStatus.SEND_PENDING, MessageStatus.SEND_FAILED])
			}
			order("dateCreated", "desc")
		}
		messages
	}

	static def getDeletedMessages() {
		def messages = Fmessage.createCriteria().list {
			and {
				eq("deleted", true)
			}
			order("dateCreated", "desc")
		}
		messages
	}
	static def search(String searchString=null, Group groupInstance=null, Collection<MessageOwner> messageOwner=[]) {
		if(searchString) {
			def groupContactAddresses = groupInstance?.getMembers()*.address
			if(!groupContactAddresses) {
				groupContactAddresses = "null"
			}
			def results = Fmessage.createCriteria().list {
				ilike("text", "%${searchString}%")
				and{
					if(groupInstance) {
						'in'("src",  groupContactAddresses)
					}
					if(messageOwner) {
						'in'("messageOwner", messageOwner)
					}
					eq('deleted', false)
				}
				order('dateReceived', 'desc')
				order('dateCreated', 'desc')
			}
			results*.updateDisplaySrc()
			results
		} else {
			[]
		}
	}
}
