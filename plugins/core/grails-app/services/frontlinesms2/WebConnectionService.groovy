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
		def body = ""
		def url = webConn.url
		def encodedParameters = ""
		webConn.requestParameters.each {
			println "I AM A REQUEST PARAMETER:::"+it
			encodedParameters+=urlEncode(it.name + "=" + it.getProcessedValue(inMessage) + "&")
		}
		if (encodedParameters.length() > 0)
			encodedParameters = encodedParameters[0..-2] // drop trailing ampersand
		if(webConn.httpMethod == WebConnection.HttpMethod.GET) {
			url += "?" + encodedParameters
		}
		else {
			println "PARAMS:::"+encodedParameters
			body = encodedParameters
		}
		x.out.headers = x.in.headers
		x.out.headers.url = url
		x.out.body = body
		println "x.out.headers = ${x.out.headers}"
		println "x.out.body = ${x.out.body}"
	}

	def postProcess(Exchange x) throws Exception {
		println "Web Connection Response::\n ${x.in.body}"
	}
	
	private String urlEncode(String s) throws UnsupportedEncodingException {
		println "PreProcessor.urlEncode : s=$s -> ${URLEncoder.encode(s, "UTF-8")}"
		return URLEncoder.encode(s, "UTF-8");
	}
}
