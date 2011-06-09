package frontlinesms2

import org.apache.camel.Processor
import org.apache.camel.Exchange

class SmslibOutgoingTranslationService implements Processor {
	static transactional = false

	void process(Exchange ex) {
		def i = ex.in
		def message = i.body
		ex.out.body = message

//		def endpoint = i.getHeader('endpoint')
	}
}

