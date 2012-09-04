package frontlinesms2

import grails.test.mixin.*
import spock.lang.*
import frontlinesms2.*
import frontlinesms2.camel.*
import org.apache.camel.Exchange
import org.apache.camel.Message

@TestFor(WebConnection)
@Mock([Keyword])
class WebConnectionSpec extends CamelUnitSpecification {
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
	//Preprocessor tests
	def 'out_body should be set to message text'() {
		given:
			def k = mockKeyword('FORWARD')
			def webconnection = new WebConnection(name:"Sync", keyword:k, url:"www.frontlinesms.com/sync",httpMethod:WebConnection.HttpMethod.GET)
			def x = mockExchange("simple")
		when:
			webconnection.preProcess(x)
		then:
			1 * x.out.setBody("simple")
	}

	def 'out_body should be URL-encoded'() {
		given:
			def k = mockKeyword('FORWARD')
			def webconnection = new WebConnection(name:"Sync", keyword:k, url:"www.frontlinesms.com/sync",httpMethod:WebConnection.HttpMethod.GET)
			def x = mockExchange("more complex")
		when:
			webconnection.preProcess(x)
		then:
			1 * x.out.setBody("more+complex")
	}

	def 'dispatch ID should be set in header'() {
		given:
			def k = mockKeyword('FORWARD')
			def webconnection = new WebConnection(name:"Sync", keyword:k, url:"www.frontlinesms.com/sync",httpMethod:WebConnection.HttpMethod.GET)
			def x = mockExchange("simple")
		when:
			webconnection.preProcess(x)
		then:
			x.out.headers.'frontlinesms.dispatch.id' == '45678'
	}

	def 'message destination should be set and stripped of leading plus'() {
		given:
			def k = mockKeyword('FORWARD')
			def webconnection = new WebConnection(name:"Sync", keyword:k, url:"www.frontlinesms.com/sync",httpMethod:WebConnection.HttpMethod.GET)
			def x = mockExchange("simple")
		when:
			webconnection.preProcess(x)
		then:
			x.out.headers.'server.dst' == '1234567890'
	}

	def 'other details should be set in header'() {
		given:
			def k = mockKeyword('FORWARD')
			def webconnection = new WebConnection(name:"Sync", keyword:k, url:"www.frontlinesms.com/sync",httpMethod:WebConnection.HttpMethod.GET)
			def p1 = new RequestParameter(name:"username",value:"bob")
			def p2 = new RequestParameter(name:"password",value:"secret")
			webconnection.addToRequestParameters(p1)
			webconnection.addToRequestParameters(p2)
			webconnection.save(failOnError:true)
			def x = mockExchange("simple")
		when:
			webconnection.preProcess(x)
		then:
			x.out.headers.'server.username' == 'bob'
			x.out.headers.'server.password' == 'secret'
	}
	//PostProcessor Tests
	def 'Successful responses should do nothing exciting'() {
		given:
			def k = mockKeyword('FORWARD')
			def webconnection = new WebConnection(name:"Sync", keyword:k, url:"www.frontlinesms.com/sync",httpMethod:WebConnection.HttpMethod.GET)
			def x = mockHTTPResponseExchange("ID: 2345678")
		when:
			webconnection.postProcess(x)
		then:
			notThrown(RuntimeException)
	}

	private def mockHTTPResponseExchange(httpResponseText) {
		def x = Mock(Exchange)
		def inMessage = Mock(Message)
		inMessage.body >> httpResponseText
		inMessage.getBody(byte[]) >> httpResponseText.getBytes('UTF-8')
		x.in >> inMessage
		return x
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