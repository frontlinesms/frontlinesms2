package frontlinesms2.domain

import spock.lang.*

import frontlinesms2.*

class ContactISpec extends grails.plugin.spock.IntegrationSpec {
	def "should return the correct count of messages sent by a contact"() {
		when:
			def contact =new Contact(name:'Bob', mobile:"1234567").save(failOnError:true, flush:true)
			TextMessage.build(src: '1234567', read:true, date: new Date(), inbound:true).save(failOnError: true, flush:true)
		then:
			contact.outboundMessagesCount == 0
			contact.inboundMessagesCount == 1
		when:
			TextMessage.build(src: '1234567', read:true, date: new Date(), inbound:true).save(failOnError: true, flush:true)
		then:
			contact.outboundMessagesCount == 0
			contact.inboundMessagesCount == 2
		when:
			def dispatch = new Dispatch(dst:'1234567', status: DispatchStatus.SENT, dateSent: new Date())
			new TextMessage(hasSent: true, isDeleted:false, text:'Another sent message', date: new Date()).addToDispatches(dispatch).addToDispatches(dispatch).save(flush:true, failOnError:true)
		then:
			contact.outboundMessagesCount == 1
			contact.inboundMessagesCount == 2
	}
	
	def "should return the right count of all messages sent to a contact's primary address"() {
		setup:
			String johnsmobile = "9876543210"
			Contact contact = new Contact(name: "John", mobile: johnsmobile).save(flush:true)
			def d1 = new Dispatch(dst: johnsmobile, status: DispatchStatus.FAILED)
			def d2 = new Dispatch(dst: johnsmobile, status: DispatchStatus.FAILED)
			new TextMessage(text:'', isDeleted: false, inbound: false, date: new Date(), hasFailed:true).addToDispatches(d1).save(flush:true, failOnError:true)
			new TextMessage(text:'', isDeleted: true, inbound: false, date: new Date(), hasFailed:true).addToDispatches(d2).save(flush:true, failOnError:true)
			new TextMessage(text:'', isDeleted: false, inbound: false, date: new Date(), hasFailed:true).addToDispatches(d1).save(flush:true, failOnError:true)
		when:
			def count = contact.outboundMessagesCount
		then:
			count == 2
	}

	def "should return the count of all messages received from a given contact except deleted messages"() {
		setup:
			String georgesAddress = "1234567890"
			String georgeAddress2 = "0987654151"
			Contact contact = new Contact(name:"George", mobile:georgesAddress).save(flush:true)
			[TextMessage.build(src: georgesAddress, isDeleted: false, inbound: true, date: new Date()),
					TextMessage.build(src: georgesAddress, isDeleted: true, inbound: true, date: new Date()),
					TextMessage.build(src: georgesAddress, isDeleted: false, inbound: true, date: new Date()),
					TextMessage.build(src: georgeAddress2, isDeleted: true, inbound: true, date: new Date())]*.save(flush:true, failOnError:true)
	    when:
	        def count = contact.inboundMessagesCount
	    then:                                     
	        count == 2
  	}

	@Unroll
	def 'customfield matching'() { 
		setup:
			[
				Adam:  [city:'Paris'],
				Bernie:[city:'Paris', like:'ca' ],
				Chaz:  [city:'Paris', like:'cake', dob:'12/06/79'],
				Dave:  [              like:'ake'],
			].collect { name, fields ->
				def c = Contact.build(name:name)
				fields.each { n, v -> c.addToCustomFields(name:n, value:v) }
				c.save(failOnError:true)
			}
		when:
			def matches = Contact.findByCustomFields(fields)
		then:
			matches*.name.sort() == contacts.sort()
		where:
			fields                                         | contacts
			[:]                                            | ['Adam', 'Bernie', 'Chaz', 'Dave']
			[cloak:'none']                                 | []
			[city:'Paris']                                 | ['Adam', 'Bernie', 'Chaz']
			[city:'paris']                                 | ['Adam', 'Bernie', 'Chaz']
			[like:'ca']                                    | ['Bernie', 'Chaz']
			[like:'ake']                                   | ['Chaz', 'Dave']
			[city:'Paris', like:'ca']                      | ['Bernie', 'Chaz']
			[city:'Paris', like:'ca', dob:'06']            | ['Chaz']
			[city:'Paris', like:'ca', dob:'06', car:'yes'] | []
	}
}
