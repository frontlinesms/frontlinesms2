package frontlinesms2

import org.apache.camel.Exchange
import org.apache.camel.Processor

class DispatchStorageService implements Processor {
	public void process(Exchange ex) {
		def d = ex.in.body
		assert d instanceof Dispatch
		d.save()
	}
}
