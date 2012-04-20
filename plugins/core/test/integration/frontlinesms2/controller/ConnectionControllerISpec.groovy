package frontlinesms2.controller

import frontlinesms2.*

class ConnectionControllerISpec extends grails.plugin.spock.IntegrationSpec {
	def controller
	def fconnectionService
	
	def setup() {
		controller = new ConnectionController()
	}

	def "can save new email connection"() {
		setup:
			controller.params.connectionType = 'email'
			controller.params.name = 'test email connection'
			controller.params.receiveProtocol = 'IMAP'
			controller.params.serverName = 'mail.example.com'
			controller.params.serverPort = '1234'
			controller.params.username = 'greg'
			controller.params.password = 'pastie'
		when:
			controller.save()
			def conn = EmailFconnection.findByPassword('pastie')
		then:
			conn
			conn.receiveProtocol == EmailReceiveProtocol.IMAP
			conn.serverName == 'mail.example.com'
			conn.serverPort == 1234
			conn.username == 'greg'
			conn.password =='pastie'
	}

	def "can save new smslib connection"() {
		setup:
			controller.params.connectionType = 'smslib'
			controller.params.name = 'test smslib connection'
			controller.params.port = 'COM1'
			controller.params.baud = 9600
		when:
			controller.save()
			def conn2 = SmslibFconnection.findByPort("COM1")
		then:
			conn2
			conn2.port == 'COM1'
			conn2.baud == 9600
	}
	
	def "can edit email connection"() {
		setup:
			def emailConnection = new EmailFconnection(receiveProtocol:EmailReceiveProtocol.IMAP, name:"test connection", serverName:"imap.gmail.com", serverPort:"1234", username:"geof", password:"3123").save(flush:true, failOnError:true)

			controller.params.id = emailConnection.id
			controller.params.receiveProtocol = 'POP3'
			controller.params.connectionType = 'email'
			controller.params.name = 'new name'
			controller.params.serverName = 'mail.example.com'
			controller.params.serverPort = '5678'
			controller.params.username = 'greg'
			controller.params.password = 'pastie'
		when:
			controller.update()
			emailConnection.refresh()
		then:
			!emailConnection.hasErrors()
			emailConnection.name == 'new name'
			emailConnection.receiveProtocol == EmailReceiveProtocol.POP3
			emailConnection.serverName == 'mail.example.com'
			emailConnection.serverPort == 5678
			emailConnection.username == 'greg'
			emailConnection.password =='pastie'
	}
	
	def "can edit sms connection"() {
		setup:
			def smslibConnection = new SmslibFconnection(name:"test modem", port:"COM2", baud:"11200").save(flush:true, failOnError:true)
			controller.params.id = smslibConnection.id
			controller.params.connectionType = 'smslib'
			controller.params.name = 'test smslib connection'
			controller.params.port = 'COM1'
			controller.params.baud = 9600
			controller.params.pin = "1234"
		when:
			controller.update()
			smslibConnection.refresh()
		then:
			!smslibConnection.hasErrors()
			smslibConnection.port == 'COM1'
			smslibConnection.baud == 9600
			smslibConnection.pin == "1234"
	}
	
	def "can save a new IntelliSmsFconnection"() {
		setup:
			controller.params.name = "Test IntelliSmsFconnection"
			controller.params.connectionType = 'intellisms'
			controller.params.username = "test"
			controller.params.password = "test"
		when:
			controller.save()
			def conn = IntelliSmsFconnection.findByName("Test IntelliSmsFconnection")
		then:
			conn
			conn.name == "Test IntelliSmsFconnection"
			conn.username == "test"
			conn.password == "test"
	}

	def "sendTest redirects to the show LIST action"() {
		setup:
			def emailConnection = new EmailFconnection(receiveProtocol:EmailReceiveProtocol.IMAP, name:"test connection", serverName:"imap.gmail.com", serverPort:"1234", username:"geof", password:"3123").save(flush:true, failOnError:true)
		when:
			controller.params.id = emailConnection.id
			controller.sendTest()
		then:
			controller.response.redirectedUrl == "/connection/list/$emailConnection.id"			
	}
	
	def "sendTest redirects to the SHOW action"() {
		setup:
			def conn = new Fconnection(name:"test")
			conn.save(flush:true)
		when:
			controller.params.id = conn.id
			controller.sendTest()
		then:
			controller.response.redirectedUrl == "/connection/list/$conn.id"
	} 
	
}
