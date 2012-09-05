package frontlinesms2

import frontlinesms2.*
import org.apache.camel.*

class WebConnectionService{

	def preProcess(Exchange x) throws Exception {
		def inHeader = x.in.header
		def inMessage = Fmessage.get(inHeader.'frontlinesms.fmessageId')
		def webConn = WebConnection.get(inHeader.'frontlinesms.webConnectionId')
		def body
		def url = webConn.url
		def encodedParameters
		webConn.requestParameters.each {
			encodedParameters+=urlEncode(it.name + "=" + it.processedValue + "&")
		}
		if (encodedParameters.length > 0)
			encodedParameters = encodedParameters[0..-2] // drop trailing ampersand
		if(webConn.httpMethod == WebConnection.HttpMethod.GET) {
			url += "?" + encodedParameters
		}
		else {
			body = encodedParameters
		}
		// TODO:
		/*
		 * - Perform substitutions on URL and BODY (thus catering for clever people who put params in URL field)
		*/
	}

	def postProcess(Exchange x) throws Exception {
		// TODO:
		/* - Log the response
		*/
	}

	private def set(Exchange x, String header, String value) {
		println "PreProcessor.set() : header=$header; value=$value"
		x.out.headers["intellisms.$header"] = urlEncode(value)
	}
	
	private String urlEncode(String s) throws UnsupportedEncodingException {
		println "PreProcessor.urlEncode : s=$s -> ${URLEncoder.encode(s, "UTF-8")}"
		return URLEncoder.encode(s, "UTF-8");
	}
}
