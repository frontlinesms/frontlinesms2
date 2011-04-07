package frontlinesms2

import org.apache.camel.Exchange

class MessageStorageService implements org.apache.camel.Processor {
    static transactional = true

	public void process(Exchange ex) {
		println("MessageStorageService.process()")
		def message = ex.in.body
		println("Saving message: ${message}")
		assert message instanceof Fmessage
		message.save(failOnError:true)
	}
}
