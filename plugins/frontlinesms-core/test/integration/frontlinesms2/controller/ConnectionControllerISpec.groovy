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
			controller.save()
		then:
			def c = SmssyncFconnection.findByName("Henry's Connection")
			c.receiveEnabled
			c.sendEnabled
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
			controller.params.connectionType = 'smssync'
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
	
	def "can save a new SmppFconnection"() {
		given:
			controller.params.name = "Test SmppFconnection"
			controller.params.connectionType = 'smpp'
			controller.params.username = "test"
			controller.params.password = "test"
			controller.params.port = '5775'
			controller.params.url = 'http://12.23.34.45'
			controller.params.fromNumber = '+1223234'
			controller.params.send = 'true'
			controller.params.receive = 'true'
		when:
			controller.save()
		then:
			def conn = SmppFconnection.findByName("Test SmppFconnection")
			conn.name == "Test SmppFconnection"
			conn.username == "test"
			conn.password == "test"
			conn.port == '5775'
			conn.url == 'http://12.23.34.45'
			conn.fromNumber == '+1223234'
			conn.send == true
			conn.receive == true
	}

	def "can edit an existingSmppFconnection"() {
		given:
			new SmppFconnection(name : "Test SmppFconnection", connectionType : 'smpp', username : "test", password : "test", port : '5775', url : 'http://12.23.34.45', fromNumber : '+1223234', send : 'true', receive : 'true').save(failOnError:true)
			controller.params.name = "Testing SmppFconnection"
			controller.params.connectionType = 'smpp'
			controller.params.username = "testing"
			controller.params.password = "testing"
			controller.params.port = '5770'
			controller.params.url = 'http://12.23.34.45'
			controller.params.fromNumber = '+1223223'
			controller.params.send = 'false'
			controller.params.receive = 'false'
		when:
			controller.save()
		then:
			def conn = SmppFconnection.findByName("Testing SmppFconnection")
			conn.name == "Testing SmppFconnection"
			conn.username == "testing"
			conn.password == "testing"
			conn.port == '5770'
			conn.url == 'http://12.23.34.45'
			conn.fromNumber == '+1223223'
			conn.send == false
			conn.receive == false
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
	
}
