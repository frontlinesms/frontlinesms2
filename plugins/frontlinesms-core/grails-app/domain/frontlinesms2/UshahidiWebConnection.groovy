package frontlinesms2

class UshahidiWebConnection extends WebConnection {
	static String getType() { 'ushahidi' }

	static constraints = {
	}

	def initialize(params) {
		def sender = new RequestParameter(name:"s", value:"${message_src_name}").save()
		def messageBody = new RequestParameter(name:"m", value:"${message_body}").save()
		def key = new RequestParameter(name:"key", value:params.key).save()
		this.addToRequestParameters(sender)
		this.addToRequestParameters(messageBody)
		this.addToRequestParameters(key)
		//TODO Test for urls which end with /
		def modifyUrl = params.url.find(/frontlinesms/) ? params.url : params.url + "/frontlinesms"
		this.url = modifyUrl
		this.httpMethod = HttpMethod.GET
		this
	}

}
