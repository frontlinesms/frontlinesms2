package frontlinesms2.connection
import spock.lang.*

import frontlinesms2.*
import frontlinesms2.popup.*
import frontlinesms2.dev.MockModemUtils

import serial.mock.MockSerial
import serial.mock.CommPortIdentifier
import spock.lang.*

class FrontlinesyncFSpec extends grails.plugin.geb.GebSpec {
	def 'can toggle the display of FrontlineSync\'s config using the provided link'() {
		given:
			setUpTestConnection()
		when:
			to PageConnection
		then:
			connectionList.frontlineSyncConfigExpander(0).displayed
			!connectionList.frontlineSyncConfigHolder(0).displayed
		when:
			connectionList.frontlineSyncConfigExpander(0).click()
		then:
			waitFor {
				connectionList.frontlineSyncConfigHolder(0).displayed
			}
	}

	def 'can change the sendEnabled, receiveEnabled and missedCallEnabled attributes via inline config editor'() {
		given:
			setUpTestConnection()
		when:
			to PageConnection
		then:
			connectionList.frontlineSyncConfigExpander(0).displayed
		when:
			connectionList.frontlineSyncConfigExpander(0).click()
		then:
			waitFor {
				connectionList.frontlineSyncConfigHolder(0).displayed
			}
			!connectionList.frontlineSyncSendEnabled(0).value()
			!connectionList.frontlineSyncReceiveEnabled(0).value()
			!connectionList.frontlineSyncMissedCallEnabled(0).value()
		when:
			connectionList.frontlineSyncSendEnabled(0).click()
			connectionList.frontlineSyncReceiveEnabled(0).click()
			connectionList.frontlineSyncMissedCallEnabled(0).click()
			connectionList.frontlineSyncSaveConfig(0).click()	
			to PageConnection
		then:
			waitFor {
				connectionList.frontlineSyncSendEnabled(0).value()
				connectionList.frontlineSyncReceiveEnabled(0).value()
				connectionList.frontlineSyncMissedCallEnabled(0).value()
			}
	}

	private def setUpTestConnection() {
		remote {
			new FrontlinesyncFconnection(name:"FrontlineSync connection", secret:'3469', enabled:true, sendEnabled: false, receiveEnabled: false, missedCallEnabled: false, checkInterval:0).save(flush: true, failOnError:true)
			return null
		}
	}
}
