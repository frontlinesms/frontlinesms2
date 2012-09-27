package frontlinesms2

import grails.test.mixin.*
import spock.lang.*
import frontlinesms2.*
import frontlinesms2.camel.*
import org.apache.camel.Exchange
import org.apache.camel.Message
import grails.buildtestdata.mixin.Build

@TestFor(UshahidiWebConnection)
@Mock([Keyword])
@Build(Fmessage)
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
			def connection = new UshahidiWebConnection(name:'name',
					keyword:new Keyword(value:'keyword'),
					url:"www.ushahidi.com/frontlinesms2",
					httpMethod:WebConnection.HttpMethod.GET)
			def s = Mock(RequestParameter)
			s >> ['name':'s', 'value':'${message_src_number}']
			def m = Mock(RequestParameter)
			m >> ['name':'m', 'value':'${message_body}']
			def key = Mock(RequestParameter)
			key >> ['name':'key', 'value':'1234567']
		given:
			def message = Fmessage.build(text:"testing", src:"bob")
			def connection = new UshahidiWebConnection(name:'name',
					keyword:new Keyword(value:'keyword'),
					url:"www.ushahidi.com/frontlinesms2",
					httpMethod:WebConnection.HttpMethod.GET)
			mockRequestParams(connection, [s:'${message_src_name}', m:'${message_body}', key:'1234567'])

			def headers = ['fmessage-id':message.id,'webConnection-id':connection.id]
			def exchange = mockExchange(null, headers)
		when:
			connection.preProcess(exchange)
		then:
			headers[Exchange.HTTP_QUERY].contains("m=testing")
			headers[Exchange.HTTP_QUERY].contains("s=bob")
			headers[Exchange.HTTP_QUERY].contains("key=1234567")
	}

	def mockRequestParams(connection, Map params) {
		params.each { key, value ->
			def rp = Mock(RequestParameter)
			rp.name >> key
			rp.value >> value
			connection.addToRequestParameters(rp)
		}
	}

	Exchange mockExchange(body, Map headers) {
		Exchange x = Mock()
		def inMessage = Mock(Message)
		if(body) inMessage.body >> body
		inMessage.headers >> headers
		x.in >> inMessage
		return x
	}
}
