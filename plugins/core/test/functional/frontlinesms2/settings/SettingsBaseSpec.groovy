package frontlinesms2.settings

import frontlinesms2.*

class SettingsBaseSpec extends grails.plugin.geb.GebSpec {
	
	def createTestConnections() {
		[new SmslibFconnection(name:'MTN Dongle', port:'stormyPort'),
				new EmailFconnection(name:'Miriam\'s Clickatell account', receiveProtocol:EmailReceiveProtocol.IMAPS, serverName:'imap.zoho.com',
						serverPort:993, username:'mr.testy@zoho.com', password:'mister')].each() {
			it.save(flush:true, failOnError: true)
		}
	}
	
}