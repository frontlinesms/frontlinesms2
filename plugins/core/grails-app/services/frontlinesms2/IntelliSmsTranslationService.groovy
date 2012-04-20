package frontlinesms2

import org.apache.camel.Processor
import org.apache.camel.Exchange

class IntelliSmsTranslationService implements Processor {
	static final String INTELLISMS_MESSAGING_ADDR = '@messaging.intellisoftware.co.uk'
	static final String INTERNATIONAL_SYMBOL = '+'
	static transactional = false

	void process(Exchange exchange) {
		println("exchange ${exchange}")
		def i = exchange.in
		println("in: ${i}")
		if(isValidMessageSource(i.getHeader('From'))) {
			Fmessage message = new Fmessage(inbound:true)
			def emailBody = i.body
			def emailSubject = i.getHeader('Subject')
			def emailDate = i.getHeader('Date')
			message.src = INTERNATIONAL_SYMBOL + emailSubject.split(" ")[2]
			println("src: ${message.src}")
			println "emailBody: $emailBody"
			println "emailSubject: $emailSubject"
			println "emailDate: $emailDate"
			message.text = emailSubject
			message.date = Date.parse("EEE, dd MMM yyyy hh:mm:ss Z",emailDate)
			
			println "message sent on ${message.date}"
			if(emailBody != null) {
				message.text = emailBody
			}
			exchange.out.body = message
		}
		
	}

	private isValidMessageSource(from) {
		println "from: $from"
		from.contains(INTELLISMS_MESSAGING_ADDR)
	}
}
