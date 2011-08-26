package frontlinesms2.services

import frontlinesms2.Fmessage
import grails.plugin.spock.UnitSpec
import org.apache.camel.Exchange
import spock.lang.Shared
import frontlinesms2.SubscriptionService
import frontlinesms2.Contact
import frontlinesms2.Group
import frontlinesms2.GroupMembership

class SubscriptionServiceSpec extends UnitSpec {
	@Shared
	SubscriptionService service

	String WHITE_SPACE = " "
		
	def setup() {
		service = new SubscriptionService()
		def contact = new Contact(primaryMobile: "9533326555")
		mockDomain(Contact, [contact])
		mockDomain(GroupMembership)
	}

	def "should process subscription messages when subscribed"() {
 		setup:
			Exchange mockExchange = Mock()
			service.metaClass.getMessage = {Exchange exchange -> new Fmessage(src: "9533326555", text: "ADD") }
			mockDomain(Group, [new Group(name:"MAC", subscriptionKey:"ADD" , 
											unsubscriptionKey:"REMOVE")])
		when:
			service.process(mockExchange)
		then:
			Group.findByName("MAC").members.contains(Contact.list()[0])
	}

	def "should process subscription messages with special characters"() {
 		setup:
			Exchange mockExchange = Mock()
			service.metaClass.getMessage = {Exchange exchange -> new Fmessage(src: "9533326555", text: "ADD@") }
			mockDomain(Group, [new Group(name:"MAC", subscriptionKey:"ADD" ,
											unsubscriptionKey:"REMOVE")])
		when:
			service.process(mockExchange)
		then:
			Group.findByName("MAC").members.contains(Contact.list()[0])
	}

	def "should process subscription messages with invalid subscription texts as keywords"() {
 		setup:
			Exchange mockExchange = Mock()
			service.metaClass.getMessage = {Exchange exchange -> new Fmessage(src: "9533326555", text: "AD #D@") }
			mockDomain(Group, [new Group(name:"MAC", subscriptionKey:"ADD" ,
											unsubscriptionKey:"REMOVE")])
		when:
			service.process(mockExchange)
		then:
			!Group.findByName("MAC").members
	}

	def "should process subscription messages without case sensitivity"() {
 		setup:
			Exchange mockExchange = Mock()
			service.metaClass.getMessage = {Exchange exchange -> new Fmessage(src: "9533326555", text: "add") }
			mockDomain(Group, [new Group(name:"WINDOWS", subscriptionKey:"ADDME" , unsubscriptionKey:"REMOVEME"),
									new Group(name:"MAC", subscriptionKey:"ADD" ,unsubscriptionKey:"REMOVE")])
		when:
			service.process(mockExchange)
		then:
			Group.findByName("MAC").members
			!Group.findByName("WINDOWS").members
	}

	def "should process subscription messages with white space"() {
 		setup:
			def contact = Contact.list()[0]
			Exchange mockExchange = Mock()
			service.metaClass.getMessage = {Exchange exchange -> new Fmessage(src: "9533326555", text: "${WHITE_SPACE}ADD${WHITE_SPACE}" ) }
			mockDomain(Group, [new Group(name:"MAC", subscriptionKey:"ADD" ,
											unsubscriptionKey:"REMOVE")])
		when:
			service.process(mockExchange)
		then:
			Group.findByName("MAC").members.contains(contact)
	}
	
	def "should process subscription messages conflating accented characters"() {
		setup:
			def contact = Contact.list()[0]
			Exchange mockExchange = Mock()
			service.metaClass.getMessage = {Exchange exchange -> new Fmessage(src: "9533326555", text: " ÂDD ") }
			mockDomain(Group, [new Group(name:"MAC", subscriptionKey:"ADD" ,
										unsubscriptionKey:"REMOVE")])
			mockDomain GroupMembership, [new GroupMembership(group: Group.findByName('MAC'), contact: contact)]
		when:
			service.process(mockExchange)
		then:
			Group.findByName("MAC").members.contains(contact)
	}
	
	def "should process subscription messages when unsubscribed"() {
		setup:
			def contact = Contact.list()[0]
			Exchange mockExchange = Mock()
			service.metaClass.getMessage = {Exchange exchange -> new Fmessage(src: "9533326555", text: "REMOVE") }
			mockDomain(Group, [new Group(name:"MAC", subscriptionKey:"ADD" ,
										unsubscriptionKey:"REMOVE")])
			mockDomain GroupMembership, [new GroupMembership(group: Group.findByName('MAC'), contact: contact)]
		when:
			service.process(mockExchange)
		then:
			!Group.findByName("MAC").members.contains(contact)
	}
}
