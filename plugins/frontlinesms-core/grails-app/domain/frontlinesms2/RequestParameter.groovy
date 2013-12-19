package frontlinesms2

class RequestParameter {
	String name
	String value

	static constraints = {
		name(blank:false)
		value(blank:false)
	}

	static belongsTo = [connection:Webconnection]

	String getProcessedValue(TextMessage msg) {
		def val = this.value
		def matches = val.findAll(regex)
		matches.each { match ->
			val = val.replaceFirst(regex, getReplacement(match, msg))
		}
		return val
	}

	String getReplacement(String arg, TextMessage msg) {
		arg = (arg - '${') - '}'
		def c = Webconnection.subFields[arg]
		if (c)
			return c(msg)
		else
			return arg
	}
}