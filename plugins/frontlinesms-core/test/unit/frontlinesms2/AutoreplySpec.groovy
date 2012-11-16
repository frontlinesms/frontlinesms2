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
		then:
			autoreply.validate() == valid
		where:
			name   | replyText   | valid
			null   | null        | false
			null   | 'something' | false
			null   | null        | false
			'test' | null        | false
			'test' | 'something' | true
			'test' | null        | false
			'test' | 'something' | true
	}

	def 'processKeyword should generate an autoreply'() {
		given:
			def autoreply = new Autoreply(name:'whatever', autoreplyText:'some reply text')

			def sendService = Mock(MessageSendService)
			autoreply.messageSendService = sendService

			def replyMessage = mockFmessage("woteva")
			sendService.createOutgoingMessage({ params ->
				params.addresses==TEST_NUMBER && params.messageText=='some reply text'
			}) >> replyMessage

			def inMessage = mockFmessage("message text", TEST_NUMBER)
		when:
			autoreply.processKeyword(inMessage, Mock(Keyword))
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

