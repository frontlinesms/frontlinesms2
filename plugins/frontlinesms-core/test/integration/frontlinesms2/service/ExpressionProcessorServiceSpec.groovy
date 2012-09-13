package frontlinesms2.service

import frontlinesms2.*
import spock.lang.*
import grails.plugin.spock.*

class ExpressionProcessorServiceSpec extends grails.plugin.spock.IntegrationSpec {	
	def expressionProcessorService

	@Unroll
	def 'process should return message content with no expressions in it'() {
		setup:
			def contact =new Contact(name:'Gedi', mobile:"10983").save(failOnError:true, flush:true)
			def m = new Fmessage(src: '10983', inbound: false, archived: false, hasSent: false, date: new Date())
			m.text = messageText
			Dispatch dis = new Dispatch(dst: '10983', message: m, status: DispatchStatus.FAILED, dateSent: new Date())
			def processedMessageText = expressionProcessorService.process(dis)
		expect:
			processedMessageText == expectedMessageText
		where:
			messageText                                             | expectedMessageText
			'message text sample'                                   | 'message text sample'
			'please call us on ${contact_number}'                   | 'please call us on 10983'
			'sender name ${contact_name}, number ${contact_number}' | 'sender name Gedi, number 10983'
	}
}