package frontlinesms2.service

import frontlinesms2.*
import spock.lang.*
import grails.plugin.spock.*

class ExpressionProcessorServiceISpec extends grails.plugin.spock.IntegrationSpec {
	def expressionProcessorService

	@Unroll
	def 'process should return message content with no expressions in it, unless some expressions are not recognised'() {
		setup:
			def contact = new Contact(name:'Gedi', mobile:'10983').save(failOnError:true, flush:true)
			def m = new Fmessage(src:'10983', inbound:false)
			m.text = messageText
			def d = new Dispatch(dst:'10983', status:DispatchStatus.FAILED)
			m.addToDispatches(d)
			m.save(failOnError:true, flush:true)
			d.refresh()
			def processedMessageText = expressionProcessorService.process(d)
		expect:
			processedMessageText == expectedMessageText
		where:
			messageText                                                                                                                | expectedMessageText
			'message text sample'                                                                                                      | 'message text sample'
			'please call us on ${recipient_number}'                                                                                    | 'please call us on 10983'
			'sender name ${recipient_name}, number ${recipient_number}'                                                                | 'sender name Gedi, number 10983'
			'the expression ${message_text_with_keyword} cannot work here'                                                             | 'the expression ${message_text_with_keyword} cannot work here'
			'the expression ${that_i_just_made_up} is not recognised'                                                                  | 'the expression ${that_i_just_made_up} is not recognised'
			'${recipient_number} - ${message_text_with_keyword} - ${that_i_just_made_up} - ${recipient_number} - ${recipient_name} .'  | '10983 - ${message_text_with_keyword} - ${that_i_just_made_up} - 10983 - Gedi .'
	}

	@Unroll
	def 'process should work for autoforward expressions which require further context from the message and its owner'() {
		setup:
			def source = new Contact(name:'Source', mobile:'112233').save(failOnError:true, flush:true)
			def destination = new Contact(name:'Destination', mobile:'445566').save(failOnError:true, flush:true)
			def autoforward = new Autoforward(name:'I forward stuff', sentMessageText:'This is not too relevant as we are manually setting message text')
				.addToKeywords(value:'INCOMING')
				.addToContacts(destination)
				.save(failOnError:true, flush:true)
			def inbound = new Fmessage(src:'112233', inbound:true, text:'Incoming Message Text').save(failOnError:true, flush:true)
			autoforward.addToMessages(inbound)

			def outbound = new Fmessage(src:'0', inbound:false, ownerDetail:inbound.id, text:outboundMessageText)
			Dispatch dis = new Dispatch(dst:'445566', status:DispatchStatus.PENDING)
			outbound.addToDispatches(dis)
			outbound.addToDetails(new MessageDetail(value: inbound.id, ownerType: MessageDetail.OwnerType.ACTIVITY, ownerId: autoforward.id))
			outbound.text = outboundMessageText
			autoforward.addToMessages(outbound).save(failOnError:true, flush:true)
			inbound.refresh()
			def processedMessageText = expressionProcessorService.process(dis)
		expect:
			processedMessageText == expectedDispatchText
		where:
			outboundMessageText                                            | expectedDispatchText
			'message text sample'                                          | 'message text sample'
			'message text sample ${keyword}'                               | 'message text sample INCOMING'
			'this message is from ${sender_name} to ${recipient_name}'     | 'this message is from Source to Destination'
			'this message is from ${sender_number} to ${recipient_number}' | 'this message is from 112233 to 445566'
			'the original message says: ${message_text}'                   | 'the original message says: Message Text'
			'the original message says: ${message_text_with_keyword}'      | 'the original message says: Incoming Message Text'
	}
}

