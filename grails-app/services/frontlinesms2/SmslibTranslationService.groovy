package frontlinesms2

import org.apache.camel.Processor
import org.apache.camel.Exchange
import org.smslib.CIncomingMessage

import frontlinesms2.enums.MessageStatus // FIXME remove "enums" package

class SmslibTranslationService implements Processor {
	void process(Exchange exchange) {
		println("exchange ${exchange}")
		def i = exchange.in
		println("smslib translation service - in: $i")
		println("smslib translation service - inclass: ${i.class}")
		CIncomingMessage bod = exchange.in.body
		Fmessage message = new Fmessage(status:MessageStatus.INBOUND)
		message.src = bod.originator
		message.dst = bod.recipient
		message.text = bod.text
		message.dateReceived = new Date(bod.date)
		
//		message.src = EMAIL_PROTOCOL_PREFIX + i.getHeader('From')
//		println("src: ${message.src}")
//		message.dst = EMAIL_PROTOCOL_PREFIX + i.getHeader('To')
//		println("dst: ${message.dst}")
//		def emailBody = i.body
//		def emailSubject = i.getHeader('Subject')
//		println("emailBody: ${emailBody}")
//		println("emailSubject: ${emailSubject}")
//		message.text = emailSubject
//		if(emailBody != null) {
//			message.text = message.text ? "${message.text}\n${underline(emailSubject)}\n\n${emailBody}" : emailBody
//		}
		assert exchange.out != null
		exchange.out.body = message
	}
}
