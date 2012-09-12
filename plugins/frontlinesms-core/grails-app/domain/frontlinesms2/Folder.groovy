package frontlinesms2

class Folder extends MessageOwner {
//> CONSTANTS
	static boolean editable = false
	static String getShortName() { 'folder' }

//> PROPERTIES
	static transients = ['liveMessageCount']
	Date dateCreated

	static mapping = {
		version false
	}

	static constraints = {
		name(blank:false, nullable:false, maxSize:255, validator: { val, obj ->
			if(obj?.deleted || obj?.archived) return true
			def identical = Folder.findAllByNameIlike(val)
			if(!identical) return true
			else if (identical.any { it.id != obj.id && !it?.archived && !it?.deleted }) return false
			else return true
			})
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
		this.messages.each {
			it.archived = true
			it.save(flush: true)
		}
	}
	
	def unarchive() {
		this.archived = false
		def messagesToArchive = Fmessage?.owned(this, false, true)?.list()
		messagesToArchive.each { it?.archived = false }
	}

	def restoreFromTrash() {
		this.deleted = false
		this.messages*.isDeleted = false
	}
}
