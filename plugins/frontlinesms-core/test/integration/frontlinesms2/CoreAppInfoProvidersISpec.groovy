package frontlinesms2

import frontlinesms2.CoreAppInfoProviders as CAIP

class CoreAppInfoProvidersISpec extends grails.plugin.spock.IntegrationSpec {
	def 'messageStats returns the correct number of messages for a given contact'() {
		setup:
			def contact = Contact.build(mobile:"1234567")
			TextMessage.build(src:'1234567')
		when:
			def stats = CAIP.contactMessageStats(null, null, [id:contact.id])
		then:
			stats.inbound == 1
			stats.outbound == 0
	}
}

