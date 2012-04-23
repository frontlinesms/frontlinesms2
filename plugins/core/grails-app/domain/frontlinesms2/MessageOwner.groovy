package frontlinesms2

class MessageOwner {
	static hasMany = [messages: Fmessage]
	boolean archived
	boolean deleted
	
	static mapping = { messages sort: 'date' }
}
