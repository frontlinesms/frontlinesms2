package frontlinesms2.service

import frontlinesms2.*
import spock.lang.*

class KeywordProcessorServiceISpec extends grails.plugin.spock.IntegrationSpec {
	def keywordProcessorService

	def processed = [:]

	@Unroll
	def "activity.processKeyword should be called with most specific keyword match"() {
		given:
			Poll p = createTestPoll()
			def m = createFmessage(messageText)
		when:
			keywordProcessorService.process(m)
		then:
			processed == [matchedKeyword:m]
		where:
			messageText      | matchedKeyword
			'top'            | Keyword.findByActivityAndIsTopLevelAndOwnerDetail(p, true, null)
			'top only'       | Keyword.findByActivityAndIsTopLevelAndOwnerDetail(p, true, null)
			'top bottom1'    | Keyword.findByActivityAndIsTopLevelAndOwnerDetail(p, false, "1")
			'top bottom2'    | Keyword.findByActivityAndIsTopLevelAndOwnerDetail(p, false, "2")
			'top bottom3'    | Keyword.findByActivityAndIsTopLevelAndOwnerDetail(p, false, "3")
			'top bottom4'    | Keyword.findByActivityAndIsTopLevelAndOwnerDetail(p, false, "4")
			'top bottom5'    | Keyword.findByActivityAndIsTopLevelAndOwnerDetail(p, false, "5")
			'top bottom6'    | Keyword.findByActivityAndIsTopLevelAndOwnerDetail(p, true, null)
	}

	@Unroll
	def "no activity should be processed if no keyword matches"() {
		given:
			Poll p = createTestPoll()
			def m = createFmessage(messageText)
		when:
			keywordProcessorService.process(m)
		then:
			processed == [:]
		where:
			messageText << ['should not match', 'topsy turvy', '']
	}

	@Unroll
	def "archived and deleted activities should not be matched"() {
		given:
			Poll p = createTestPoll(archived, deleted)
			def m = createFmessage(messageText)
		when:
			keywordProcessorService.process(m)
		then:
			processed == [:]
		where:
			messageText             | archived | deleted
			'top'                   | true     | false
			'top only'              | true     | false
			'top'                   | false    | true
			'top only'              | false    | true
			'top'                   | true     | true
			'top only'              | true     | true
			'top bottom1'           | true     | false
			'top bottom1'           | false    | true
			'top bottom1'           | true     | true
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

	private def createTestPoll(archived=false, deleted=false) {
		Poll p = new Poll(name:'test poll')
		p.addToKeywords(new Keyword(value:"TOP", isTopLevel: true))
		(1..5).each {
			p.addToResponses(new PollResponse(value: 'poll response ${it}'))
			p.addToKeywords(new Keyword(value: "BOTTOM${it}", isTopLevel: false, ownerDetail: "${it}"))
		}
		p.metaClass.processKeyword = { Keyword k, Message m -> processed << [k:m]}
		p.save(failOnError:true, flush:true)
		p
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

