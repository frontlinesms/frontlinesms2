package frontlinesms2

import grails.test.mixin.*
import spock.lang.*

@TestFor(Autoreply)
@Mock([Keyword])
class AutoreplySpec extends Specification {
	private static final String TEST_NUMBER = "+2345678"
	
	def setup() {
		// Not sure why this is necessary with Test Mixins, but it seems to be:
		Autoreply.metaClass.addToMessages = { m ->
			if(delegate.messages) delegate.messages << m
			else delegate.messages = [m]
			return delegate
		}
	}

	@Unroll
	def "Test constraints"() {
		when:
			def autoreply = new Autoreply(name:name, autoreplyText:replyText)
			if(addKeyword)
				autoreply.addToKeywords(Mock(Keyword))
		then:
			autoreply.validate() == valid
		where:
			name   | replyText   | addKeyword | valid
			null   | null        | false      | false
			null   | 'something' | false      | false
			null   | null        | true       | false
			'test' | null        | false      | false
			'test' | 'something' | false      | true
			'test' | null        | true       | false
			'test' | 'something' | true       | true
	}

	def 'processKeyword should generate an autoreply for blank keyword if non-exact match'() {
		given:
			def k = mockKeyword('')
			def autoreply = new Autoreply(name:'whatever', autoreplyText:'some reply text').addToKeywords(k)

			def sendService = Mock(MessageSendService)
			autoreply.messageSendService = sendService

			def replyMessage = mockFmessage("woteva")
			sendService.createOutgoingMessage({ params ->
				params.addresses==TEST_NUMBER && params.messageText=='some reply text'
			}) >> replyMessage

			def inMessage = mockFmessage("message text", TEST_NUMBER)
		when:
			autoreply.processKeyword(inMessage, k)
		then:
			1 * sendService.send(replyMessage)
	}

	def 'processKeyword should generate an autoreply'() {
		given:
			def autoreply = new Autoreply(name:'whatever', autoreplyText:'some reply text')
			def k = new Keyword(value:'test')
			autoreply.addToKeywords(k)

			def sendService = Mock(MessageSendService)
			autoreply.messageSendService = sendService

			def replyMessage = mockFmessage("woteva")
			sendService.createOutgoingMessage({ params ->
				params.addresses==TEST_NUMBER && params.messageText=='some reply text'
			}) >> replyMessage

			def inMessage = mockFmessage("message text", TEST_NUMBER)
		when:
			autoreply.processKeyword(inMessage, k)
		then:
			1 * sendService.send(replyMessage)
	}

	private def mockKeyword(String value) {
		return Mock(Keyword)
	}

	private def mockFmessage(String messageText, String src=null) {
		Fmessage m = Mock()
		m.text >> messageText
		m.src >> src
		return m
	}
}

