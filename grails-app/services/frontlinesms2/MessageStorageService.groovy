package frontlinesms2

import org.apache.camel.Exchange
import org.apache.camel.Processor

class MessageStorageService implements Processor {
    static transactional = true

	public void process(Exchange ex) {
		println("MessageStorageService.process()")
		def message = ex.in.body
		println("Saving message: ${message}")
		assert message instanceof Fmessage
		message.save(failOnError:true)
	}
}
