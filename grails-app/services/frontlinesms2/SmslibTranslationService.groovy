package frontlinesms2

import org.apache.camel.Exchange
import org.smslib.CIncomingMessage
import org.smslib.CStatusReportMessage
import org.smslib.COutgoingMessage

import frontlinesms2.enums.MessageStatus // FIXME remove "enums" package

class SmslibTranslationService {
	void toFmessage(Exchange exchange) {
		println("exchange ${exchange}")
		def i = exchange.in
		println("smslib translation service - in: $i")
		println("smslib translation service - inclass: ${i.class}")
		CIncomingMessage bod = exchange.in.body
		
		// Ignore CStatusReportMessages
		if(bod instanceof CStatusReportMessage) {
			return
		} else {
			Fmessage message = new Fmessage(status:MessageStatus.INBOUND)
			message.src = bod.originator
			message.dst = bod.recipient
			message.text = bod.text
			message.dateReceived = new Date(bod.date)
			assert exchange.out != null
			exchange.out.body = message	
		}
	}
	
	void toCmessage(Exchange exchange) {
		println "Should be translating to CMessage: $exchange"
		def f = exchange.in.body
		def c = new COutgoingMessage(f.dst, f.text)
		c.originator = f.src
		c.date = f.dateCreated.time
		
		println "Created CMessage $c"
		
		exchange.out.body = c
	}
}
