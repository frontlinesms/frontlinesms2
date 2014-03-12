package frontlinesms2.camel.clickatell

import frontlinesms2.*
import org.apache.camel.*

class ClickatellPreProcessor implements Processor {
	public void process(Exchange x) throws Exception {
		def log = { println "ClickatellPreProcessor.process() : $it" }
		log 'ENTRY'
		
		// URL-encode body, and set as hex-encoded unicode if necessary
		def d = x.in.body
		def text = d.text
		x.in.headers['frontlinesms.dispatch.id'] = d.id
		if (!text.areAllCharactersValidGSM()) {
			text = text.getBytes('utf-16').encodeHex().toString()
			set x, 'unicode', '1'
		}
		else {
			set x, 'unicode', '0'
		}
		def concat = TextMessageInfoUtils.getMessageInfos(d.text).partCount
		set x, 'concat', "${concat}"
		x.in.body = urlEncode(text)
		
		def destination = d.dst
		if(destination && destination.charAt(0)=='+') destination = destination.substring(1)
		set x, 'dst', destination
		
		// Add auth details to header
		log "Calculating connection ID..."
		def connectionId = x.fconnectionId
		log "connectionId=$connectionId"
		def connection = ClickatellFconnection.get(connectionId)
		log "connection=$connection"
		['apiId', 'username', 'password'].each { set x, it, connection."$it" }
		if(connection.sendToUsa)
			set x, 'fromNumber', connection.'fromNumber'

		log 'EXIT'
	}
	
	private def set(Exchange x, String header, String value) {
		println "PreProcessor.set() : header=$header; value=$value"
		x.in.headers["clickatell.$header"] = urlEncode(value)
	}
	
	private String urlEncode(String s) throws UnsupportedEncodingException {
		println "PreProcessor.urlEncode : s=$s -> ${URLEncoder.encode(s, "UTF-8")}"
		return URLEncoder.encode(s, "UTF-8");
	}

}

