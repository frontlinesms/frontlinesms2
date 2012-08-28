package frontlinesms2

import grails.test.mixin.*
import spock.lang.*

@TestFor(WebConnection)
@Mock([Keyword])
class WebConnectionSpec extends Specification {
	private static final String TEST_NUMBER = "+2345678"
	def setup() {
	}

	@Unroll
	def "Test constraints"() {
		when:
			def keyword = addKeyword? new Keyword(value:'TEST'): null
			def connection =  new HttpWebConnectionFconnection(name:'Testing', url:"www.frontlinesms.com/sync",httpMethod:HttpWebConnectionFconnection.HttpMethod.GET)
			def extComm = new WebConnection(name:name, keyword:keyword, connection: connection)
		then:
			extComm.validate() == valid
		where:
			name 	| addKeyword 	| valid
			'test' 	| true 			| true
			'test' 	| false 		| true
			'' 		| true 			| false
			null 	| true 			| false

	}

	def 'incoming message matching keyword should trigger message sending through the external command connection'() {
		given:
			def k = mockKeyword('FORWARD')
			def connection =  new HttpWebConnectionFconnection(name:'Testing', url:"www.frontlinesms.com/sync",httpMethod:HttpWebConnectionFconnection.HttpMethod.GET)
			def extCommand = new WebConnection(name:'whatever', keyword:k, connection:connection)

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
			def connection =  new HttpWebConnectionFconnection(name:'Testing', url:"www.frontlinesms.com/sync",httpMethod:HttpWebConnectionFconnection.HttpMethod.GET)
			def extCommand = new WebConnection(name:'whatever', keyword:k, connection:connection)

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