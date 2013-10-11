package frontlinesms2.connection

import spock.lang.*

import frontlinesms2.*
import frontlinesms2.popup.*
import frontlinesms2.dev.MockModemUtils

import serial.mock.MockSerial
import serial.mock.CommPortIdentifier
import spock.lang.*

class ConnectionFSpec extends grails.plugin.geb.GebSpec {
	def 'When there are no connections, this is explained to the user'() {
		when:
			to PageConnection
		then:
			btnNewConnection.displayed
			noContent?.text()?.contains('connection.list.none')
	}

	def 'There is a Failed label shown for failed connection'() {
		when:
			to PageConnection, createSecondTestEmailConnection()
		then:
			connectionList.status(0).jquery.hasClass("FAILED")
	}

	def 'There is a Disabled label shown for disabled connection'() {
		when:
			to PageConnection, createTestEmailConnection()
		then:
			connectionList.status(0).jquery.hasClass("DISABLED")
	}

	def 'there is a DELETE button shown for inactive connection'() {
		when:
			to PageConnection, createTestEmailConnection()
		then:
			connectionList.btnDelete(0).displayed
	}

	def 'should show "create route" button for inactive connection'() {
		when:
			to PageConnection, createTestEmailConnection()
		then:
			connectionList.btnEnableRoute(0).displayed
	}

	def 'DELETE button should remove selected fconnection from the list'() {
		given:
			to PageConnection, createTestEmailConnection()
		when:
			connectionList.btnDelete(0).click()
		then:
			waitFor { notifications.flashMessageText.contains("connection.deleted[test email connection]") }
			noContent.displayed
			noContent?.text()?.contains('connection.list.none')
	}

	def 'Send test message button for particular connection displayed on a successfully created route'() {
		given:
			def testConnection = createTestEmailConnection()
		when:
			to PageConnection, testConnection
			waitFor { connectionList.displayed }
			$('div.controls').jquery.css("visibility", "visible")

			connectionList.btnEnableRoute(0).click()
		then:
			waitFor('slow') { connectionList.btnTestRoute(0).displayed }
			waitFor { connectionList.status(0).jquery.hasClass("CONNECTED") }
	}

	def 'delete button is not displayed for a connected Fconnection'() {
		given:
			def c = createTestEmailConnection()
		when:
			to PageConnection, c
			connectionList.btnEnableRoute(0).click()
		then:
			waitFor('very slow') { connectionList.status(0).jquery.hasClass("CONNECTED") }
			!connectionList.btnDelete(0).displayed
	}

	def 'creating a new fconnection adds the connection to the connections list'() {
		given:
			to PageConnection
			assert noContent.displayed
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
			confirmType.text() == "smslib.label"
		when:
			submit.click()
		then:
			at PageConnection
			waitFor { connectionList.connectionName(0).contains('name') }
			connectionList.listSize() == 1
	}

/* FIXME TODO FIXME TODO make this work reliably.  Prizes on offer.
 Have commented this out as it seems to randomly fail to validate.

 Supect this is a race condition with the validation not being applied to the popup
 in time, but strangely putting lots of calls to `sleep()` has not helped.
	def 'dialog should dispay error when wrong baud data type is entered'() {
		when:
			launchCreateWizard('smslib')
			sleep 500
			connectionForm.smslibbaud = "wrongBaud"
			connectionForm.smslibname = "name"
			connectionForm.smslibport = "port"
		then:
			// wait for javascript to load... not sure of a cleaner way to do that
			sleep 500
		when:
			next.click()
		then:
			waitFor { error.text()?.contains('Please enter only digits') }
	}*/

	def 'can set up a new Smssync connection'() {
		when:
			launchCreateWizard('smssync')
			smssyncname = 'Henry\'s SMSSync Connection'
			connectionForm.smssyncsecret = 'topcat'
			next.click()
		then:
			confirmSmssyncSecret.text() == 'topcat'
	}

	def 'Smslib connection has a description under its name'() {
		when:
			launchCreateWizard()
		then:
			basicInfo("smslib") == "smslib.description"
	}

	def 'Smssync connection has a description under its name'() {
		when:
			launchCreateWizard()
		then:
			basicInfo("smssync") == "smssync.description"
	}

	def 'Clickatell connection has a description under its name'() {
		when:
			launchCreateWizard()
		then:
			basicInfo("clickatell") == "clickatell.description"
	}

	def 'Intellisms connection has a description under its name'() {
		when:
			launchCreateWizard()
		then:
			basicInfo("intellisms") == "intellisms.description"
	}

	def 'can set up a new Smssync connection with no secret'() {
		when:
			launchCreateWizard('smssync')
			smssyncname = 'Henry\'s SMSSync Connection'
			next.click()
		then:
			confirmSmssyncName.text() == 'Henry\'s SMSSync Connection'
			confirmSmssyncSecret.text() == ''
		when:
			submit.click()
		then:
			at PageConnection
		and:
			waitFor { connectionList.connectionName(0).contains('Henry') }
	}

	def 'can set up a new IntelliSMS account'() {
		when:
			launchCreateWizard('intellisms')
			connectionForm.intellismssendEnabled = true
			connectionForm.intellismsname = "New IntelliSMS Connection"
			connectionForm.intellismsusername = "test"
			connectionForm.intellismspassword = "1234"
			next.click()
		then:
			confirmIntelliSmsConnectionName.text() == "New IntelliSMS Connection"
			confirmIntelliSmsUserName.text() == "test"
			confirmIntelliSmsType.text() == "intellisms.label"
		when:
			submit.click()
		then:
			at PageConnection
			waitFor { connectionList.connectionName(0).contains('New IntelliSMS Connection') }
	}

	def 'clicking Send test message displays a popup with a default message and empty address field'() {
		given:
			to PageConnection, createTestEmailConnection()
		when:
			connectionList.btnEnableRoute(0).click()
		then:
			waitFor('slow') { connectionList.btnTestRoute(0).displayed }
		when:
			connectionList.btnTestRoute(0).click()
		then:
			waitFor { at TestMessagePopup }
			addresses == ''
			message == "connection.test.message[test email connection]"
	}

	def 'failed connection should show an edit button in the flash message'() {
		given: 'connection exists'
			createBadConnection()
		when: 'bad connection is started'
			startBadConnection()
		then: 'connection failed message is displayed'
			waitFor('very slow') {
				js.exec('window.location.reload()') || true
				notifications.systemNotificationText.startsWith('connection.route.failNotification[12,Bad Port,connection.error.java.io.ioexception[This is a bad mock port :(]]')
			}
		and: 'there is an edit button available'
			connectionFailedFlashMessageEditButton.displayed
	}

	def 'clicking edit in failed connection flash message should launch connection edit dialog'() {
		given: 'connection exists, is started and failed message is displayed'
			createBadConnection()
			startBadConnection()
			waitFor('very slow') { js.exec('window.location.reload()') || true; notifications.systemNotificationText.contains('connection.route.failNotification') }
		when: 'edit button is clicked'
			connectionFailedFlashMessageEditButton.click()
		then: 'modification dialog is displayed'
			waitFor { at ConnectionDialog }
	}

	private def createBadConnection() {
		remote {
			MockModemUtils.initialiseMockSerial([
					MOCK97:new CommPortIdentifier('MOCK97', MockModemUtils.createMockPortHandler_badPort())])
			SmslibFconnection.build(name:'Bad Port', port:'MOCK97').id
		}
	}

	private def startBadConnection() {
		def connectionId = remote { SmslibFconnection.findByName('Bad Port').id }
		to PageConnection, connectionId
		waitFor { connectionList.btnRetryConnection(0).displayed }
		connectionList.btnRetryConnection(0).click()
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
		remote {
			EmailFconnection.build(name:'test email connection',
					receiveProtocol:EmailReceiveProtocol.IMAPS,
					serverName:'imap.zoho.com', serverPort:993,
					username:'mr.testy@zoho.com', password:'mter',
					enabled:false).id }
	}

	def createSecondTestEmailConnection() {
		remote {
			EmailFconnection.build(name:'test email connection',
					receiveProtocol:EmailReceiveProtocol.IMAPS,
					serverName:'imap.zoho.com', serverPort:993,
					username:'mr.testy@zoho.com', password:'mter').id }
	}

	def createTestSmsConnection() {
		remote {
			MockModemUtils.initialiseMockSerial([
					COM99:new CommPortIdentifier('COM99', MockModemUtils.createMockPortHandler())])
			SmslibFconnection.build(name:'MTN Dongle', port:'COM99', enabled:true).id }
	}

}

