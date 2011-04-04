package frontlinesms2

import org.apache.camel.Exchange

class FmessageSaver implements org.apache.camel.Processor {
	public void process(Exchange ex) {
		def message = ex.in.body
		assert message instanceof Fmessage
		message.save(failOnError:true)
	}
}
