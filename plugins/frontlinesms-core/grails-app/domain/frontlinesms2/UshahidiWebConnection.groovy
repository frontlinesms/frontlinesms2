package frontlinesms2

class UshahidiWebConnection extends WebConnection {
	static String getType() { 'ushahidi' }

	static constraints = {
	}

	def initialize(params) {
		this.addToRequestParameters(new RequestParameter(name:"s", value:'${message_src_name}'))
		this.addToRequestParameters(new RequestParameter(name:"m", value:'${message_body}'))
		this.addToRequestParameters(new RequestParameter(name:"key", value:params.key))
		//TODO Test for urls which end with /
		def modifyUrl = params.url.find(/frontlinesms/) ? params.url : params.url + "/frontlinesms"
		this.url = modifyUrl
		this.httpMethod = WebConnection.HttpMethod.GET
		this.name = params.name
		this
	}

}
