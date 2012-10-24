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
			waitFor { notifications.flashMessageText.contains("Connection test email connection was deleted.") }
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
			waitFor('very slow') { connectionList.status == "Connected" }
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

	def 'creating a new fconnection adds the connection to the connections list'() {
		given:
			to PageConnection
			assert connectionList.connection.size() == 0
		when:
			launchCreateWizard()
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
			connectionList.connection.size() == 1
	}

	def 'dialog should dispay error when wrong baud data type is entered'() {
		when:
			launchCreateWizard('smslib')
			connectionForm.smslibbaud = "wrongBaud"
			connectionForm.smslibname = "name"
			connectionForm.smslibport = "port"
			next.click()
		then:
			waitFor { error.text().contains('Please enter only digits') }
	}

	def 'can set up a new Smssync connection'() {
		when:
			launchCreateWizard('smssync')
			smssyncname = 'Henry\'s SMSSync Connection'
			connectionForm.smssyncsecret = 'topcat'
			next.click()
		then:
			confirmSmssyncSecret.text() == '****'
			confirmSmssyncReceiveEnabled.text() == 'Yes'
			confirmSmssyncSendEnabled.text() == 'Yes'
	}

	def 'can set up a new Smssync connection with no secret'() {
		when:
			launchCreateWizard('smssync')
			smssyncname = 'Henry\'s SMSSync Connection'
			connectionForm.smssyncreceiveEnabled = false
			connectionForm.smssyncsendEnabled = false
			next.click()
		then:
			confirmSmssyncName.text() == 'Henry\'s SMSSync Connection'
			confirmSmssyncSecret.text() == 'None'
			confirmSmssyncReceiveEnabled.text() == 'No'
			confirmSmssyncSendEnabled.text() == 'No'
		when:
			submit.click()
		then:
			at PageConnection
		and:
			waitFor { connectionList.selectedConnection.text().contains('Henry') }
	}

	def 'can set up a new IntelliSMS account'() {
		when:
			launchCreateWizard('intellisms')
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

	def 'failed connection should show an edit button in the flash message'() {
		given: 'connection exists'
			createBadConnection()
		when: 'bad connection is started'
			startBadConnection()
		then: 'connection failed message is displayed'
			waitFor('very slow') { js.exec('window.location.reload()') || true; notifications.systemNotificationText.contains('Failed to create route on Bad Port') }
		and: 'there is an edit button available'
			connectionFailedFlashMessageEditButton.displayed
	}

	def 'clicking edit in failed connection flash message should launch connection edit dialog'() {
		given: 'connection exists, is started and failed message is displayed'
			createBadConnection()
			startBadConnection()
			waitFor('very slow') { js.exec('window.location.reload()') || true; notifications.systemNotificationText.contains('Failed to create route on Bad Port') }
		when: 'edit button is clicked'
			connectionFailedFlashMessageEditButton.click()
		then: 'modification dialog is displayed'
			waitFor { at ConnectionDialog }
	}

	private def createBadConnection() {
		MockModemUtils.initialiseMockSerial([
				MOCK97:new CommPortIdentifier('MOCK97', MockModemUtils.createMockPortHandler_badPort())])
		SmslibFconnection.build(name:'Bad Port', port:'MOCK97')
	}

	private def startBadConnection() {
		def connectionId = SmslibFconnection.findByName('Bad Port').id
		to PageConnection, connectionId
		waitFor { connectionList.btnCreateRoute.displayed }
		connectionList.btnCreateRoute.click()
	}

	private def launchCreateWizard(def type=null) {
		to PageConnection
		btnNewConnection.click()
		waitFor('very slow') { at ConnectionDialog }

		if(!type) return

		connectionType.value(type)
		next.click()
		waitFor { $('#' + type + 'name').displayed }
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

