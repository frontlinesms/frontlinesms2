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
	
	def "Auto-reply can be edited"() {
		setup:
			mockDomain(Autoreply)
		when:
			def autoreply = Autoreply(name:"title", sentMessageText:"thanks for participaping", keyword:"WHAT")
		then:
			autoreply.save()
		when:
			autoreply = Autoreply.editAutoReply(autoreply.id, [sentMessageText:"thanks again for participating"])
		then:
			autoreply
	}
	
}
