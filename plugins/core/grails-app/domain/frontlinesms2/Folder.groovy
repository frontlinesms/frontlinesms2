package frontlinesms2

import java.util.Date

class Folder extends MessageOwner{
	static transients = ['liveMessageCount']
	String name
	Date dateCreated
	String type = 'folder'
	
	static constraints = {
		name(blank:false, nullable:false, maxSize:255)
	}
	
	def getFolderMessages(getOnlyStarred=false, getSent=true) {
		Fmessage.owned(getOnlyStarred, this, getSent)
	}
	
	def archive() {
		this.archived = true
		def messagesToArchive = Fmessage.owned(false, this, true)?.list()
		messagesToArchive.each { it?.archived = true }
	}
	
	def unarchive() {
		this.archived = false
		def messagesToArchive = Fmessage?.owned(false, this, true)?.list()
		messagesToArchive.each { it?.archived = false }
	}
	
	def getLiveMessageCount() {
		def m = Fmessage.findAllByMessageOwnerAndIsDeleted(this, false)
		m ? m.size() : 0
	}
}
