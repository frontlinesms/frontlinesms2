package frontlinesms2.camel.clickatell

import org.apache.camel.*

class ClickatellPreProcessor extends Processor {
	public void process(Exchange x) throws Exception {
		def log = { println "ClickatellPreProcessor.process() : $it" }
		log 'ENTRY'
		
		// URL-encode body
		def d = x.in.body
		x.out.setHeader('frontlinesms.dispatch.id', d.id)
		x.out.body = urlEncode(d.message.text)
		set x, 'dst', d.dst
		
		// Add auth details to header
		log "Calculating connection ID..."
		def connectionId = x.in.headers.fconnection
		log "connectionId=$connectionId"
		def connection = ClickatellFconnection.get(connectionId)
		log "connection=$connection"
		['apiId', 'username', 'password'].each { set x, it, connection."$it" }

		log 'EXIT'
	}
	
	private def set(Exchange x, String header, String value) {
		println "PreProcessor.set() : header=$header; value=$value"
		x.out.headers["clickatell.$header"] = urlEncode(value)
	}
	
	private String urlEncode(String s) throws UnsupportedEncodingException {
		println "PreProcessor.urlEncode : s=$s"
		return URLEncoder.encode(s, "UTF-8");
	}
}
