package frontlinesms2

class NewConnectionSpec extends grails.plugin.spock.IntegrationSpec {
	
	def controller1
	def controller2
	
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
			controller2.params.baud = '9600'
		when:
			controller2.save()
			println Fconnection.findAll()
			def conn2 = SmslibFconnection.findByPort("COM1")
		then:
			conn2
			conn2.port == 'COM1'
			conn2.baud == '9600'
	}
}