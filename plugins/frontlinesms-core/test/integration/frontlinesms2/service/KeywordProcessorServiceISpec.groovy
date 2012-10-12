package frontlinesms2.service

import frontlinesms2.*
import spock.lang.*

class KeywordProcessorServiceISpec extends grails.plugin.spock.IntegrationSpec {
	def keywordProcessorService

	def keywordsThatHaveBeenProcessed = [:]

	@Unroll
	def "activity.processKeyword should be called with most specific keyword match"() {
		given:
			Poll p = createTestPoll()
			def m = createFmessage(messageText).save(failOnError:true)
		when:
			keywordProcessorService.process(m)
		then:
			println "*** all Keywords::::"
			Keyword.findAll().each { println "::: ${it.value}"}
			Keyword.findAllByValue(matchedKeyword) == Keyword.findAllByOwnerDetail("PROCESSED")
		where:
			messageText      | matchedKeyword
			'top'            | "TOP"
			'top'            | "TOP"
			'top only'       | "TOP"
			'top bottom1'    | "BOTTOM1"
			'top bottom2'    | "BOTTOM2"
			'top bottom3'    | "BOTTOM3"
			'top bottom4'    | "BOTTOM4"
			'top bottom5'    | "BOTTOM5"
			'top bottom6'    | "TOP"
	}

	@Unroll
	def "no activity should be processed if no keyword matches"() {
		given:
			Poll p = createTestPoll()
			def m = createFmessage(messageText)
		when:
			keywordProcessorService.process(m)
		then:
			Keyword.findAllByOwnerDetail("PROCESSED") == []
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
			Keyword.findAllByOwnerDetail("PROCESSED") == []
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

	private def createTestPoll(archived=false, deleted=false) {
		Poll p = new Poll(name:'test poll', archived: archived, deleted: deleted)
		p.addToKeywords(new Keyword(value:"TOP", isTopLevel: true))
		(1..5).each {
			p.addToResponses(new PollResponse(value: "poll response ${it}"))
			p.addToKeywords(new Keyword(value: "BOTTOM${it}", isTopLevel: false, ownerDetail: "${it}"))
		}
		p.addToResponses(PollResponse.createUnknown())
		Poll.metaClass.processKeyword = { Fmessage m, Keyword k -> 
			println "processing keyword $k, value: ${k.value}"
			k.ownerDetail = "PROCESSED"
			k.save(failOnError:true)
		}
		p.save(failOnError:true, flush:true)
		return p
	}

	private def createFmessage(text) {
		Fmessage.build(text:text)
	}
}

