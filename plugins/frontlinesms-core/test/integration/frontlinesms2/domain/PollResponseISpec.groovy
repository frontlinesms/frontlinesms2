package frontlinesms2.domain

import frontlinesms2.*

class PollResponseISpec extends grails.plugin.spock.IntegrationSpec {
	def "Adding a message to a PollResponse will set the message_messageOwner tp the poll"() {
		given:
			def p = new Poll(name:'new')
			def r = new PollResponse(value:'yes', key:'yes')
			p.addToResponses(PollResponse.createUnknown())
			p.addToResponses(value:'No', key:'No')
			p.addToResponses(r)
			p.save(flush:true, failOnError:true)
			def m = Fmessage.build()
		when:
			r.addToMessages(m)
			p.save(failOnError:true, flush:true)
			r.refresh()
			m.refresh()
		then:
			r.messages.contains(m)
			m.messageOwner == p
	}

	def "PollResponse.getMessages() only returns inbound messages associated with the poll"() {
		given:
			def p = new Poll(name:'new')
			def r = new PollResponse(value:'yes', key:'yes')
			p.addToResponses(PollResponse.createUnknown())
			p.addToResponses(value:'No', key:'No')
			p.addToResponses(r)
			p.save(flush:true, failOnError:true)
			def m1 = Fmessage.buildWithoutSave(inbound: false, date: new Date() - 10)
			m1.addToDispatches(Dispatch.build())
			def m2 = Fmessage.build(src: "src4", inbound: true, date: new Date() - 10)
			def m3 = Fmessage.build(src: "src4", inbound: true, date: new Date() - 10)
			[m1, m2, m3].each { p.addToMessages(it) }
			m1.ownerDetail = r.id
			m1.save()
			m3.ownerDetail = r.id
			m3.save()
		when:
			def messages = r.getMessages()	
		then:
			messages == [m3]
	}
}

