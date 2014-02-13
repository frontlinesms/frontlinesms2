package frontlinesms2

import org.apache.camel.Processor
import org.apache.camel.Exchange

class SmppTranslationService implements Processor {
	void process(Exchange exchange) {
		def logWithPrefix = { t->
			log.info "SmppTranslationService.process ${t}"
		}
		logWithPrefix "ENTRY"
		logWithPrefix ("exchange ${exchange}")
		def i = exchange.in
		logWithPrefix ("in: ${i}")
		logWithPrefix ("in.headers: ${i.headers}")
		logWithPrefix ("in.body: ${i.body}")
		logWithPrefix ("in.getHeaders: ${i.getHeaders()}")
		
		//TODO allow messages is source is set
		if(i.headers['CamelSmppSourceAddr']) {
			TextMessage message = new TextMessage(inbound:true)
			def messageBody = i.body
			def messageSource = i.headers['CamelSmppSourceAddr']
			logWithPrefix "###### message-timestamp ${i.headers['CamelSmppDoneDate']}"
			def messageDate = i.headers['CamelSmppDoneDate']
			
			message.src = messageSource
			message.text = messageBody
			//TODO pick the value from Exchange
			message.date = Date.parse("yyMMddHHmm",messageDate)
			
			logWithPrefix "message source is ${message.src}"
			logWithPrefix "message body is ${message.text}"
			logWithPrefix "message sent on ${message.date}"


			exchange.in.body = message
			logWithPrefix "IN::BODY ${exchange.in.body}"
		} else {
			exchange.setProperty(Exchange.ROUTE_STOP, Boolean.TRUE);
		}
		
	}
}
