package frontlinesms2.domain

import frontlinesms2.*

import spock.lang.*
import grails.plugin.spock.*

class AutoforwardISpec extends grails.plugin.spock.IntegrationSpec {
	def "the outgoing message created by processKeyword should have the owner detail set to the id of the triggering incoming message"() {
		when:
			def messageSendService = Mock(MessageSendService)
			def desiredOutbound = new Fmessage(inbound:false, text:"Someone said something").addToDispatches(new Dispatch(dst:"12345", status:DispatchStatus.PENDING)).save(failOnError:true)
			messageSendService.createOutgoingMessage(_) >> desiredOutbound
			def inbound = new Fmessage(inbound: true, src:"54321", text:"this should trigger an outgoing message").save()
			def keyword = new Keyword(value:"DOESNTMATTER")
			def autoforward = new Autoforward(name: "test", sentMessageText: "Someone said something")
				.addToKeywords(keyword)
				.addToContacts(new Contact(name:"Soja", mobile:"12345"))
				.addToContacts(new Contact(name:"Meja", mobile:"12345321"))
				.save(failOnError:true)
			autoforward.messageSendService = messageSendService
			autoforward.processKeyword(inbound, keyword)
			def outbound = Fmessage.findByText('Someone said something')
		then:
			outbound.ownerDetail == "${inbound.id}"
	}
	def 'Deleting a contact that is part of an autoforward should remove the contact from the autoforward'(){
		when:
			def contact = new Contact(name:'dude', mobileNumber:'+0727272727').save(failOnError:true)
			def contactId = contact.id 
			def autoforward  = new Autoforward(name:'Test', sentMessageText: "this is a sample message")
			autoforward.addToContacts(contact)
			autoforward.save(failOnError:true)
			def controller = new ContactController()
			controller.params['contact-select'] = [contactId]
		and:
			controller.delete()
		then:
			!Autoforward.findByName("Test").contacts.contains(contactId)
	}
}

