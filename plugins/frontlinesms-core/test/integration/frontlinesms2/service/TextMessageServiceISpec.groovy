package frontlinesms2.service

import frontlinesms2.*

import spock.lang.*

class TextMessageServiceISpec extends grails.plugin.spock.IntegrationSpec {

	def textMessageService 

	def "search returns results with a given max and offset"() {
		setup:
			setUpMessages()
			def search = new Search(searchString: 'inbox')
		when:
			def firstInboxMessage = textMessageService.search(search, [max: 1,  offset:0]).interactionInstanceList
			def firstTwoInboxMessages = textMessageService.search(search, [max: 2, offset: 0]).interactionInstanceList
			def allMsgsWithTheGivenSearchString = textMessageService.search(search).interactionInstanceList
		then:
			firstInboxMessage.size() == 1
			firstTwoInboxMessages.size() == 2
			allMsgsWithTheGivenSearchString.size() == 3
			textMessageService.search(search).interactionInstanceTotal == 3
	}

	def "messages are fetched based on message status"() {
		setup:
			setUpMessages()
			def search = new Search(status: ['INBOUND'])
			def search2 = new Search(status: ['SENT', 'PENDING', 'FAILED'])
			def search3 = new Search(searchString: "")
		when:
			def allInboundMessages = textMessageService.search(search).interactionInstanceList
			def allSentMessages = textMessageService.search(search2).interactionInstanceList
			def allMessages = textMessageService.search(search3).interactionInstanceList
		then:
			allInboundMessages*.every { it.inbound }
			allSentMessages*.every { !it.inbound }
			allMessages.size() == 7
	}
	
	def "searching for a group with no members does not throw an error"() {
		setup:
			def footballGroup = new Group(name: "football").save(flush: true)
			def search = new Search(group: footballGroup)
		when:
			def searchMessages = textMessageService.search(search).interactionInstanceList
		then:
			!searchMessages
	}

	@Unroll
	def "searching for a partial name of a contact will match messages he has sent and received"() {
		given:
			def messages = [:]
			[robert:'123', bernie:'456', iane:'789'].each { contactName, mobile ->
				Contact.build(name:contactName, mobile:mobile)
				def sent = new TextMessage(text:'')
						.addToDispatches(dst:mobile, status:DispatchStatus.PENDING)
						.save(failOnError:true, flush:true)
				def received = TextMessage.build(src:mobile).save(failOnError:true, flush:true)
				messages[contactName] = [received, sent]
			}
		expect:
			textMessageService.search([contactString:contactString]).interactionInstanceList == contactNames.inject([]) { m, c -> m += messages[c] }
		where:
			contactString | contactNames
			'ROB'         | ['robert']
			'bER'         | ['bernie', 'robert']
			'i'           | ['iane', 'bernie']
			'e'           | ['iane', 'bernie', 'robert']
	}

	private TextMessage buildWithDispatches(Dispatch... dispatches) {
		def m = TextMessage.buildWithoutSave(inbound:false)
		dispatches.each { m.addToDispatches(it) }
		m.save(failOnError:true, flush:true)
	}

	private TextMessage buildOutgoing(params) {
		def m = buildWithDispatches(params.dispatches)
		if(params.containsKey('deleted')) m.isDeleted = params.deleted
		if(params.text) m.text = params.text
		return m
	}

	private def setUpMessages() {
		TextMessage.build(text:"An inbox message")
		TextMessage.build(text:"Another inbox message")
		buildWithDispatches(dispatch())
		buildWithDispatches(dispatch(), dispatch())
		buildOutgoing(deleted:true, dispatches:dispatch())
		buildOutgoing(text:'This msg will not show up in inbox view', dispatches:dispatch())
		buildWithDispatches(failedDispatch())
		buildWithDispatches(pendingDispatch())
	}

	private def dispatch() { sentDispatch() }
	private def sentDispatch() { new Dispatch(dst:'1234', status:DispatchStatus.SENT, dateSent:new Date()) }
	private def pendingDispatch() { new Dispatch(dst:'1234', status:DispatchStatus.PENDING) }
	private def failedDispatch() { new Dispatch(dst:'1234', status:DispatchStatus.FAILED) }
}
