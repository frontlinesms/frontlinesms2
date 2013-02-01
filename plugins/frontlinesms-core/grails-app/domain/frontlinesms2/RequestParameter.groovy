package frontlinesms2

class RequestParameter {
	String name
	String value

	static constraints = {
		name(blank:false)
		value(blank:false)
	}

	static belongsTo = [connection:Webconnection]

	

}