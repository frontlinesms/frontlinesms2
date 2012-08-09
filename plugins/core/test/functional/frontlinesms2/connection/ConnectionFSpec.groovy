package frontlinesms2.connection

import spock.lang.*

import frontlinesms2.*
import frontlinesms2.popup.*
import frontlinesms2.dev.MockModemUtils

import serial.mock.MockSerial
import serial.mock.CommPortIdentifier

class ConnectionFSpec extends grails.plugin.geb.GebSpec {
	def 'When there are no connections, this is explained to the user'() {
		when:
			to PageConnection
		then:
			connectionList.displayed
			connectionList.text().contains('You have no connections configured.')
	}
	
	def 'There is a Not Connected label shown for inactive connection'() {
		when:
			createTestEmailConnection()
			to PageConnection
		then:
			connectionList.status == "Not connected"
	}

	def 'there is a DELETE button shown for inactive connection'() {
		when:
			createTestEmailConnection()
			to PageConnection
		then:
			connectionList.btnDelete.displayed
	}
	
	def 'should show "create route" button for inactive connection '() {
		when:
			createTestEmailConnection()
			to PageConnection
		then:
			connectionList.btnCreateRoute.displayed
	}

	def 'DELETE button should remove selected fconnection from the list'() {
		given:
			createTestEmailConnection()
			to PageConnection
		when:
			connectionList.btnDelete.click()
		then:
			notifications.flashMessagesText.contains("Connection test email connection was deleted.")
			connectionList.text().contains('You have no connections configured.')
	}

	def 'Send test message button for particular connection displayed on a successfully created route'() {
		given:
			def testConnection = createTestSmsConnection()
			SmslibFconnection.build(name:"test modem", port:"COM2", baud:11200)
		when:
			to PageConnection
			waitFor{ connectionList.btnCreateRoute.displayed }
		then:
			!connectionList.btnTestRoute.displayed
		when:
			connectionList.btnCreateRoute.click()
		then:
			waitFor('slow') { connectionList.status == "Connected" }
			waitFor { connectionList.btnTestRoute.displayed }
	}

	def 'delete button is not displayed for a connected Fconnection'() {
		given:
			createTestEmailConnection()
		when:
			to PageConnection
			connectionList.btnCreateRoute.click()
		then:
			waitFor('very slow') { connectionList.status == "Connected" }
			!connectionList.btnDelete.displayed
	}
	
	def 'The first connection in the connection list page is selected'() {
		when: 
			createTestEmailConnection()
			to PageConnection
		then:
			connectionList.selectedConnection.size() == 1
	}

	def 'creating a new fconnection adds the connection to the connections list'(){
		given:
			createTestEmailConnection()
		when:
			to PageConnection
			btnNewConnection.click()
		then:
			waitFor('very slow') { at ConnectionDialog }
		when:
			next.click()
			connectionForm.smslibname = "name"
			connectionForm.smslibport = "COM2"
			connectionForm.smslibbaud = "9600"
			next.click()
		then:
			confirmName.text() == "name"
			confirmPort.text() == "COM2"
			confirmType.text() == "Phone/Modem"
		when:
			submit.click()
		then:
			at PageConnection
			waitFor { connectionList.selectedConnection.text().contains('name') }
			connectionList.connection.size() == 2
	}

	def 'dialog should not close after confirmation screen unless save is successful'(){
		given:
			to PageConnection
			btnNewConnection.click()
			waitFor { at ConnectionDialog }
			next.click()
			connectionForm.smslibname = "name"
			connectionForm.smslibport = "port"
			connectionForm.smslibbaud = "wrongBaud"
			next.click()
		when:
			submit.click()
		then:
			waitFor{ error }
			error == 'baud must be a valid number'
			at ConnectionDialog
			confirmName.text() == "name"
			confirmPort.text() == "port"
			confirmType.text() == "Phone/Modem"
	}

	def 'can setup a new IntelliSMS account'() {
		when:
			to PageConnection
			btnNewConnection.click()
		then:
			waitFor { at ConnectionDialog }
		when:
			connectionType.value("intellisms").jquery.trigger("click")

			next.click()
			connectionForm.intellismssend = true
			connectionForm.intellismsname = "New IntelliSMS Connection"
			connectionForm.intellismsusername = "test"
			connectionForm.intellismspassword = "1234"
			next.click()
		then:
			confirmIntelliSmsConnectionName.text() == "New IntelliSMS Connection"
			confirmIntelliSmsUserName.text() == "test"
			confirmIntelliSmsType.text() == "IntelliSms Account"
		when:
			submit.click()
		then:
			at PageConnection
			waitFor { connectionList.selectedConnection.text().contains('New IntelliSMS Connection') }
	}

	def 'clicking Send test message displays a popup with a default message and empty address field'() {
		given:
			createTestEmailConnection()
			to PageConnection
		when:
			connectionList.btnCreateRoute.click()
		then:
			waitFor('slow') {connectionList.btnTestRoute.displayed}
		when:
			connectionList.btnTestRoute.click()
		then:
			waitFor { at TestMessagePopup }
			addresses == ''
			message == "Congratulations from FrontlineSMS \\o/ you have successfully configured test email connection to send SMS \\o/"
	}

	def createTestConnections() {
		createTestEmailConnection()
		createTestSmsConnection()
	}
	
	def createTestEmailConnection() {
		EmailFconnection.build(name:'test email connection',
				receiveProtocol:EmailReceiveProtocol.IMAPS,
				serverName:'imap.zoho.com', serverPort:993,
				username:'mr.testy@zoho.com', password:'mter')
	}
	
	def createTestSmsConnection() {
		MockModemUtils.initialiseMockSerial([
				COM99:new CommPortIdentifier('COM99', MockModemUtils.createMockPortHandler())])
		SmslibFconnection.build(name:'MTN Dongle', port:'COM99')
	}
}

