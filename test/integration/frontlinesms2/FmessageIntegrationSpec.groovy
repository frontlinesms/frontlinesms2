package frontlinesms2

class FmessageIntegrationSpec extends grails.plugin.spock.IntegrationSpec {

	def 'get deleted messages gets all messages with deleted flag'() {
		setup:
				(1..3).each {
				    new Fmessage(deleted:true).save(flush:true)
				}
				(1..2).each {
					new Fmessage(deleted:false).save(flush:true)
				}
		when:
			def deletedMessages = Fmessage.getDeletedMessages(false, 10, 0)
		then:
			deletedMessages.size() == 3
	}
}
