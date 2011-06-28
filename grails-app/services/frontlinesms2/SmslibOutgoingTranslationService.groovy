package frontlinesms2

import org.apache.camel.Processor
import org.apache.camel.Exchange
import org.smslib.CMessage
import net.frontlinesms.camel.smslib.OutgoingSmslibCamelMessage

class SmslibOutgoingTranslationService implements Processor {
	static transactional = false

	void process(Exchange ex) {
		def messageIn = ex.in.body
		println "message is: $messageIn"
//		def msg = new CMessage(2, '0702231301', '0703535226', messageIn.text)
//		OutgoingSmslibCamelMessage msg = null


//		ex.out.body = c
		ex.out = new OutgoingSmslibCamelMessage() {}
//		println "message text is: ${ex.out}"
	}
}

