package frontlinesms2

import frontlinesms2.*
import org.apache.camel.*

class WebConnectionService{

	def preProcess(Exchange x) throws Exception {
		println "x: ${x}"
		println "x.in: ${x.in}"
		println "x.in.headers: ${x.in.headers}"
		def inHeader = x.in.headers
		def inMessage = Fmessage.get(inHeader.'frontlinesms.fmessageId')
		def webConn = WebConnection.get(inHeader.'frontlinesms.webConnectionId')
		def body
		def url = webConn.url
		def encodedParameters = ""
		encodedParameters += webConn.requestParameters.collect {
			urlEncode(it.name) + "=" + urlEncode(it.getProcessedValue(inMessage))
		}.join("&")
		if(webConn.httpMethod == WebConnection.HttpMethod.GET) {
			url += "?" + encodedParameters
		}
		else {
			println "PARAMS:::"+encodedParameters
			body = encodedParameters
		}
		x.out.headers = x.in.headers
		x.out.headers.url = url
		if (webConn.httpMethod == WebConnection.HttpMethod.POST) {
			x.out.headers."${Exchange.HTTP_METHOD}" = "POST"
			x.out.headers."${Exchange.CONTENT_TYPE}" = "application/x-www-form-urlencoded"
		}
		else {
			x.out.headers."${Exchange.HTTP_METHOD}" = "GET"
		}
		x.out.body = body
		println "x.out.headers = ${x.out.headers}"
		println "x.out.body = ${x.out.body}"
	}

	def postProcess(Exchange x) throws Exception {
		println "Web Connection Response::\n ${x.in.body}"
	}

	def handleException(Exchange x) {
		println "Web Connection request failed with exception: ${x.in.body}"
	}

	def send(Fmessage message){
		println "*** sending message ${message}"
		def headers = [:]
		headers.'frontlinesms.fmessageId' = message.id
		headers.'frontlinesms.webConnectionId' = message.messageOwner.id
		sendMessageAndHeaders("seda:activity-webconnection-${message.messageOwner.id}", message, headers)
	}
	
	private String urlEncode(String s) throws UnsupportedEncodingException {
		println "PreProcessor.urlEncode : s=$s -> ${URLEncoder.encode(s, "UTF-8")}"
		return URLEncoder.encode(s, "UTF-8");
	}
}
