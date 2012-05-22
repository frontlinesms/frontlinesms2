package frontlinesms2.services

import frontlinesms2.*
import spock.lang.*
import grails.test.mixin.*

@TestFor(ExpressionProcessorService)
class ExpressionProcessorServiceSpec extends Specification {	
	@Unroll
	def 'process should return message content with no expressions in it'() {
		setup:
			def contact =new Contact(name:'Gedi', mobile:"10983").save(failOnError:true, flush:true)
			def m = new Fmessage(src: '10983', inbound: false, archived: false, hasSent: false, date: new Date())
			m.text = messageText
			Dispatch dis = new Dispatch(dst: '12345', message: m, status: DispatchStatus.FAILED, dateSent: now)
			def processedMessageText = service.process(dis)
		expect:
			processedMessageText == expectedMessageText
		where:
			messageText                                             | expectedMessageText
			'message text sample'                                   | 'message text sample'
			'please call us on ${contact_number}'                   | 'please call us on 10983'
			'sender name ${contact_name}, number ${contact_number}' | 'sender name Gedi, number 10983'
	}
}