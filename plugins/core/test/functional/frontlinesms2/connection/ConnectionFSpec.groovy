package frontlinesms2.connection

import frontlinesms2.*
import frontlinesms2.*
import frontlinesms2.dev.MockModemUtils

import serial.mock.MockSerial
import serial.mock.CommPortIdentifier

class ConnectionFSpec extends grails.plugin.geb.GebSpec {
	def cleanup() {
		SmslibFconnection.findAll()*.delete(flush:true)
		EmailFconnection.findAll()*.delete(flush:true)
		Fconnection.findAll()*.delete(flush:true)
	}
	
	def 'When there are no connections, this is explained to the user'() {
		when:
			to ConnectionPage
		then:
			lstConnections.tag() == 'div'
			lstConnections.text().contains('You have no connections configured.')
	}
	
	def 'There is a Not Connected label shown for inactive connection'() {
		when:
			createTestEmailConnection()
			to ConnectionPage
		then:
			txtStatus == "Not connected"
	}
	
	def 'should show "create route" button for inactive connection '() {
		when:
			createTestEmailConnection()
			to ConnectionPage
		then:
			btnCreateRoute.displayed
	}
	
	def 'There is a Connected label shown for working connection'() {
		when:
			createTestSmsConnection()
			to ConnectionPage
		then:
			txtStatus == "Not connected"
		when:
			btnCreateRoute.click()
		then:
			waitFor { txtStatus == "Connected" }
	}
	
	def 'The first connection in the connection list page is selected'() {
		when: 
			createTestEmailConnection()
			to ConnectionPage
		then:
			$('#connections .selected').size() == 1
	}
	
	def 'clicking on a connection shows us more details'() {
		given:
			createTestSmsConnection()
			to ConnectionPage
		when:
			lstConnections.find('h2').click()
		then:
			$('title').text() == "Settings > Connections > MTN Dongle"
	}
	
	def "should update message count when in Settings section"() {
		when:
			to ConnectionPage
			def message = new Fmessage(src:'+254999999', text: "message count", inbound:true, date: new Date()).save(flush: true, failOnError:true)
		then:
			$("#message-tab-link").text().equalsIgnoreCase("Messages\n0")
		when:
			js.refreshMessageCount()
		then:
			waitFor { $("#message-tab-link").text().equalsIgnoreCase("Messages\n1") }
	}
	
	def 'Send test message button for particular connection appears when that connection is selected and started'() {
		given:
			def testConnection = createTestSmsConnection()
			new SmslibFconnection(name:"test modem", port:"COM2", baud:"11200").save(flush:true, failOnError:true)
		when:
			to ConnectionPage
		then:
			$('#connections .selected .test').isEmpty()
		when:
			waitFor{ $("#connections .selected .route").displayed }
			btnCreateRoute.click()
		then:
			waitFor { btnTestRoute.@href == "/connection/createTest/${testConnection.id}" }
			$('#notifications').text().contains("Created route")
	}

	def 'creating a new fconnection causes a refresh of the connections list'(){
		given:
			createTestEmailConnection()
		when:
			to ConnectionPage
			btnNewConnection.click()
		then:
			waitFor { at ConnectionDialog }
		when:
			connectionForm.connectionType = "smslib"
			nextPageButton.click()
			connectionForm.smslibname = "name"
			connectionForm.smslibport = "COM2"
			connectionForm.smslibbaud = "9600"
			nextPageButton.click()
		then:
			confirmName.text() == "name"
			confirmPort.text() == "COM2"
			confirmType.text() == "Phone/Modem"
		when:
			doneButton.click()
		then:
			waitFor { selectedConnection.text().contains('name') }
			println "TEXT: ${lstConnections.find('li')*.text()}"
			lstConnections.find('li').size() == 2
	}
	
	def 'clicking Send test message takes us to a page with default message and empty recieving number field'() {
		given:
			def email = createTestEmailConnection()
		when:
			go "connection/createTest/${email.id}"
		then:
			assertFieldDetailsCorrect('addresses', 'Number', '')
			assertFieldDetailsCorrect('messageText', 'Message', "Congratulations from FrontlineSMS \\o/ you have successfully configured ${email.name} to send SMS \\o/")
	}

	def assertFieldDetailsCorrect(fieldName, labelText, expectedValue) {
		def label = $('label', for:fieldName)
		assert label.text() == labelText
		assert label.@for == fieldName
		def input
		if (fieldName == 'addresses') {
			input = $('input', name: fieldName)
		} else {
			input = $('textarea', name: fieldName)
		}
		assert input.@name == fieldName
		assert input.@id == fieldName
		assert input.@value  == expectedValue
		true
	}

	def createTestConnections() {
		createTestEmailConnection()
		createTestSmsConnection()
	}
	
	def createTestEmailConnection() {
		def c = new EmailFconnection(name:'test email connection',
				receiveProtocol:EmailReceiveProtocol.IMAPS,
				serverName:'imap.zoho.com', serverPort:993,
				username:'mr.testy@zoho.com', password:'mter')
		c.save(failOnError:true, flush:true)
		return c
	}
	
	def createTestSmsConnection() {
		def c = new SmslibFconnection(name:'MTN Dongle', port:'COM99')
		c.save(failOnError:true, flush:true)
		MockModemUtils.initialiseMockSerial([
				COM99:new CommPortIdentifier('COM99', MockModemUtils.createMockPortHandler_sendFails())])
		return c
	}
}

class ConnectionDialog extends ConnectionPage {
	static at = {
		$("#ui-dialog-title-modalBox").text().toLowerCase().contains('connection')
	}
	
	static content = {
		connectionForm { $('#connectionForm')}
		doneButton { $("#submit") }
		nextPageButton { $("#nextPage") }
		confirmName { $("#confirm-name")}
		confirmType { $("#confirm-type")}
		confirmPort { $("#confirm-port")}
	}
}
