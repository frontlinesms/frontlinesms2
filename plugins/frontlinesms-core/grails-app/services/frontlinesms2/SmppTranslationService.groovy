package frontlinesms2

import org.apache.camel.Processor
import org.apache.camel.Exchange

class SmppTranslationService implements Processor {
	void process(Exchange exchange) {
		def log = { t->
			println "SmppTranslationService.process ${t}"
		}
		log "ENTRY"
		log ("exchange ${exchange}")
		def i = exchange.in
		log ("in: ${i}")
		log ("in.headers: ${i.headers}")
		log ("in.body: ${i.body}")
		log ("in.getHeaders: ${i.getHeaders()}")
		
		//TODO allow messages is source is set
		//if(i.headers['CamelSmppSourceAddr']) {
		if(true) {
			Fmessage message = new Fmessage(inbound:true)
			def messageBody = i.body
			def messageSource = i.headers['CamelSmppSourceAddr']
			log "###### message-timestamp ${i.headers['CamelSmppDoneDate']}"
			def messageDate = i.headers['CamelSmppDoneDate']
			
			message.src = messageSource
			message.text = messageBody
			//TODO pick the value from Exchange
			message.date = Date.parse("YYMMDDhhmm",messageDate)
			
			log "message source is ${message.src}"
			log "message body is ${message.text}"
			log "message sent on ${message.date}"


			exchange.in.body = message
			log "IN::BODY ${exchange.in.body}"
		} else {
			exchange.setProperty(Exchange.ROUTE_STOP, Boolean.TRUE);
		}
		
	}
}
