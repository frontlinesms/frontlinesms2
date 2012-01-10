package frontlinesms2

import java.util.Date

class Dispatch {
	static belongsTo = Fmessage
	String dst
	DispatchStatus status
	Date dateSent
}