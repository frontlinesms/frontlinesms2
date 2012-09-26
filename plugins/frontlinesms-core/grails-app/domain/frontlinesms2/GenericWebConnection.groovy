package frontlinesms2

class GenericWebConnection extends WebConnection {
	static String getType() { 'generic' }

	def initialize(params) {
		this.httpMethod = WebConnection.HttpMethod."${params.httpMethod.toUpperCase()}"
		this.url = params.url
		this.name = params.name
		processRequestParameters(params)
		this
	}
}