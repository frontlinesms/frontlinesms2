package frontlinesms2

import org.apache.camel.Processor
import org.apache.camel.Exchange
import org.smslib.CMessage
import net.frontlinesms.camel.smslib.OutgoingSmslibCamelMessage
import org.apache.camel.impl.DefaultMessage
import org.smslib.COutgoingMessage

class SmslibOutgoingTranslationService implements Processor {
	static transactional = false

	void process(Exchange ex) {
		def messageIn = ex.in.body
		println "exchange in: ${ex.in}"
		println "exchange in body: ${ex.in.body}"
		println "message is: $messageIn"
//		def msg = new CMessage(2, '0702231301', '0703535226', messageIn.text)
//		OutgoingSmslibCamelMessage msg = null
		def cmsg = new COutgoingMessage("123","wewewe")
		println "outgoing cmessage: $cmsg"
		println "outgoing message text: ${cmsg.text}"
//		ex.out.body = c
		ex.out.body = cmsg

		println "outgoing exchange is: ${ex.out}"
		println "outgoing exchange body is: ${ex.out.body}"
	}
}

