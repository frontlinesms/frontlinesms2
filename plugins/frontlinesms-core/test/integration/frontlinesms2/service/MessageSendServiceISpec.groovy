package frontlinesms2.service

import frontlinesms2.*

class MessageSendServiceISpec extends grails.plugin.spock.IntegrationSpec {
	def messageSendService
	
	def 'createOutgoingMessage should support Group/SmartGroup ids/instances, as well as addresses'() {
		given:
			def contact1 = Contact.build(name: "test1", mobile:"+11111").save() // in g1
			def contact2 = Contact.build(name: "test2", mobile:"+22222").save() // in g2
			def contact3 = Contact.build(name: "test3", mobile:"+33333").save() // in sg1
			def contact4 = Contact.build(name: "test4", mobile:"+44444").save() // in sg2
			def contact5 = Contact.build(name: "test5", mobile:"+334455").save() // in g1 and sg1
			def contact6 = Contact.build(name: "test6", mobile:"+66666").save() // in no groups or smartgroups
			def sg1 = SmartGroup.build(name: "test-smartgroup-1", mobile: "+33").save()
			def sg2 = SmartGroup.build(name: "test-smartgroup-2", mobile: "+44").save()
			def g1 = Group.build(name: "test-group-1").save()
			def g2 = Group.build(name: "test-group-2").save()
			g1.addToMembers(contact1)
			g1.addToMembers(contact5)
			g2.addToMembers(contact2)
		when:
			def params = [message: "test message",
				groups:["group-${g1.id}", g2, "smartgroup-${sg1.id}", sg2],
				addresses:["+66666", "+77777"]]
			def generatedAddresses = messageSendService.createOutgoingMessage(params).dispatches*.dst
		then:
			generatedAddresses.size() == 6
			generatedAddresses.containsAll(["+11111", "+22222", "+33333", "+44444", "+55555", "+66666", "+77777"])
	}
}
