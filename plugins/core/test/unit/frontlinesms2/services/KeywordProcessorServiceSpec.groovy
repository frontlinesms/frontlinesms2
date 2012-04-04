package frontlinesms2.services

import frontlinesms2.*
import spock.lang.*
import grails.plugin.spock.UnitSpec
import org.apache.camel.CamelContext
import org.apache.camel.Exchange
import org.apache.camel.Message

class KeywordProcessorServiceSpec extends UnitSpec {
	def service
	
	def setup() {
		service = new KeywordProcessorService()
	}

	@Unroll
	def "activity_process should be called for matching keywords"() {
		given:
			mockKeywords(keywordValues)
			def activity = Keyword.findByValue(matchedKeyword).activity
			def m = mockFmessage(messageText)
		when:
			service.process(m)
		then:
			1 * activity.processKeyword(m, exactMatch)
			0 * _.processKeyword(_, _)
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
			mockKeywords(keywordValues)
			def m = mockFmessage(messageText)
		when:
			service.process(m)
		then:
			0 * _.processKeyword(_, _)
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

	def mockKeywords(words, processKeyword=null) {
		println "Mocking keywords with processKeyword = ${processKeyword.toString()}"
		
		def keywords = words.collect {
			Activity a = Mock()
			a.processKeyword(_, _) >> { x, y -> processKeyword.call(x, y) }
			Keyword k = Mock()
			k.activity >> a
			k.value >> it.toUpperCase()
			println "Creating keyword with value $it: $k.value"
			println "Creating keyword with value $it: $k.value"
			println "Creating keyword with value $it: $k.value"
			k
		}

		println "Finished creating keywords: ${keywords*.value}"
		keywords.each { println it.value }
		
		registerMetaClass Keyword
		Keyword.metaClass.static.findByValue = { value ->
			println "Trying to match keyword $value against ${keywords*.value}"
			keywords.each { println it.value }
			return keywords.find { it.value == value }
		}
		
		return keywords
	}
	
	def mockFmessage(String messageText) {
		Fmessage m = Mock()
		m.text >> messageText
		return m
	}
}
