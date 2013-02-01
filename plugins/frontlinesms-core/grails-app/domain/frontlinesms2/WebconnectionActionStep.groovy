package frontlinesms2

class WebconnectionActionStep extends Step {
	def webconnectionService
	static service = 'webconnection'
	static action = 'doUpload'
	static String getShortName() { 'webconnection' }

	static configFields = [method: [Webconnection.HttpMethod.GET, Webconnection.HttpMethod.POST], url:'url', params:[:]]

	static constraints = {
	}

	Map getConfig() {
		[stepId:id, method:method, url:url, params:params]
	}

	def getMethod() {
		getPropertyValue("method")
	}

	def setMethod(String method) {
		setPropertyValue("method", method)
	}

	def getUrl() {
		getPropertyValue("url")
	}


	def setUrl(String url) {
		setPropertyValue("url", url)
	}

	def getParams() {
		getPropertyValue("params")
	}


	def setParams(params) {
		setPropertyValue("params", params)
	}
	
	def process(Fmessage message) {
		webconnectionService.doReply(this, message)
	}

	def getNiceFormat() {
		"Upload with '${this.autoreplyText}'"
	}

}
