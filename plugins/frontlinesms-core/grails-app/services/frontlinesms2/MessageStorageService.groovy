package frontlinesms2

import org.apache.camel.Exchange
import org.apache.camel.Processor

class MessageStorageService implements Processor {
	public void process(Exchange x) {
		def message = x.in.body
		assert message instanceof Interaction
		message = message.id ? Interaction.findById(message.id) : message
		message.connectionId = x.in.headers[Fconnection.HEADER_FCONNECTION_ID]
		message.save(flush:true)
	}
}

