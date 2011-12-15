package frontlinesms2

class Dispatch {
	static belongsTo = Fmessage
	String dst
	DispatchStatus status
}