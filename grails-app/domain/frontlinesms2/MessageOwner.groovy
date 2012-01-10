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
	}
}