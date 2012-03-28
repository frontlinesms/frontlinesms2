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
	
	def "should return the right count of all messages sent to a contact's primary address"() {
		setup:
			String johnsprimaryMobile = "9876543210"
			Contact contact = new Contact(name: "John", primaryMobile: johnsprimaryMobile).save(flush:true)
			def d1 = new Dispatch(dst: johnsprimaryMobile, status: DispatchStatus.FAILED)
			def d2 = new Dispatch(dst: johnsprimaryMobile, status: DispatchStatus.FAILED)
			def m1 = new Fmessage(isDeleted: false, inbound: false, date: new Date(), hasFailed:true).addToDispatches(d1).save(flush:true, failOnError:true)
			def m2 = new Fmessage(isDeleted: true, inbound: false, date: new Date(), hasFailed:true).addToDispatches(d2).save(flush:true, failOnError:true)
			def m3 = new Fmessage(isDeleted: false, inbound: false, date: new Date(), hasFailed:true).addToDispatches(d1).save(flush:true, failOnError:true)
		when:
			def count = contact.outboundMessagesCount
		then:
			count == 2
	}

	def "should return the count of all messages received from a given contact except deleted messages"() {
		setup:
			String georgesAddress = "1234567890"
			String georgeAddress2 = "0987654151"
			Contact contact = new Contact(name:"George", primaryMobile:georgesAddress).save(flush:true)
			[new Fmessage(src: georgesAddress, isDeleted: false, inbound: true, date: new Date()),
					new Fmessage(src: georgesAddress, isDeleted: true, inbound: true, date: new Date()),
					new Fmessage(src: georgesAddress, isDeleted: false, inbound: true, date: new Date()),
					new Fmessage(src: georgeAddress2, isDeleted: true, inbound: true, date: new Date())]*.save(flush:true, failOnError:true)
	    when:
	        def count = contact.inboundMessagesCount
	    then:                                     
	        count == 2
  	}
}
