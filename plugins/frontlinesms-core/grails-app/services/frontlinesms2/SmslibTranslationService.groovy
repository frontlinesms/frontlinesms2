package frontlinesms2

import org.apache.camel.Exchange
import org.smslib.CIncomingMessage
import org.smslib.CStatusReportMessage
import org.smslib.COutgoingMessage

class SmslibTranslationService {
	void toTextMessage(Exchange exchange) {
		CIncomingMessage bod = exchange.in.body
		
		// Ignore CStatusReportMessages
		if(bod instanceof CStatusReportMessage) {
			return
		} else {
			TextMessage message = new TextMessage(inbound:true)
			message.src = bod.originator
			message.text = bod.text
			message.date = new Date(bod.date)
			assert exchange.out != null
			exchange.out.body = message	
			exchange.out.headers."${Fconnection.HEADER_FCONNECTION_ID}" = exchange.in.headers."${Fconnection.HEADER_FCONNECTION_ID}"
		}
	}
	
	void toCmessage(Exchange exchange) {
		Dispatch d = exchange.in.body
		TextMessage m = d.message
		String address = d.dst
		String messageText = d.text?: ''
		def c = new COutgoingMessage(address, messageText)
		c.originator = m.src
		c.date = m.date.time
		exchange.in.body = c
		exchange.in.setHeader('frontlinesms.dispatch.id', d.id)
	}
}
