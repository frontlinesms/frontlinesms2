package frontlinesms2.domain

import frontlinesms2.*
class ContactISpec extends grails.plugin.spock.IntegrationSpec {
	def "should return the correct count of messages sent by a contact"() {
		when:
			def contact =new Contact(name:'Bob', primaryMobile:"1234567").save(failOnError:true, flush:true)
			new Fmessage(src: '1234567', read:true, date: new Date(), inbound:true).save(failOnError: true, flush:true)
		then:
			contact.outboundMessagesCount == 0
			contact.inboundMessagesCount == 1
		when:
			new Fmessage(src: '1234567', read:true, date: new Date(), inbound:true).save(failOnError: true, flush:true)
		then:
			contact.outboundMessagesCount == 0
			contact.inboundMessagesCount == 2
		when:
			def dispatch = new Dispatch(dst:'1234567', status: DispatchStatus.SENT, dateSent: new Date())
			new Fmessage(hasSent: true, isDeleted:false, text:'Another sent message', date: new Date()).addToDispatches(dispatch).addToDispatches(dispatch).save(flush:true, failOnError:true)
		then:
			contact.outboundMessagesCount == 1
			contact.inboundMessagesCount == 2
	}
}