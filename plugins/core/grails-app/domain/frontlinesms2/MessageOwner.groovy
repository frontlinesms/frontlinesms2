package frontlinesms2

abstract class MessageOwner {
	static hasMany = [messages: Fmessage]
	boolean archived
	boolean deleted
	
	static mapping = { messages sort: 'date' }
}
