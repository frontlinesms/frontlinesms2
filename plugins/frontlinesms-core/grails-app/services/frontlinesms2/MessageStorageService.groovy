package frontlinesms2

import org.apache.camel.Exchange
import org.apache.camel.Processor

class MessageStorageService implements Processor {
	public void process(Exchange x) {
		def message = x.in.body
		assert message instanceof TextMessage
		message = message.id ? TextMessage.findById(message.id) : message
		message.connectionId = x.in.headers[Fconnection.HEADER_FCONNECTION_ID] as Long
		message.save(flush:true)
	}
}

