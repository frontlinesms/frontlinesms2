package frontlinesms2.services

import frontlinesms2.Fmessage
import frontlinesms2.MessageStorageService
import grails.plugin.spock.UnitSpec
import org.apache.camel.CamelContext
import org.apache.camel.Exchange
import org.apache.camel.impl.DefaultExchange
import spock.lang.Shared
import frontlinesms2.SubscriptionService
import frontlinesms2.Contact
import frontlinesms2.Group

class SubscriptionServiceSpec extends UnitSpec {
	@Shared
	SubscriptionService service

	def "should process subscription messages when subscribed"() {
		setup:
			service = new SubscriptionService()
			Exchange mockExchange = Mock()
			service.metaClass.getMessage = {Exchange exchange -> new Fmessage(src: "9533326555", text: "ADD") }
			def contact = new Contact(primaryMobile: "9533326555")
			mockDomain(Contact, [contact])
			mockDomain(Group, [new Group(name:"MAC", subscriptionKey:"ADD" , 
											unsubscriptionKey:"REMOVE")])
		when:
			service.process(mockExchange)
		then:
			Group.findByName("MAC").members.contains(contact)
	}

	def "should process subscription messages when unsubscribed"() {
		setup:
			service = new SubscriptionService()
			Exchange mockExchange = Mock()
			service.metaClass.getMessage = {Exchange exchange -> new Fmessage(src: "9533326555", text: "REMOVE") }
			def contact = new Contact(primaryMobile: "9533326555")
			mockDomain(Contact, [contact])
			mockDomain(Group, [new Group(name:"MAC", subscriptionKey:"ADD" ,
											unsubscriptionKey:"REMOVE", members: [contact])])
		when:
			service.process(mockExchange)
		then:
			!Group.findByName("MAC").members.contains(contact)
	}

}