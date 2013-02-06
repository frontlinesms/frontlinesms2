package frontlinesms2

import org.apache.camel.*
import org.apache.camel.Exchange
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.model.RouteDefinition
import frontlinesms2.camel.exception.*

class WebconnectionActionStep extends Step {
	def webconnectionService
	static service = 'webconnection'
	static action = 'doUpload'
	static String getShortName() { 'webconnectionStep' }

	static configFields = [httpMethod: [Webconnection.HttpMethod.GET, Webconnection.HttpMethod.POST], url:'url', params:[:]]

	static constraints = {
	}

	Map getConfig() {
		[stepId:id, method:httpMethod, urlEncode:url, params:params]
	}

	def getHttpMethod() {
		getPropertyValue("httpMethod")
	}

	def setHttpMethod(String method) {
		setPropertyValue("httpMethod", method)
	}

	def getUrl() {
		getPropertyValue("url")
	}

	def setUrl(String url) {
		setPropertyValue("url", url)
	}

	def getParams() {
		def params = []
		this.stepProperties?.each { property->
			if((property.key =! "url") && (property.key =! "method") ) {
				params << new RequestParameter(name:property.key, value:property.value)
			}
		}
		return params
	}
	
	def process(Fmessage message) {
		webconnectionService.doUpload(this, message)
	}

	def getNiceFormat() {
		"Upload to '${this.url}'"
	}

	List<RouteDefinition> getRouteDefinitions() {
		return new RouteBuilder() {
			@Override void configure() {}
			List getRouteDefinitions() {
				return [from("seda:activity-${WebconnectionActionStep.shortName}-${WebconnectionActionStep.this.id}")
						.beanRef('webconnectionService', 'preProcess')
						.setHeader(Exchange.HTTP_PATH, simple('${header.url}'))
						.onException(Exception)
									.redeliveryDelay(Webconnection.initialRetryDelay)
									.backOffMultiplier(Webconnection.delayMultiplier)
									.maximumRedeliveries(Webconnection.retryAttempts)
									.retryAttemptedLogLevel(LoggingLevel.WARN)
									.handled(true)
									.beanRef('webconnectionService', 'handleException')
									.end()
						.to(WebconnectionActionStep.this.url)
						.beanRef('webconnectionService', 'postProcess')
						.routeId("activity-${WebconnectionActionStep.shortName}-${WebconnectionActionStep.this.id}")]
			}
		}.routeDefinitions
	}

	def activate() {
		webconnectionService.activate(this)
	}

	def deactivate() {
		webconnectionService.deactivate(this)
	}

	def preProcess(Exchange x) {
		println "x: ${x}"
		println "x.in: ${x.in}"
		println "x.in.headers: ${x.in.headers}"
		def fmessage = Fmessage.get(x.in.headers.'fmessage-id')
		def encodedParameters = this.stepProperties?.collect {
			urlEncode(it.key) + '=' + urlEncode(webconnectionService.getProcessedValue(it, fmessage))
		}.join('&')
		println "PARAMS:::$encodedParameters"
		x.in.headers[Exchange.HTTP_PATH] = this.url
		x.in.headers[Exchange.HTTP_METHOD] = this.httpMethod
		switch(this.httpMethod) {
			case 'GET':
				x.in.headers[Exchange.HTTP_QUERY] = encodedParameters
				break;
			case 'POST':
				x.in.body = encodedParameters
				x.in.headers[Exchange.CONTENT_TYPE] = 'application/x-www-form-urlencoded'
				break;
		}
		println "# Exchange after adding other headers in the Webconnection.preProcess()"
		println "x.in.headers = $x.in.headers"
		println "x.in.body = $x.in.body"
		x
	}

	def postProcess(Exchange x) {
		println "###### WebconnectionActionStep.postProcess() with Exchange # ${x}"
		println "Web Connection ActionStep Response::\n ${x.in.body}"
		log.info "Web Connection ActionStep Response::\n ${x.in.body}"
		x
	}

	private String urlEncode(String s) throws UnsupportedEncodingException {
		println "PreProcessor.urlEncode : s is $s"
		println "PreProcessor.urlEncode : s=$s -> ${URLEncoder.encode(s, "UTF-8")}"
		return URLEncoder.encode(s, "UTF-8");
	}

}
