package frontlinesms2.connection

import frontlinesms2.*

class ConnectionListSpec extends ConnectionGebSpec {
	def 'When there are no connections, this is explained to the user'() {
		when:
			to ConnectionListPage
		then:
			lstConnections.tag() == 'div'
			lstConnections.text() == 'You have no connections configured.'
	}
	
	def 'There is a Not Connected label shown for inactive connection'() {
		when:
			createTestConnection()
			to ConnectionListPage
		then:
			$('.con-status')[0].text() == "Not connected"
		cleanup:
			deleteTestConnections()
	}
//FIXME: Build Fix	
/*	def 'There is a Connected label shown for working connection'() {
		when:
			createTestConnection()
			to ConnectionListPage
		then:
			$('div.status').text() == "Not connected"
		when:
			$(".buttons a").click()
		then:
			$('div.status').text() == "Connected"
		cleanup:
			deleteTestConnections()
	}*/
	
	def 'The first connection in the connection list page is selected'() {
		when: 
			createTestConnection()
			to ConnectionListPage
		then:
			$('#connections .selected').size() == 1
		cleanup:
			deleteTestConnections()
	}
	
	def createTestConnections() {
		[new SmslibFconnection(name:'MTN Dongle', port:'stormyPort'),
				new EmailFconnection(name:'Miriam\'s Clickatell account', receiveProtocol:EmailReceiveProtocol.IMAPS, serverName:'imap.zoho.com',
						serverPort:993, username:'mr.testy@zoho.com', password:'mister')].each() {
			it.save(flush:true, failOnError: true)
		}
	}

	def deleteTestConnections() {
		SmslibFconnection.findAll().each() { it?.delete(flush: true) }
		EmailFconnection.findAll().each() { it?.delete(flush: true) }
		Fconnection.findAll().each() { it?.delete(flush: true) }
	}
}
