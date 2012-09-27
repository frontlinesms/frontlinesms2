package frontlinesms2

import grails.test.mixin.*
import spock.lang.*
import frontlinesms2.*
import frontlinesms2.camel.*
import org.apache.camel.Exchange
import org.apache.camel.Message

@TestFor(UshahidiWebConnection)
@Mock([Keyword])
class UshahidiWebConnectionSpec extends CamelUnitSpecification {
	private static final String TEST_NUMBER = "+2345678"
	def setup() {
		WebConnection.metaClass.static.findAllByNameIlike = { name -> UshahidiWebConnection.findAll().findAll { it.name == name } }
	}

	@Unroll
	def "Test constraints"() {
		when:
			def keyword = addKeyword? new Keyword(value:'TEST'): null
			def connection = new UshahidiWebConnection(name:name, keyword:keyword, url:"www.ushahidi.com/frontlinesms2", httpMethod:method)
		then:
			println connection.errors
			connection.validate() == valid
		where:
			name	|addKeyword	|valid | method
			'test'	|true		|true  | WebConnection.HttpMethod.POST
			'test'	|false		|false | WebConnection.HttpMethod.POST
			''		|true		|false | WebConnection.HttpMethod.POST
			null	|true		|false | WebConnection.HttpMethod.POST
	}

	def "Test Ushahidi pre-processor"() {
		when:
			def message = Fmessage.build(text:"testing", src:"bob")
			def connection = new UshahidiWebConnection(name:name, keyword:keyword, url:"www.ushahidi.com/frontlinesms2", httpMethod:method)
			def s = Mock(RequestParameter)
			s >> ['name':'s', 'value':'${message_src_name}']
			def m = Mock(RequestParameter)
			m >> ['name':'m', 'value':'${message_body}']
			def key = Mock(RequestParameter)
			key >> ['name':'key', 'value':'1234567']

			[s, m, key].each { connection.addToRequestParameters(it) }

			Exchange exchange = Mock(Exchange)
			exchange.in >> mockExchangeMessage(['frontlinesms.fmessageId':message.id,'frontlinesms.webConnectionId':webconnection.id], null)
			exchange.out >> mockExchangeMessage([:], null)
			exchange.unitOfWork >> Mock(UnitOfWork)
			connection.preProcess(exchange)
		then:
			1 * exchange.out.setBody({ bodyContent ->
				bodyContent.contains("m=testing") && 
				bodyContent.contains("s=bob") && 
				bodyContent.contains("key=1234567")
			})
	}

	def "Test Ushahidi post-processor"() {
		when:
			def thisTest = "TODO"
			// TODO: if/when we decide to handle different Ushahidi response codes with appropriate actions, the post processor
			// tests need to be expanded to cover this. Currently it just logs the output after 3 attempts.
		then:
			true
	}
	
	def mockExchangeMessage(headers, body) {
		def m = Mock(Message)
		m.body >> body
		m.headers >> headers
		return m
	}
}