package frontlinesms2

import org.apache.camel.Processor
import org.apache.camel.Exchange
import org.smslib.COutgoingMessage

class SmslibOutgoingTranslationService implements Processor {
	static transactional = false

	void process(Exchange ex) {
		def msgIn = ex.in.body
		def msgTxt = msgIn.text
		def msgDst = msgIn.dst
		def cmsg = new COutgoingMessage("$msgDst","$msgTxt")
		ex.out.body = cmsg
	}
}

