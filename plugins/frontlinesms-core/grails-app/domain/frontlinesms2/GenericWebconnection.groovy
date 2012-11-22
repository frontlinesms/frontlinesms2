package frontlinesms2

class GenericWebconnection extends Webconnection {
	static String getType() { 'generic' }

	def initialize(params) {
		this.httpMethod = Webconnection.HttpMethod."${params.httpMethod.toUpperCase()}"
		this.url = params.url
		this.name = params.name
		// API setup
		this.apiEnabled = params.enableApi?:false
		this.secret = params.secret

		processRequestParameters(params)
		this
	}

	def getServiceType() {'generic'}

	private def processRequestParameters(params) {
		def paramsName = params.'param-name'
		def paramsValue = params.'param-value'
		this.requestParameters?.clear()
		if(paramsName instanceof String[]) {
			paramsName?.size()?.times {
				addToRequestParameters(name:paramsName[it], value:paramsValue[it])
			}
		} else if(paramsName) {
			addToRequestParameters(name:paramsName, value:paramsValue)
		}
	}
}

