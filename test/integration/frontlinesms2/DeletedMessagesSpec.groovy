package frontlinesms2

class DeletedMessagesSpec extends grails.plugin.spock.IntegrationSpec {
	
	def 'Deleted messages do not show up in Inbox'() {
		when:
			def message = new Fmessage(src:'Bob', dst:'+254987654', text:'I like manchester', status:MessageStatus.INBOUND).save()
		then:
			Fmessage.getInboxMessages().size() == 1
		when:
			message.toDelete().save(flush:true, failOnError:true)
		then:
			Fmessage.getInboxMessages().size() == 1
		cleanup:
			deleteTestData()
	}
	
	static deleteTestData() {
		Fmessage.findAll().each() {
			it.refresh()
			it.delete(failOnError:true, flush:true)
		}
	}
}
