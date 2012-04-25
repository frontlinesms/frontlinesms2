package frontlinesms2.camel.intellisms

import frontlinesms2.*
import frontlinesms2.camel.exception.*
import org.apache.camel.*

class IntelliSmsPostProcessor implements Processor {
	public void process(Exchange exchange) throws Exception {
		def log = { println "IntelliSmsPostProcessor.process() : $it" }
		log 'ENTRY'
		log "in.body:" + exchange.in.body
		byte[] bytes = exchange.in.getBody(byte[].class);
		log "in.body as byte[]:" + bytes
		String text = new String(bytes, "UTF-8").trim();
		log "in.body as byte[] as String:" + text
		log "in.body got as a string" + exchange.in.getBody(String.class)
		if(text ==~ "ID:.*") log "message sent successfully"
		else {
			def m = (text =~ /ERR:\s*(.*)/)
			if(m.matches()) throw new FatalConnectionException("IntelliSMS gateway error: ${m[0][1]} (${m[0][2]})")
			else throw new RuntimeException("Unexpected response from IntelliSMS gateway: $text")
		}
		log 'EXIT'
	}
}
