package frontlinesms2

class Folder extends MessageOwner {
//> CONSTANTS
	static String getShortName() { 'folder' }

//> PROPERTIES
	static transients = ['liveMessageCount']
	String name
	Date dateCreated
	
	static constraints = {
		name(blank:false, nullable:false, maxSize:255)
	}

//> ACCESSORS
	def getFolderMessages(getOnlyStarred=false, getSent=true) {
		Fmessage.owned(this, getOnlyStarred, getSent)
	}
	
	def getLiveMessageCount() {
		def m = Fmessage.findAllByMessageOwnerAndIsDeleted(this, false)
		m ? m.size() : 0
	}

//> ACTION METHODS
	def archive() {
		this.archived = true
		def messagesToArchive = Fmessage.owned(this, false, true)?.list()
		messagesToArchive.each { it?.archived = true }
	}
	
	def unarchive() {
		this.archived = false
		def messagesToArchive = Fmessage?.owned(this, false, true)?.list()
		messagesToArchive.each { it?.archived = false }
	}
}
