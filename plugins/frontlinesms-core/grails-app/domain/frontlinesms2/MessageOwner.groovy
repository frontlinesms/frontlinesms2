package frontlinesms2

abstract class MessageOwner {
	String name
	static hasMany = [messages: TextMessage]
	boolean archived
	boolean deleted
	
	static mapping = {
		messages sort: 'date'
		tablePerHierarchy false
		version false
	}

	def getDisplayText(TextMessage msg) {
		msg.text
	}

	def getMoreActions() { [] }
}

