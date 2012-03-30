package frontlinesms2.service

import frontlinesms2.*

class failPendingMessagesServiceISpec extends grails.plugin.spock.IntegrationSpec {
	def failPendingMessagesService
	
	def "init should fail all pending dispatches"() {
		setup:
			def m = new Fmessage(src: 'src', inbound: false, archived: false, hasSent: true, hasPending: true, hasFailed: true, date: new Date())
			[Dispatch.build(status: DispatchStatus.FAILED, message: m),
					Dispatch.build(status: DispatchStatus.PENDING, message: m),
					Dispatch.build(status: DispatchStatus.SENT, message: m, dateSent: new Date())].each() {
				it.save(flush:true, failOnError: true)
			}
		when:
			failPendingMessagesService.init()
		then:
			Dispatch.findAllByStatus(DispatchStatus.FAILED).size() == 2
	}
	
	def "init should create system notification if it fails any messages"() {
		setup:
			def m = new Fmessage(src: 'src', inbound: false, archived: false, hasSent: true, hasPending: true, hasFailed: true, date: new Date())
			SystemNotification.findAll()*.delete(flush:true)
		when:
			failPendingMessagesService.init()
		then:
			SystemNotification.count() == 0
		when:
			[Dispatch.build(status: DispatchStatus.FAILED, message: m),
					Dispatch.build(status: DispatchStatus.PENDING, message: m),
					Dispatch.build(status: DispatchStatus.SENT, message: m, dateSent: new Date())].each() {
				it.save(flush:true, failOnError: true)
			}
			failPendingMessagesService.init()
		then:
			SystemNotification.count() == 1
	}
}
