package frontlinesms2

import org.apache.camel.Processor
import org.apache.camel.Exchange

class SmppTranslationService implements Processor {
	void process(Exchange exchange) {
		println "ENTRY"
		def log = { t->
			println "SmppTranslationService.process ${t}"
		}
		println("exchange ${exchange}")
		def i = exchange.in
		println("in: ${i}")
		println("in.headers: ${i.headers}")
		println("in.getHeaders: ${i.getHeaders()}")
		
		if(i.headers['CamelSmppSourceAddr']) {
			Fmessage message = new Fmessage(inbound:true)
			def messageBody = i.body
			def messageSource = i.headers['CamelSmppSourceAddr']
			def messageDate = i.headers['CamelSmppDoneDate']
			
			message.src = messageSource
			message.text = messageBody
			message.date = Date.parse("YYMMDDhhmm",messageDate)
			
			log "message source is ${message.src}"
			log "message body is ${message.text}"
			log "message sent on ${message.date}"


			exchange.out.body = message
			log "IN::BODY ${exchange.in.body}"
			log "OUT::BODY ${exchange.out.body}"
		} else {
			exchange.setProperty(Exchange.ROUTE_STOP, Boolean.TRUE);
		}
		
	}
}
