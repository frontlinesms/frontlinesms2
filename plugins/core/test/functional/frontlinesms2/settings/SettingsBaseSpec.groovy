package frontlinesms2.settings

import frontlinesms2.*

class SettingsBaseSpec extends grails.plugin.geb.GebSpec {
	def createTestConnections() {
		SmslibFconnection.build(name:'MTN Dongle', port:'stormyPort')
		EmailFconnection.build(name:'Miriam\'s Clickatell account',
				receiveProtocol:EmailReceiveProtocol.IMAPS,
				serverName:'imap.zoho.com',
				serverPort:993,
				username:'mr.testy@zoho.com',
				password:'mister')
	}

	def createTestLogs() {
		new LogEntry(date:new Date(), content: "Log entry one").save()
	}
}
