package frontlinesms2

class AutoreplySpec extends grails.plugin.spock.UnitSpec {
	private static final String TEST_NUMBER = "+2345678"

	def "Test constraints"() {
		given:
			mockForConstraintsTests(Autoreply)
		when:
			def keyword = addKeyword? new Keyword(): null
			def autoreply = new Autoreply(name:name, autoreplyText:replyText, keyword:keyword)
		then:
			autoreply.validate() == valid
		where:
			name   | replyText   | addKeyword | valid
			null   | null        | false      | false
			null   | 'something' | false      | false
			null   | null        | true       | false
			'test' | null        | false      | false
			'test' | 'something' | false      | false
			'test' | null        | true       | false
			'test' | 'something' | true       | true
	}

	def 'processKeyword should not generate an autoreply for non-exact keyword match'() {
		given:
			def autoreply = new Autoreply(name:'whatever', autoreplyText:'some reply text')

			def sendService = Mock(MessageSendService)
			autoreply.messageSendService = sendService

			def inMessage = mockFmessage("message text", TEST_NUMBER)
		when:
			autoreply.processKeyword(inMessage, false)
		then:
			0 * sendService._
	}

	def 'processKeyword should generate an autoreply'() {
		given:
			mockDomain Autoreply
			def autoreply = new Autoreply(name:'whatever', autoreplyText:'some reply text')

			def sendService = Mock(MessageSendService)
			autoreply.messageSendService = sendService

			def replyMessage = mockFmessage("woteva")
			sendService.getMessagesToSend({ params ->
				params.addresses==TEST_NUMBER && params.messageText=='some reply text'
			}) >> replyMessage

			def inMessage = mockFmessage("message text", TEST_NUMBER)
		when:
			autoreply.processKeyword(inMessage, true)
		then:
			1 * sendService.send(replyMessage)
	}

	private def mockFmessage(String messageText, String src=null) {
		Fmessage m = Mock()
		m.text >> messageText
		m.src >> src
		return m
	}
}

