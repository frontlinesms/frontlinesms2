package frontlinesms2

import java.util.Date

class Folder extends MessageOwner {
	static transients = ['liveMessageCount']
	String name
	Date dateCreated
	Date lastUpdated
	boolean archived
	boolean deleted
	
	static constraints = {
		name(blank:false, nullable:false, maxSize:255)
	}
	
	def getFolderMessages(getOnlyStarred = false) {
		Fmessage.owned(getOnlyStarred, this)
	}
	
	def archive() {
		this.archived = true
		def messagesToArchive = Fmessage?.owned(this)?.list()
		messagesToArchive.each { it?.archived = true }
	}
	
	def unarchive() {
		this.archived = false
		def messagesToArchive = Fmessage?.owned(this)?.list()
		messagesToArchive.each { it?.archived = false }
	}
		
	def getLiveMessageCount() {
		def m = Fmessage.findAllByMessageOwnerAndDeleted(this, false)
		m ? m.size() : 0
	}

    def toDelete() {
        this.deleted = true
        this
    }
}
