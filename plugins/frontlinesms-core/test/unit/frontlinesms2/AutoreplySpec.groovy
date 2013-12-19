package frontlinesms2

import grails.test.mixin.*
import spock.lang.*

@TestFor(Autoreply)
@Mock([Keyword, AutoreplyService])
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

	def 'processKeyword should invoke doReply on autoreplyService'() {
		given:
			def autoreply = new Autoreply(name:'whatever', autoreplyText:'some reply text')

			def autoreplyService = Mock(AutoreplyService)
			autoreply.autoreplyService = autoreplyService

			def inMessage = mockTextMessage("message text", TEST_NUMBER)
		when:
			autoreply.processKeyword(inMessage, Mock(Keyword))
		then:
			1 * autoreplyService.doReply(autoreply, inMessage)
	}

	private def mockTextMessage(String messageText, String src=null) {
		TextMessage m = Mock()
		m.text >> messageText
		m.src >> src
		return m
	}
}

