package frontlinesms2

import org.apache.camel.Exchange
import org.smslib.CIncomingMessage
import org.smslib.CStatusReportMessage
import org.smslib.COutgoingMessage

class SmslibTranslationService {
	void toFmessage(Exchange exchange) {
		def i = exchange.in
		CIncomingMessage bod = exchange.in.body
		
		// Ignore CStatusReportMessages
		if(bod instanceof CStatusReportMessage) {
			return
		} else {
			Fmessage message = new Fmessage(inbound:true)
			message.src = bod.originator
			message.dst = bod.recipient
			message.text = bod.text
			message.dateCreated = new Date(bod.date)
			assert exchange.out != null
			exchange.out.body = message	
		}
	}
	
	void toCmessage(Exchange exchange) {
		def f = exchange.in.body
		def c = new COutgoingMessage(f.dst, f.text)
		c.originator = f.src
		c.date = f.dateCreated.time
		
		exchange.out.body = c
	}
}
