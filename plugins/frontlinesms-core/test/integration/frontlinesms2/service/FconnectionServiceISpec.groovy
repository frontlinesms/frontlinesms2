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

	def "enableFconnection should create system notification updating connection fails do to validation"(){
		setup:
			def conn = Fconnection.build(name:'name')
		when:
			fconnectionService.enableFconnection(conn)
			def conn2 = Fconnection.build(name:'other name')
			SystemNotification.findAll()*.delete(flush:true)
			conn2.name = ''
			fconnectionService.enableFconnection(conn2)
		then:
			SystemNotification.count() == 2
			SystemNotification.findAll()*.text.contains("Property [name] of class [Fconnection] cannot be blank")
	}
}
