package frontlinesms2

import org.apache.camel.*
import org.apache.camel.Exchange
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.model.RouteDefinition
import frontlinesms2.camel.exception.*

abstract class Webconnection extends Activity {
	def camelContext
	def webconnectionService
	enum HttpMethod { POST, GET }
	static String getShortName() { 'webconnection' }
	static String getType() { '' }
	static def implementations = [GenericWebconnection,
			UshahidiWebconnection]

	// Camel route redelivery config
	static final def retryAttempts = 3 // how many times to retry before giving up
	static final def initialRetryDelay = 10000 // delay before first retry, in milliseconds
	static final def delayMultiplier = 3 // multiplier applied to delay after each failed attempt

	// Substitution variables
	static subFields = ['message_body' : { msg ->
			def keyword = msg.messageOwner?.keyword?.value
			def text = msg.text
			if (keyword?.size() && text.toUpperCase().startsWith(keyword.toUpperCase())) {
				text = text.substring(keyword.size()).trim()
			}
			text
		},
		'message_body_with_keyword' : { msg -> msg.text },
		'message_src_number' : { msg -> msg.src },
		'message_src_name' : { msg -> Contact.findByMobile(msg.src)?.name ?: msg.src },
		'message_timestamp' : { msg -> msg.dateCreated }]
	
	/// Variables
	String url
	HttpMethod httpMethod
	static hasMany = [requestParameters:RequestParameter]
	
	static constraints = {
		name(blank:false, maxSize:255, validator: { val, obj ->
			if(obj?.deleted || obj?.archived) return true
			def identical = Webconnection.findAllByNameIlike(val)
			if(!identical) return true
			else if (identical.any { it.id != obj.id && !it?.archived && !it?.deleted }) return false
			else return true
			})
	}
	static mapping = {
		requestParameters cascade: "all-delete-orphan"
		tablePerHierarchy false
	}

	def processKeyword(Fmessage message, Keyword k) {
		this.addToMessages(message)
		this.save(failOnError:true)
		webconnectionService.send(message)
	}

	List<RouteDefinition> getRouteDefinitions() {
		return new RouteBuilder() {
			@Override void configure() {}
			List getRouteDefinitions() {
				return [from("seda:activity-webconnection-${Webconnection.this.id}")
						.beanRef('webconnectionService', 'preProcess')
						.setHeader(Exchange.HTTP_PATH,
								simple('${header.url}'))
						.onException(Exception)
									.redeliveryDelay(initialRetryDelay)
									.backOffMultiplier(delayMultiplier)
									.maximumRedeliveries(retryAttempts)
									.retryAttemptedLogLevel(LoggingLevel.WARN)
									.handled(true)
									.beanRef('webconnectionService', 'handleException')
									.end()
						.to(Webconnection.this.url)
						.beanRef('webconnectionService', 'postProcess')
						.routeId("activity-webconnection-${Webconnection.this.id}")]
			}
		}.routeDefinitions
	}

	def activate() {
		println "*** ACTIVATING ACTIVITY ***"
		try {
			def routes = this.routeDefinitions
			camelContext.addRouteDefinitions(routes)
			println "################# Activating Webconnection :: ${this}"
			LogEntry.log("Created Webconnection routes: ${routes*.id}")
		} catch(FailedToCreateProducerException ex) {
			println ex
		} catch(Exception ex) {
			println ex
			camelContext.stopRoute("activity-webconnection-${this.id}")
			camelContext.removeRoute("activity-webconnection-${this.id}")
		}
	}

	def deactivate() {
		println "################ Deactivating Webconnection :: ${this}"
		camelContext.stopRoute("activity-webconnection-${this.id}")
		camelContext.removeRoute("activity-webconnection-${this.id}")
	}

	abstract def initialize(params)
	abstract def getServiceType()

	def preProcess(Exchange x) {
		println "x: ${x}"
		println "x.in: ${x.in}"
		println "x.in.headers: ${x.in.headers}"
		def fmessage = Fmessage.get(x.in.headers.'fmessage-id')
		def encodedParameters = this.requestParameters.collect {
			urlEncode(it.name) + '=' + urlEncode(it.getProcessedValue(fmessage))
		}.join('&')
		println "PARAMS:::$encodedParameters"

		x.out.headers[Exchange.HTTP_METHOD] = this.httpMethod
		switch(this.httpMethod) {
			case 'GET':
				x.out.headers[Exchange.HTTP_QUERY] = encodedParameters
				break;
			case 'POST':
				x.out.body = encodedParameters
				x.out.headers[Exchange.CONTENT_TYPE] = 'application/x-www-form-urlencoded'
				break;
		}
		println "x.out.headers = $x.out.headers"
		println "x.out.body = $x.out.body"
	}

	def postProcess(Exchange x) {
		println "Web Connection Response::\n ${x.in.body}"
		log.info "Web Connection Response::\n ${x.in.body}"
	}

	private def processRequestParameters(params) {
		def paramsName = params.'param-name'
		def paramsValue = params.'param-value'
		this.requestParameters?.clear()
		if(paramsName instanceof String[]) {
			paramsName?.size()?.times {
				addRequestParameter(paramsName[it], paramsValue[it])
			}
		} else { if(paramsName) addRequestParameter(paramsName, paramsValue)}
	}

	private def addRequestParameter(name, value) {
		def requestParam = new RequestParameter(name:name, value:value)
		this.addToRequestParameters(requestParam)
	}

	private String urlEncode(String s) throws UnsupportedEncodingException {
		println "PreProcessor.urlEncode : s=$s -> ${URLEncoder.encode(s, "UTF-8")}"
		return URLEncoder.encode(s, "UTF-8");
	}
}
	
