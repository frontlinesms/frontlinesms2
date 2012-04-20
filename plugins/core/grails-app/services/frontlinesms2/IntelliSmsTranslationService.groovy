package frontlinesms2

import org.apache.camel.Processor
import org.apache.camel.Exchange

class IntelliSmsTranslationService implements Processor {
	static final String INTELLISMS_MESSAGING_ADDR = '@messaging.intellisoftware.co.uk'

	static transactional = false

	void process(Exchange exchange) {
		println("exchange ${exchange}")
		def i = exchange.in
		println("in: ${i}")
		if(isValidMessageSource(i.getHeader('From'))) {
			Fmessage message = new Fmessage(inbound:true)
			message.src = i.getHeader('From')
			println("src: ${message.src}")
			def emailBody = i.body
			def emailSubject = i.getHeader('Subject')
			println("emailBody: ${emailBody}")
			println("emailSubject: ${emailSubject}")
			message.text = emailSubject
			
			if(emailBody != null) {
				message.text = emailBody
				assert exchange.out != null
				exchange.out.body = message
			}
		
		}
		
	}

	private isValidMessageSource(from) {
		println "from: $from"
		from.contains(INTELLISMS_MESSAGING_ADDR)
	}
}
