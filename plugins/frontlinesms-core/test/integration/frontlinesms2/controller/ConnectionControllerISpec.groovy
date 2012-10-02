package frontlinesms2.controller

import frontlinesms2.*

class ConnectionControllerISpec extends grails.plugin.spock.IntegrationSpec {
	def controller
	def fconnectionService
	
	def setup() {
		controller = new ConnectionController()
	}

	def "can save new smslib connection"() {
		given:
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

	def "can save new smssync connection"() {
		given:
			controller.params.connectionType = 'smssync'
			controller.params.name = 'Henry\'s Connection'
			controller.params.secret = 'dibble'
		when:
			controller.save
		then:
			def c = SmssyncFconnection.findByName("Henry's Connection")
			!c.receiveEnabled
			!c.sendEnabled
			c.secret == 'dibble'
	}
	
	def "can edit sms connection"() {
		given:
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

	def 'can edit smssync connection'() {
		given:
			def c = new SmssyncFconnection(name:'h', sendEnabled:true, receiveEnabled:false, secret:null).save(failOnError:true, flush:true)
		when:
			controller.params.id = c.id
			controller.params.name = 'i'
			controller.params.receiveEnabled = true
			controller.params.sendEnabled = false
			controller.params.secret = 'humbug'
			controller.update()
		then:
			c.refresh()
			c.name == 'i'
			!c.hasErrors()
			c.receiveEnabled
			!c.sendEnabled
			c.secret == 'humbug'
	}
	
	def "can save a new IntelliSmsFconnection"() {
		given:
			controller.params.name = "Test IntelliSmsFconnection"
			controller.params.connectionType = 'intellisms'
			controller.params.send = 'true'
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
		given:
			def emailConnection = new EmailFconnection(receiveProtocol:EmailReceiveProtocol.IMAP, name:"test connection", serverName:"imap.gmail.com", serverPort:"1234", username:"geof", password:"3123").save(flush:true, failOnError:true)
		when:
			controller.params.id = emailConnection.id
			controller.sendTest()
		then:
			controller.response.redirectedUrl == "/connection/list/$emailConnection.id"			
	}
	
	def "sendTest redirects to the SHOW action"() {
		given:
			def conn = new Fconnection(name:"test")
			conn.save(flush:true)
		when:
			controller.params.id = conn.id
			controller.sendTest()
		then:
			controller.response.redirectedUrl == "/connection/list/$conn.id"
	}
	
	def "can edit an existing IntelliSmsFconnection"() {
		given:
			def intellismsConn = new IntelliSmsFconnection(send:true, name:"Test IntelliSmsFconnection", username:"test", password:"****").save(flush:true)
			controller.params.connectionType = 'intellisms'
			controller.params.id = intellismsConn.id
			controller.params._intellismssend = ''
			controller.params.receive = 'true'
			controller.params.receiveProtocol = "POP3"
			controller.params.serverName = 'pop3.gmail.com'
			controller.params.serverPort = '465'
			controller.params.emailUserName = 'test'
			controller.params.emailPassword = 'bla'
		when:
			controller.update()
			def conn = IntelliSmsFconnection.findByName("Test IntelliSmsFconnection")
		then:
			conn
			!conn.send
			conn.username == "test"
			conn.password == "****"
			conn.receive
			conn.receiveProtocol == EmailReceiveProtocol.POP3
			conn.serverName == "pop3.gmail.com"
			conn.serverPort == 465
			conn.emailUserName == "test"
			conn.emailPassword == "bla"
	}
	
}
