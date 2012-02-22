package frontlinesms2.controller

import frontlinesms2.*

class ConnectionControllerISpec extends grails.plugin.spock.IntegrationSpec {
	
	def controller1
	def controller2
	def fconnectionService
	
	def setup() {
		controller1 = new ConnectionController()
		controller2 = new ConnectionController()
	}

	def "can save new email connection"() {
		setup:
			EmailReceiveProtocol receiveProtocol
			controller1.params.connectionType = 'email'
			controller1.params.name = 'test email connection'
			controller1.params.receiveProtocol = 'imap'
			controller1.params.serverName = 'mail.example.com'
			controller1.params.serverPort = '1234'
			controller1.params.username = 'greg'
			controller1.params.password = 'pastie'
		when:
			controller1.save()
			def conn = EmailFconnection.findByPassword("pastie")
		then:
			conn
			conn.receiveProtocol.toString() == 'imap'
			conn.serverName == 'mail.example.com'
			conn.serverPort == 1234
			conn.username == 'greg'
			conn.password =='pastie'
	}

	def "can save new smslib connection"() {
		setup:
			controller2.params.connectionType = 'smslib'
			controller2.params.name = 'test smslib connection'
			controller2.params.port = 'COM1'
			controller2.params.baud = 9600
		when:
			controller2.save()
			def conn2 = SmslibFconnection.findByPort("COM1")
		then:
			conn2
			conn2.port == 'COM1'
			conn2.baud == 9600
	}
	
	def "can edit email connection"() {
		setup:
			def emailConnection = new EmailFconnection(receiveProtocol:EmailReceiveProtocol.valueOf("imap".toUpperCase()), name:"test connection",
					 serverName:"imap.gmail.com", serverPort:"1234", username:"geof", password:"3123").save(flush:true, failOnError:true)

			controller1.params.id = emailConnection.id
			controller1.params.receiveProtocol = 'imap'
			controller1.params.connectionType = 'email'
			controller1.params.name = 'test email connection'
			controller1.params.serverName = 'mail.example.com'
			controller1.params.serverPort = '1234'
			controller1.params.username = 'greg'
			controller1.params.password = 'pastie'
		when:
			controller1.update()
			emailConnection.refresh()
		then:
			!emailConnection.hasErrors()
			emailConnection.receiveProtocol.toString() == 'imap'
			emailConnection.serverName == 'mail.example.com'
			emailConnection.serverPort == 1234
			emailConnection.username == 'greg'
			emailConnection.password =='pastie'
	}
	
	def "can edit sms connection"() {
		setup:
			def smslibConnection = new SmslibFconnection(name:"test modem", port:"COM2", baud:"11200").save(flush:true, failOnError:true)
			controller1.params.id = smslibConnection.id
			controller2.params.connectionType = 'smslib'
			controller2.params.name = 'test smslib connection'
			controller2.params.port = 'COM1'
			controller2.params.baud = 9600
			controller2.params.pin = "1234"
		when:
			controller2.update()
			smslibConnection.refresh()
		then:
			!smslibConnection.hasErrors()
			smslibConnection.port == 'COM1'
			smslibConnection.baud == 9600
			smslibConnection.pin == "1234"
	}
	
}
