package frontlinesms2

import java.util.Date

class Dispatch {
	static belongsTo = [fmessage: Fmessage]
	String dst
	DispatchStatus status
	Date dateSent
}