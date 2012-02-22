package frontlinesms2.service

import frontlinesms2.*

class FconnectionServiceISpec extends grails.plugin.spock.IntegrationSpec {
	def fconnectionService
	
	def "createRoute should create a system notification when a exception occurs"() {
		setup:
			def conn = Fconnection.build()
			SystemNotification.findAll()*.delete(flush:true)
		when:
			fconnectionService.createRoutes(conn)
		then:
			SystemNotification.count() == 1
	}

	def "createRoute should not recreate an already existing notification when the same exception occurs"() {
		setup:
			def conn = Fconnection.build()
			SystemNotification.findAll()*.delete(flush:true)
		when:
			fconnectionService.createRoutes(conn)
		then:
			SystemNotification.count() == 1
		when:
			fconnectionService.createRoutes(conn)
		then:
			SystemNotification.count() == 1
	}	
}
