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
}
