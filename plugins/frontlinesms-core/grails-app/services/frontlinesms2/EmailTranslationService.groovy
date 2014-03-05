package frontlinesms2

import org.apache.camel.Processor
import org.apache.camel.Exchange

class EmailTranslationService implements Processor {
	static final String EMAIL_PROTOCOL_PREFIX = 'email:'
	static final char UNDERLINE_CHAR = '='

	static transactional = false

	void process(Exchange exchange) {
		log.info("exchange ${exchange}")
		def i = exchange.in
		log.info("in: ${i}")
		TextMessage message = new TextMessage(inbound:true)
		message.src = EMAIL_PROTOCOL_PREFIX + i.getHeader('From')
		log.info("src: ${message.src}")
//		message.dst = EMAIL_PROTOCOL_PREFIX + i.getHeader('To')
//		log.info("dst: ${message.dst}")
		def emailBody = i.body
		def emailSubject = i.getHeader('Subject')
		log.info("emailBody: ${emailBody}")
		log.info("emailSubject: ${emailSubject}")
		message.text = emailSubject
		if(emailBody != null) {
			message.text = message.text ? "${message.text}\n${underline(emailSubject)}\n\n${emailBody}" : emailBody
		}
		assert exchange.out != null
		exchange.out.body = message
	}

	private String underline(String title) {
		def u = ''
		title.collect { u += UNDERLINE_CHAR }
		u
	}
}
