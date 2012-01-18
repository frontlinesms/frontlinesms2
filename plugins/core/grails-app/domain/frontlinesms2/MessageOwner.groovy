package frontlinesms2

import java.util.Date;

class MessageOwner {
	static hasMany = [messages:Fmessage]
	Date dateCreated
	boolean archived
	boolean deleted

	static mapping = {
		messages cascade:'all'
		messages sort:'dateCreated'
		messages sort:'dateReceived'
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
