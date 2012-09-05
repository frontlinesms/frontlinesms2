package frontlinesms2

class RequestParameter{
	String name
	String value
	static def regex = /[$][{]*[a-z_]*[}]/

	static belongsTo = [connection:WebConnection]

	String getProcessedValue(Fmessage msg) {
		def val = this.value
		def matches = val.findAll(regex)
		matches.each { match ->
			val = val.replaceFirst(regex, getReplacement(match, msg))
		}
		return val
	}

	String getReplacement(String arg, Fmessage msg) {
		arg = (arg - '${') - '}'
		def c = WebConnection.subFields[arg]
		return c msg
	}

}