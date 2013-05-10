package frontlinesms2

class UshahidiWebconnection extends Webconnection {
	static String getType() { 'ushahidi' }
	static constraints = {
	}

	def initialize(params) {
		this.addToRequestParameters(name:"s", value:'${message_src_number}')
		this.addToRequestParameters(name:"m", value:'${message_body}')
		this.addToRequestParameters(name:"key", value:params.key)
		//TODO Test for urls which end with /
		this.url = params.url
		this.httpMethod = Webconnection.HttpMethod.GET
		this.name = params.name
		// API setup
		this.apiEnabled = false //params.enableApi?:false <- replace with this to re-enable in future
		// this.secret = params.secret <- uncomment to re-enable in future
		this
	}

	def getServiceType() {
		url ==~ 'https://.*\\.crowdmap.com/frontlinesms/' ? "crowdmap" : "ushahidi"
	}

	def getKey() {
		requestParameters?.find {it.name == "key"}?.value
	}
}

