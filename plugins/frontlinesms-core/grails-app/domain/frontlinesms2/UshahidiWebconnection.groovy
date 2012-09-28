package frontlinesms2

import org.apache.camel.Exchange

class UshahidiWebconnection extends Webconnection {
	static String getType() { 'ushahidi' }
	static constraints = {
	}

	def initialize(params) {
		this.addToRequestParameters(new RequestParameter(name:"s", value:'${message_src_number}'))
		this.addToRequestParameters(new RequestParameter(name:"m", value:'${message_body}'))
		this.addToRequestParameters(new RequestParameter(name:"key", value:params.key))
		//TODO Test for urls which end with /
		def modifyUrl = params.url.endsWith(/\/frontlinesms/) ? params.url : params.url + "/frontlinesms"
		this.url = modifyUrl
		this.httpMethod = Webconnection.HttpMethod.GET
		this.name = params.name
		this
	}

	def getServiceType() {
		url ==~ 'http://.*\\.crowdmap.com' ? "crowdmap" : "ushahidi"
	}

	def getKey() {
		requestParameters?.find {it.name == "key"}
	}
}

