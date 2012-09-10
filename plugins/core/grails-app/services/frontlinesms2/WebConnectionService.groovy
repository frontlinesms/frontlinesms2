package frontlinesms2

import frontlinesms2.*
import org.apache.camel.*

class WebConnectionService{

	def preProcess(Exchange x) throws Exception {
		println "x: ${x}"
		println "x.in: ${x.in}"
		println "x.in.headers: ${x.in.headers}"
		x.out.headers = x.in.headers
		def inMessage = Fmessage.get(x.in.headers.'frontlinesms.fmessageId')
		def webConn = WebConnection.get(x.in.headers.'frontlinesms.webConnectionId')
		def encodedParameters = webConn.requestParameters.collect {
			urlEncode(it.name) + '=' + urlEncode(it.getProcessedValue(inMessage))
		}.join('&')
		println "PARAMS:::$encodedParameters"

		x.out.headers[Exchange.HTTP_METHOD] = webConn.httpMethod
		switch(webConn.httpMethod) {
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
