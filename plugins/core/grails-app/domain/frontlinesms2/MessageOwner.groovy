package frontlinesms2

abstract class MessageOwner {
	String name
	static hasMany = [messages: Fmessage]
	boolean archived
	boolean deleted
	
	static mapping = {
		messages sort: 'date'
		tablePerHierarchy false
		version false
	}

	def sendToTrash() {
		this.deleted = true
		this.messages*.isDeleted = true
		return [displayName:name, text:"${messages? messages.size(): 0} message(s)"] // TODO i18n
	}

	def restoreFromTrash() {
		this.deleted = false
		this.messages*.isDeleted = false
	}
}
