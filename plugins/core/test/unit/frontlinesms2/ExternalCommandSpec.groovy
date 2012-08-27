package frontlinesms2

import grails.test.mixin.*
import spock.lang.*

@TestFor(ExternalCommand)
@Mock([Keyword])
class ExternalCommandSpec extends Specification {
	private static final String TEST_NUMBER = "+2345678"
	def setup() {
	}

	@Unroll
	def "Test constraints"() {
		when:
			def keyword = addKeyword? new Keyword(): null
			def extComm = new ExternalCommand(name:name, url:url, keyword:keyword, type:type)
		then:
			extComm.validate() == valid
		where:
			name     | url                                       | addKeyword | type   	             | valid
			'test'   | 'http://192.168.0.200:8080/test'          | true       | ExternalCommand.GET  | true
			'test'   | 'http://192.168.0.200:8080/test'          | true       | ExternalCommand.POST | true
			null     | 'http://192.168.0.200:8080/test'          | true       | ExternalCommand.POST | false
			'test'   | null                                      | true       | ExternalCommand.POST | false
			'test'   | 'http://192.168.0.200:8080/test'          | false      | ExternalCommand.POST | false
			'test'   | 'http://192.168.0.200:8080/test'          | true       | "not a valid value!" | false
			'test'   | 'not a valid URL'                         | true       | ExternalCommand.POST | false
	}

	def 'incoming message matching keyword should trigger message sending through the external command connection'() {
		given:
			def k = mockKeyword('FORWARD')
			def extCommand = new ExternalCommand(name:'whatever', url:'http://192.168.0.200:8080/test', keyword:k, type:ExternalCommand.POST)

			def sendService = Mock(MessageSendService)
			extCommand.messageSendService = sendService

			def forwardedMessage = mockFmessage("FORWARD ME")
			sendService.createOutgoingMessage({ params ->
				params.addresses==null && params.messageText=='test'
			}) >> forwardedMessage

			def incomingMessage = mockFmessage("FORWARD ME", TEST_NUMBER)
		when:
			extCommand.processKeyword(incomingMessage, true)
		then:
			1 * sendService.send(replyMessage, extCommand.connection)
	}

	def 'incoming message should match if keyword is blank and exactmatch == false'() {
		given:
			def k = mockKeyword('')
			def extCommand = new ExternalCommand(name:'whatever', url:'http://192.168.0.200:8080/test', keyword:k, type:ExternalCommand.POST)

			def sendService = Mock(MessageSendService)
			extCommand.messageSendService = sendService

			def forwardedMessage = mockFmessage("FORWARD ME")
			sendService.createOutgoingMessage({ params ->
				params.addresses==null && params.messageText=='test'
			}) >> forwardedMessage

			def incomingMessage = mockFmessage("FORWARD ME", TEST_NUMBER)
		when:
			extCommand.processKeyword(incomingMessage, false)
		then:
			1 * sendService.send(replyMessage, extCommand.connection)
	}

	private def mockKeyword(String value) {
		new Keyword(value:value)
	}

	private def mockFmessage(String messageText, String src=null) {
		Fmessage m = Mock()
		m.text >> messageText
		m.src >> src
		return m
	}

}