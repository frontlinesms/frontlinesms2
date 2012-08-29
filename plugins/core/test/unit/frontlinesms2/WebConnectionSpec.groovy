package frontlinesms2

import grails.test.mixin.*
import spock.lang.*
import frontlinesms2.*

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
			def extComm = new WebConnection(name:name, keyword:keyword, url:"www.frontlinesms.com/sync",httpMethod:WebConnection.HttpMethod.GET)
		then:
			extComm.validate() == valid
		where:
			name	|addKeyword	|valid
			'test'	|true		|true
			'test'	|false		|true
			''		|true		|false
			null	|true		|false

	}

	def 'incoming message matching keyword should trigger http message sending'() {
		given:
			def k = mockKeyword('FORWARD')
			def extCommand = new WebConnection(name:"Sync", keyword:k, url:"www.frontlinesms.com/sync",httpMethod:WebConnection.HttpMethod.GET)
			def incomingMessage = mockFmessage("FORWARD ME", TEST_NUMBER)
		when:
			extCommand.processKeyword(incomingMessage, true)
		then:
			1 * extCommand.send(incomingMessage)
	}

	def 'incoming message should match if keyword is blank and exactmatch == false'() {
		given:
			def k = mockKeyword('')
			def extCommand = new WebConnection(name:"Sync", keyword:k, url:"www.frontlinesms.com/sync",httpMethod:WebConnection.HttpMethod.GET)
			def incomingMessage = mockFmessage("FORWARD ME", TEST_NUMBER)
		when:
			extCommand.processKeyword(incomingMessage, false)
		then:
			1 * extCommand.send(incomingMessage)
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