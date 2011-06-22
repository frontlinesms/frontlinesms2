package frontlinesms2

class Fmessage {
	String src
	String dst
	String text
	String displaySrc
	Date dateCreated
	Date dateReceived
	boolean contactExists
	boolean inbound
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
				eq("inbound", true)
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
				eq("inbound", false)
				isNull("messageOwner")
			}
			order("dateCreated", "desc")
		}
		messages
	}
}
