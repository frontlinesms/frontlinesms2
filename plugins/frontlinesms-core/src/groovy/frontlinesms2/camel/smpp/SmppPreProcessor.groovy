package frontlinesms2.camel.smpp

import frontlinesms2.*
import org.apache.camel.*

class SmppPreProcessor implements Processor {
	public void process(Exchange x) throws Exception {
		def log = { println "SmppPreProcessor.process() : $it" }
		log 'ENTRY'
		log "Exchange:IN.HEADERS  ${x.in.headers}"
		
		// URL-encode body
		def d = x.in.body
		x.in.headers['frontlinesms.dispatch.id'] = d.id
		x.in.body = urlEncode(d.text)
		
		def destination = d.dst
		if(destination && destination.charAt(0)=='+') destination = destination.substring(1)
		set x, 'dst', destination
		//Setting the destination of the message
		x.in.setHeader("CamelSmppDestAddr", d.dst)
		
		log 'EXIT'
	}
	
	private def set(Exchange x, String header, String value) {
		println "PreProcessor.set() : header=$header; value=$value"
		x.in.headers["smpp.$header"] = urlEncode(value)
	}
	
	private String urlEncode(String s) throws UnsupportedEncodingException {
		println "PreProcessor.urlEncode : s=$s -> ${URLEncoder.encode(s, "UTF-8")}"
		return URLEncoder.encode(s, "UTF-8");
	}
}