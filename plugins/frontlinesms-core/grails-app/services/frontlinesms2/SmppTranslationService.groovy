package frontlinesms2

import org.apache.camel.Processor
import org.apache.camel.Exchange

class SmppTranslationService implements Processor {
	static final String INTERNATIONAL_SYMBOL = '+'
	static transactional = false

	void process(Exchange exchange) {
		log { t->
			println "SmppTranslationService.process ${t}"
		}
		println("exchange ${exchange}")
		def i = exchange.in
		println("in: ${i}")
		if(i.getHeader('CamelSmppSourceAddr')) {
			Fmessage message = new Fmessage(inbound:true)
			def messageBody = i.body
			def messageSource = i.getHeader('CamelSmppSourceAddr')
			def messageDate = i.getHeader('CamelSmppDoneDate')
			
			message.src = messageSource
			message.text = messageBody
			message.date = Date.parse("YYMMDDhhmm",messageDate)
			
			log "message source is ${message.src}"
			log "message body is ${message.text}"
			log "message sent on ${message.date}"


			exchange.out.body = message
		} else {
			exchange.setProperty(Exchange.ROUTE_STOP, Boolean.TRUE);
		}
		
	}
}
