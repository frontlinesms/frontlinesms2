package frontlinesms2

class MessageOwner {
	static hasMany = [messages: Fmessage]
	static transients  = ['type']
	boolean archived
	boolean deleted
	
	static mapping = { messages sort: 'date' }
}
