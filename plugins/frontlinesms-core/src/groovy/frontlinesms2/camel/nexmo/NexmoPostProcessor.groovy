package frontlinesms2.camel.nexmo

import frontlinesms2.*
import org.apache.camel.*
import frontlinesms2.camel.exception.*
import groovy.json.JsonSlurper

class NexmoPostProcessor implements Processor {
	public void process(Exchange exchange) throws Exception {
		def log = { println "NexmoPostProcessor.process() : $it" }
		log 'ENTRY'
		log 'exchange:'+exchange
		log "in.body:" + exchange.in.body
		byte[] bytes = exchange.in.getBody(byte[].class);
		log "in.body as byte[]:" + bytes
		String text = new String(bytes, "UTF-8").trim();
		log "in.body as byte[] as String:" + text
		def stringValue = """${text}""".replaceAll("-","_")
		def slurper = new JsonSlurper()
		def jsonValue = slurper.parseText(stringValue)
		log "in.body as JSON:"+jsonValue
		log "message count:"+jsonValue.message_count
		log "message status:"+jsonValue.messages[0].status
		if(jsonValue.messages[0].status == '0'){
			log "message sent successfully" 
		}
		else if(jsonValue.messages[0].status == '2' || jsonValue.messages[0].status == '3' || jsonValue.messages[0].status == '4') {
			throw new AuthenticationException("Nexmo error: ${jsonValue.messages[0].error_text}")
		}
		else if(jsonValue.messages[0].status == '9') {
			throw new InsufficientCreditException("Nexmo error: ${jsonValue.messages[0].error_text}")
		}
		else{
			throw new RuntimeException("Nexmo error: ${jsonValue.messages[0].error_text}")
		}	
		log "in.body got as a string" + exchange.in.getBody(String.class)
		log 'EXIT'
	}
}