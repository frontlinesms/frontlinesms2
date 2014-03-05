package frontlinesms2.camel.nexmo

import frontlinesms2.*
import org.apache.camel.*
import frontlinesms2.camel.exception.*

class NexmoPreProcessor implements Processor {
	public void process(Exchange x) throws Exception {
		def log = { println "NexmoPreProcessor.process() : $it" }
		log 'ENTRY'
		
		// URL-encode body
		def d = x.in.body
		x.out.headers['frontlinesms.dispatch.id'] = d.id
		x.out.headers[Fconnection.HEADER_FCONNECTION_ID] = x.fconnectionId
		x.out.body = urlEncode(d.text)
		
		def destination = d.dst
		if(destination && destination.charAt(0)=='+') destination = destination.substring(1)
		set x, 'dst', destination
		
		// Add auth details to header
		log "Calculating connection ID..."
		def connectionId = x.fconnectionId
		log "connectionId=$connectionId"
		def connection = NexmoFconnection.get(connectionId)
		log "connection=$connection"
		['api_key', 'api_secret', 'fromNumber'].each { set x, it, connection."$it" }

		log 'EXIT'
	}
	
	private def set(Exchange x, String header, String value) {
		println "PreProcessor.set() : header=$header; value=$value"
		x.out.headers["nexmo.$header"] = urlEncode(value)
	}
	
	private String urlEncode(String s) throws UnsupportedEncodingException {
		println "PreProcessor.urlEncode : s=$s -> ${URLEncoder.encode(s, "UTF-8")}"
		return URLEncoder.encode(s, "UTF-8");
	}
}
