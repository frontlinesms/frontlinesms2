package frontlinesms2

import org.apache.camel.Exchange
import org.apache.camel.Processor

class MessageStorageService implements Processor {
	public void process(Exchange ex) {
		def message = ex.in.body
		assert message instanceof Fmessage
		message = message.id ? Fmessage.findById(message.id) : message
		message.save(flush:true)
	}
}
