package frontlinesms2.domain

import frontlinesms2.*

import spock.lang.*
import grails.plugin.spock.*

class AutoforwardISpec extends grails.plugin.spock.IntegrationSpec {
	def "the outgoing message created by processKeyword should have the owner detail set to the id of the triggering incoming message"() {
		when:
			def messageSendService = Mock(MessageSendService)
			def forwardService =  new AutoforwardService()

			def desiredOutbound = new Fmessage(inbound:false, text:"Someone said something").addToDispatches(new Dispatch(dst:"12345", status:DispatchStatus.PENDING)).save(failOnError:true)
			messageSendService.createOutgoingMessage(_) >> desiredOutbound
			def inbound = new Fmessage(inbound: true, src:"54321", text:"this should trigger an outgoing message").save()
			def keyword = new Keyword(value:"DOESNTMATTER")
			def autoforward = new Autoforward(name: "test", sentMessageText: "Someone said something")
				.addToKeywords(keyword)
				.addToContacts(new Contact(name:"Soja", mobile:"12345"))
				.addToContacts(new Contact(name:"Meja", mobile:"12345321"))
				.save(failOnError:true)

			forwardService.messageSendService = messageSendService
			autoforward.autoforwardService = forwardService
			autoforward.processKeyword(inbound, keyword)
			def outbound = Fmessage.findByText('Someone said something')
		then:
			outbound.ownerDetail == "${inbound.id}"
	}

	def 'Autoforward.addressesAvailable return true if contacts/groups/smartgroups have contacts'(){
		setup:
			def group = new Group (name:'Group 1').save(failOnError:true)
			def smartGroup = new SmartGroup(name:'Group 2', mobile:'+44').save(failOnError:true)
			def contact1 = new Contact(name:"Soja", mobile:"12345").save(failOnError:true)
			new Contact(name:"tim", mobile:"+4412345321").save(failOnError:true)
			def contact3 = new Contact(name:"Hijo", mobile:"34534534").save(failOnError:true)
			group.addToMembers(contact1).save(failOnError:true)
			def autoforward = new Autoforward(name: "test", sentMessageText: "Someone said something")
				.addToKeywords(new Keyword(value:"DOESNTMATTER"))
				.addToContacts(contact3)
				.addToGroups(group)
				.addToSmartGroups(smartGroup)
				.save(failOnError:true)
		expect:
			autoforward.addressesAvailable()
	}

	def 'Autoforward.addressesAvailable returns false if contacts/groups/smartgroups dont have contacts'(){
		setup:
			def group = new Group (name:'Group 1').save(failOnError:true)
			def smartGroup = new SmartGroup(name:'Group 2', mobile:'+44').save(failOnError:true)
			def contact1 = new Contact(name:"Soja", mobile:"12345").save(failOnError:true)
			new Contact(name:"tim", mobile:"+4412345321").save(failOnError:true)
			new Contact(name:"Hijo", mobile:"34534534").save(failOnError:true)
			group.addToMembers(contact1).save(failOnError:true)
			def autoforward = new Autoforward(name: "test", sentMessageText: "Someone said something")
				.addToKeywords(new Keyword(value:"DOESNTMATTER"))
				.save(failOnError:true)
		expect:
			!autoforward.addressesAvailable()
	}
}

