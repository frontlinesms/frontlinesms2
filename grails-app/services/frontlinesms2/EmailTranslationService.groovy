package frontlinesms2

import org.apache.camel.Processor
import org.apache.camel.Exchange

class EmailTranslationService implements Processor {
	static transactional = false

	void process(Exchange exchange) {
		Fmessage message = new Fmessage()
		def i = exchange.in
		message.src = i.getHeader('From')
		message.dst = i.getHeader('To')
		assert exchange.out != null
		exchange.out.body = message
	}
}
