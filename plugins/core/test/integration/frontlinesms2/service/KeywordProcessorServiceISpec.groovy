package frontlinesms2.service

import frontlinesms2.*

class KeywordProcessorServiceISpec extends grails.plugin.spock.IntegrationSpec {
	def keywordProcessorService

	def processed = []

	def 'Keyword matching should ignore archived and deleted activities when there is an unarchived match'() {
		given:
			createKeywords([archived:true, deleted:false], [archived:true, deleted:false], [archived:false, deleted:false])
			def activities = Keyword.findAllByValue('A')*.activity
			def activeKeyword = activities[2]
			assert activities*.archived == [true, false, false]
			assert activities*.deleted == [false, true, false]
			def m = createFmessage 'a'
		when:
			keywordProcessorService.process(m)
		then:
			processed == [activeKeyword]
	}

	def 'Keyword matching should ignore archived and deleted activities even if there is no unarchived or undeleted match'() {
		given:
			createKeywords([archived:true, deleted:false], [archived:true, deleted:false])
			def activities = Keyword.findAllByValue('A')*.activity
			assert activities*.archived == [true, false]
			assert activities*.deleted == [false, true]
			def m = createFmessage 'a'
		when:
			keywordProcessorService.process(m)
		then:
			processed == []
	}

	private def createKeywords(attributes) {
		attributes.collect { a ->
			k = new Keyword(value:'A')
			k.activity = Autoreply.build(keyword:k, archived:a.archived, deleted:a.deleted)
			k.activity.metaClass.processKeyword = { Fmessage m, boolean b -> processed << delegate }
			k.save(failOnError:true, flush:true)
		}
	}

	private def createFmessage(text) {
		Fmessage.build(text:text)
	}
}

