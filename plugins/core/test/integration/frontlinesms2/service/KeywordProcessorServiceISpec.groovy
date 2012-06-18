package frontlinesms2.service

import frontlinesms2.*

class KeywordProcessorServiceISpec extends grails.plugin.spock.IntegrationSpec {
	def keywordProcessorService

	def processed = []

	def 'Keyword matching should ignore archived activities when there is an unarchived match'() {
		given:
			createKeywords(["A", "A"])
			def activities = Keyword.findAllByValue('A')*.activity
			assert activities.size() == 2
			def activeKeyword = activities[1]
			activeKeyword.archived = false
			activeKeyword.save()
			assert Keyword.findAllByValue('A')*.activity*.archived == [true, false]
			def m = createFmessage 'a'
		when:
			keywordProcessorService.process(m)
		then:
			processed == [activeKeyword]
	}

	def 'Keyword matching should ignore archived activities even if there is no unarchived match'() {
		given:
			createKeywords(["A", "A"])
			def activities = Keyword.findAllByValue('A')*.activity
			assert activities.size() == 2
			assert Keyword.findAllByValue('A')*.activity*.archived == [true, true]
			def m = createFmessage 'a'
		when:
			keywordProcessorService.process(m)
		then:
			processed == []
	}

	private def createKeywords(keywords) {
		keywords.collect { k ->
			k = new Keyword(value:k)
			k.activity = Autoreply.build(keyword:k, archived:true)
			k.activity.metaClass.processKeyword = { Fmessage m, boolean b -> processed << delegate }
			k.save(failOnError:true, flush:true)
		}
	}

	private def createFmessage(text) {
		Fmessage.build(text:text)
	}
}

