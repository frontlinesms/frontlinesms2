package frontlinesms2

import frontlinesms2.*
import org.apache.camel.*

class WebConnectionService implements Processor {

	def preProcess(Exchange x) throws Exception {
		def inHeader = x.in.header
		def inMessage = Fmessage.get(inHeader.'frontlinesms.fmessageId')
		def webConn = WebConnection.get(inHeader.'frontlinesms.webConnectionId')
		def body
		def url = webConn.url
		def params
		webConn.requestParameters.each {
			params+=urlEncode(it.name + "=" + it.value + "&")
		}
		if (params.length > 0)
			params = params[0..-2] // drop trailing ampersand
		if(webConn.httpMethod == WebConnection.HttpMethod.GET) {
			url += "?" + params
		}
		else {
			body = params
		}
		body = urlEncode(body)
		url = urlEncode(url)
		// TODO:
		/* - Find the message and the webservice activity instance
		 * - Put all parameters into URL/BODY (depending on GET/POST)
		 * - Perform substitutions on URL and BODY (thus catering for clever people who put params in URL field)
		 * - URLEncode body & url
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
