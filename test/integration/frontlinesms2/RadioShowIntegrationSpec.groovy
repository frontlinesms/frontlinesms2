package frontlinesms2

import frontlinesms2.enums.MessageStatus

class RadioShowIntegrationSpec extends grails.plugin.spock.IntegrationSpec {

	def setup() {
		def radioShow = new RadioShow(name: "Health & fitness")
		radioShow.addToMessages(new Fmessage(text: "eat fruits", starred : true))
		radioShow.addToMessages(new Fmessage(text: "eat vegetables"))
		radioShow.addToMessages(new Fmessage(text: "excerise"))
		radioShow.save(flush: true)
	}

	def "should fetch all messages for a radio show"() {
		when:
			def results = RadioShow.findByName("Health & fitness").getShowMessages(false)
		then:
			results*.text.containsAll(["eat fruits", "eat vegetables", "excerise"])
	}

	def "should fetch all messages based on offset and max parameters"() {
		when:
			def results = RadioShow.findByName("Health & fitness").getShowMessages(false, 2, 0)
		then:
			results.size() == 2
	}

	def "should fetch all starred radio show messages"() {
		when:
			def results = RadioShow.findByName("Health & fitness").getShowMessages(true)
		then:
			results*.text == ["eat fruits"]
	}

	def "should count starred radio show messages"() {
		when:
			def starredHealthShowMessages = RadioShow.findByName("Health & fitness").countMessages(true)
			def unstarredHealthShowMessages = RadioShow.findByName("Health & fitness").countMessages(false)
		then:
			starredHealthShowMessages == 1
			unstarredHealthShowMessages == 3
	}


}