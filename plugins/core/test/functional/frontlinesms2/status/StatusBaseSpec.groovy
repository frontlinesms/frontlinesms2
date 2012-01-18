package frontlinesms2.status

import frontlinesms2.*


class StatusBaseSpec extends grails.plugin.geb.GebSpec {
	
	private createTestMessages() {
		(new Date()..new Date()-14).each {
			new Fmessage(dateReceived: it, dateCreated: it, src:"+123456789${it}", inbound:true, text: "A message received on ${it}").save()
			new Fmessage(dateReceived: it, dateCreated: it, src:"+123456789${it}", hasSent:true, text: "A message sent on ${it}").save()
		}
	}
	
	def createTestConnections() {
		[new SmslibFconnection(name:'MTN Dongle', port:'stormyPort'),
				new EmailFconnection(name:'Miriam\'s Clickatell account', receiveProtocol:EmailReceiveProtocol.IMAPS, serverName:'imap.zoho.com',
						serverPort:993, username:'mr.testy@zoho.com', password:'mister')].each() {
			it.save(flush:true, failOnError: true)
		}
	}

}