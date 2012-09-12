package frontlinesms2

import spock.lang.*
import grails.test.mixin.*

@TestFor(EmailFconnection)
class EmailFconnectionSpec extends Specification {
	def 'serverPort is nullable'() {
		when:
			def serverPort = createEmailFconnectionWithServerPort(1234)
		then:
			serverPort.validate()
		when:
			def noServerPort = createEmailFconnectionWithServerPort(null)
		then:
			noServerPort.validate()
	}
	
	def createEmailFconnectionWithServerPort(serverPort) {
		new EmailFconnection(name: 'test', receiveProtocol:EmailReceiveProtocol.IMAP,
				serverName:'example.com', serverPort:serverPort, username:'user',
				password:'secret')
	}
}

