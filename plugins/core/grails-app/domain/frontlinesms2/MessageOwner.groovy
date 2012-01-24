package frontlinesms2

class MessageOwner {
	static hasMany = [messages: Fmessage]
	boolean archived
	boolean deleted
}
