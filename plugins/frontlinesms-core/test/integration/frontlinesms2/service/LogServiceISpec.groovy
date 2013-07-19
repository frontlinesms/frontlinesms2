package frontlinesms2.service

import frontlinesms2.*

class LogServiceISpec extends grails.plugin.spock.IntegrationSpec {
	def logService

	def "handleRouteCreated should create a LogEntry"() {
		setup:
			def connection = Fconnection.build()
			LogEntry.findAll()*.delete(flush:true)
		when:
			logService.handleRouteCreated(connection)
		then:
			LogEntry.count() == 1
	}

	def "handleRouteCreationFailed should create a LogEntry"() {
		setup:
			def connection = Fconnection.build()
			LogEntry.findAll()*.delete(flush:true)
		when:
			logService.handleRouteCreationFailed(connection)
		then:
			LogEntry.count() == 1
	}
}
