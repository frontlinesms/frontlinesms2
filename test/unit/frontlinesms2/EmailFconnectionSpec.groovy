package frontlinesms2

import spock.lang.*
import grails.plugin.spock.*

class EmailFconnectionSpec extends UnitSpec {
	def 'serverPort is nullable'() {
		setup:
			mockForConstraintsTests(EmailFconnection)
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
		new EmailFconnection(name: 'test', protocol:EmailProtocol.IMAP,
				serverName:'example.com', serverPort:serverPort, username:'user',
				password:'secret')
	}
		
}
