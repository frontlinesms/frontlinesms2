package frontlinesms2

class GenericWebconnection extends Webconnection {
	static String getType() { 'generic' }

	def initialize(params) {
		this.httpMethod = Webconnection.HttpMethod."${params.httpMethod.toUpperCase()}"
		this.url = params.url
		this.name = params.name
		processRequestParameters(params)
		this
	}

	def getServiceType() {'generic'}
}