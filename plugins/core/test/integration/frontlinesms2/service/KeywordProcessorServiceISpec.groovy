package frontlinesms2.service

import frontlinesms2.*
import spock.lang.*

class KeywordProcessorServiceISpec extends grails.plugin.spock.IntegrationSpec {
	def keywordProcessorService

	def processed = [:]

	@Unroll
	def "activity_process should be called for matching keywords"() {
		given:
			createKeywords(keywordValues)
			def activity = Keyword.findByValue(matchedKeyword).activity
			def m = createFmessage(messageText)
		when:
			keywordProcessorService.process(m)
		then:
			processed == [(activity):exactMatch]
		where:
			keywordValues      | messageText          | matchedKeyword | exactMatch
			['']               | ''                   | ''             | true
			['']               | '   '                | ''             | true
			['']               | 'whatever'           | ''             | false
			['']               | 'two words'          | ''             | false
			['A', 'B']         | 'a very nice day'    | 'A'            | true
			['A', 'B']         | 'a\nvery nice day'   | 'A'            | true
			['A', 'B']         | 'by jove'            | 'B'            | false
			['A']              | '\r\n    A'          | 'A'            | true
			['', 'A']          | '\r\n    B'          | ''             | false
			['A', 'AB']        | 'ab'                 | 'AB'           | true
			['A', 'AB']        | 'ac'                 | 'A'            | false
			['LONG', 'LONGER'] | 'long time no see'   | 'LONG'         | true
			['LONG', 'LONGER'] | 'longo bongo'        | 'LONG'         | false
			['LONG', 'LONGER'] | 'longer time no see' | 'LONGER'       | true
	}

	@Unroll
	def "no activity should be processed if no keyword matches"() {
		given:
			createKeywords(keywordValues)
			def m = createFmessage(messageText)
		when:
			keywordProcessorService.process(m)
		then:
			processed == [:]
		where:
			keywordValues | messageText
			[]            | ''
			[]            | 'word'
			[]            | 'many words'
			['a', 'b']    | 'word'
			['a', 'b']    | 'many words'
			['a', 'b']    | 'averyniceday'
			['a', 'b']    | 'but why'
	}


	def 'Keyword matching should ignore archived and deleted activities when there is an unarchived match'() {
		given:
			createActivities([[archived:true, deleted:false], [archived:false, deleted:true], [archived:false, deleted:false]])
			def activities = Keyword.findAllByValue('A')*.activity
			def activeKeyword = activities[2]
			assert activities*.archived == [true, false, false]
			assert activities*.deleted == [false, true, false]
			def m = createFmessage 'a'
		when:
			keywordProcessorService.process(m)
		then:
			processed == [(activeKeyword):true]
	}

	def 'Keyword matching should ignore archived and deleted activities even if there is no unarchived or undeleted match'() {
		given:
			createActivities([[archived:true, deleted:false], [archived:false, deleted:true]])
			def activities = Keyword.findAllByValue('A')*.activity
			assert activities*.archived == [true, false]
			assert activities*.deleted == [false, true]
			def m = createFmessage 'a'
		when:
			keywordProcessorService.process(m)
		then:
			processed == [:]
	}

	private def createKeywords(keywords) {
		keywords = keywords*.toUpperCase()
		def count = 0
		keywords.collect { keyword ->
			def k = new Keyword(value:keyword)
			k.activity = Autoreply.build(name:"autoreply-${++count}", keyword:k)
			k.activity.metaClass.processKeyword = { Fmessage m, boolean b -> processed << [(delegate):b] }
			k.save(failOnError:true, flush:true)
		}
	}

	private def createActivities(attributes) {
		def count = 0
		attributes.collect { a ->
			def k = new Keyword(value:'A')
			k.activity = Autoreply.build(name:"autoreply-${++count}",
					keyword:k, archived:a.archived, deleted:a.deleted)
			k.activity.metaClass.processKeyword = { Fmessage m, boolean b -> processed << [(delegate):b] }
			k.save(failOnError:true, flush:true)
		}
	}

	private def createFmessage(text) {
		Fmessage.build(text:text)
	}
}

