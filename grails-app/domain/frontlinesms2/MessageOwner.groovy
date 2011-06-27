package frontlinesms2

class MessageOwner {
	static hasMany = [messages:Fmessage]

	static mapping = {
		messages cascade:'all'
		messages sort:'dateCreated'
		messages sort:'dateReceived'
	}
}