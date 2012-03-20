package frontlinesms2

class AutoreplySpec extends grails.plugin.spock.UnitSpec {

	def "AutoReplies must have outgoing message text and a keyword"() {
		given:
			mockDomain(Autoreply)
		when:
			def a = new Autoreply(name:'test Autoreply')
		then:
			!a.validate()
		when:
			a.sentMessageText = "You sent me a message, why?"
		then:
			!a.validate()
		when:
			a.keyword = new Keyword()
		then:
			a.validate()
	}
}
