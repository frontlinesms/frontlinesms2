package frontlinesms2

import org.apache.camel.Processor
import org.apache.camel.Exchange

class IntelliSmsTranslationService implements Processor {
	static final String INTELLISMS_MESSAGING_ADDR = '@messaging.intellisoftware.co.uk'
	static final String INTERNATIONAL_SYMBOL = '+'
	static transactional = false //TODO please explain why this is not transactional

	void process(Exchange exchange) {
		log.info("exchange ${exchange}")
		def i = exchange.in
		log.info("in: ${i}")
		if(isValidMessageSource(i.getHeader('From'))) {
			TextMessage message = new TextMessage(inbound:true)
			def emailBody = i.body
			def emailSubject = i.getHeader('Subject')
			def emailDate = i.getHeader('Date')
			message.src = INTERNATIONAL_SYMBOL + emailSubject.split(" ")[2]
			log.info("src: ${message.src}")
			log.info "emailBody: $emailBody"
			log.info "emailSubject: $emailSubject"
			log.info "emailDate: $emailDate"
			message.text = emailSubject
			message.date = Date.parse("EEE, dd MMM yyyy hh:mm:ss Z",emailDate)
			
			log.info "message sent on ${message.date}"
			if(emailBody != null) {
				message.text = emailBody
			}
			exchange.out.body = message
		} else {
			exchange.setProperty(Exchange.ROUTE_STOP, Boolean.TRUE);
		}
		
	}

	private isValidMessageSource(from) {
		log.info "from: $from"
		from.contains(INTELLISMS_MESSAGING_ADDR)
	}
}
