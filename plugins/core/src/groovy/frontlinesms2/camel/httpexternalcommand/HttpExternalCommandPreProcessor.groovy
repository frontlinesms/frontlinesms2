package frontlinesms2.camel.httpwebconnection

import frontlinesms2.*
import org.apache.camel.*

class HttpWebConnectionPreProcessor implements Processor {
	public void process(Exchange x) throws Exception {
		def log = { println "HttpWebConnectionPreProcessor.process() : $it" }
		log 'ENTRY'
		log 'EXIT'
	}
	
	private def set(Exchange x, String header, String value) {
		println "PreProcessor.set() : header=$header; value=$value"
		x.out.headers["clickatell.$header"] = urlEncode(value)
	}
	
	private String urlEncode(String s) throws UnsupportedEncodingException {
		println "PreProcessor.urlEncode : s=$s -> ${URLEncoder.encode(s, "UTF-8")}"
		return URLEncoder.encode(s, "UTF-8");
	}
}

