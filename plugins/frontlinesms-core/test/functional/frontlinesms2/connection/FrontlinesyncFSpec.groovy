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

	@Unroll
	def 'each checkInterval is represented by the slider, with appropriate text above it'() {
		given:
			setUpTestConnection(true,checkInterval)
		when:
			to PageConnection
			connectionList.frontlineSyncConfigExpander(0).click()
		then:
			connectionList.frontlineSyncCheckIntervalString(0) == "frontlinesync.checkInterval.${expectedString}"
		where:
			checkInterval   | expectedString
			1 		| '1'
			5 		| '5'
			15 		| '15'
			30 		| '30'
			60 		| '60'
			120 		| '120'
			0 		| 'manual'
	}

	def 'when config is changed in the back end while the UI is displayed, the display is updated asynchronously, including the \'dirty\' flag'() {
		given:
			setUpTestConnection(true,0)
		when:
			to PageConnection
			connectionList.frontlineSyncConfigExpander(0).click()
		then:
			connectionList.frontlineSyncConfigSyncStatus(0) == 'frontlinesync.sync.config.dirty.true'
			!connectionList.frontlineSyncSendEnabled(0).value()
			!connectionList.frontlineSyncReceiveEnabled(0).value()
			!connectionList.frontlineSyncMissedCallEnabled(0).value()
			connectionList.frontlineSyncCheckIntervalString(0) == "frontlinesync.checkInterval.manual"
		when:
			remote {
				def fc = FrontlinesyncFconnection.findBySecret('3469')
				fc.sendEnabled = true
				fc.receiveEnabled = true
				fc.missedCallEnabled = true
				fc.checkInterval = 60
				fc.configSynced = true
				fc.save(flush:true, failOnError: true)
				return null
			}
		then:
			waitFor ('very slow') {
				connectionList.frontlineSyncConfigSyncStatus(0) == 'frontlinesync.sync.config.dirty.false'
				connectionList.frontlineSyncSendEnabled(0).value()
				connectionList.frontlineSyncReceiveEnabled(0).value()
				connectionList.frontlineSyncMissedCallEnabled(0).value()
				connectionList.frontlineSyncCheckIntervalString(0) == "frontlinesync.checkInterval.60"
			}
	}

	private def setUpTestConnection(hasBeenContacted=true,checkInterval=0) {
		remote {
			def conn = new FrontlinesyncFconnection(name:"FrontlineSync connection", secret:'3469', enabled:true, sendEnabled: false, receiveEnabled: false, missedCallEnabled: false, checkInterval:checkInterval)
			if(hasBeenContacted) {
				conn.lastConnectionTime = new Date()	
			}
			conn.save(flush: true, failOnError:true)
			return null
		}
	}
}
