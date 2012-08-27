package frontlinesms2

class RequestParameter{
	String name
	String value

	static belongsTo = [connection:HttpExternalCommandFconnection]
}