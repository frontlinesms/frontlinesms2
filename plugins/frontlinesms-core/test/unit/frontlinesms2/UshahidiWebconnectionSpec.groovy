package frontlinesms2

import grails.test.mixin.*
import spock.lang.*
import frontlinesms2.*
import frontlinesms2.camel.*
import org.apache.camel.Exchange
import org.apache.camel.Message
import grails.buildtestdata.mixin.Build

@TestFor(UshahidiWebconnection)
@Mock([Keyword])
@Build(Fmessage)
class UshahidiWebconnectionSpec extends CamelUnitSpecification {
	private static final String TEST_NUMBER = "+2345678"
	def setup() {
		Webconnection.metaClass.static.findAllByNameIlike = { name -> UshahidiWebconnection.findAll().findAll { it.name == name } }
	}

	@Unroll
	def "Test constraints"() {
		when:
			def keyword = addKeyword? new Keyword(value:'TEST'): null
			def connection = new UshahidiWebconnection(name:name, url:"www.ushahidi.com/frontlinesms2", httpMethod:method).addToKeywords(keyword)
		then:
			println connection.errors
			connection.validate() == valid
		where:
			name	|addKeyword	|valid | method
			'test'	|true		|true  | Webconnection.HttpMethod.POST
			'test'	|false		|true | Webconnection.HttpMethod.POST
			''		|true		|false | Webconnection.HttpMethod.POST
			null	|true		|false | Webconnection.HttpMethod.POST
	}

	def "Test Ushahidi pre-processor"() {
		given:
			def message = Fmessage.build(text:"testing", src:"bob")
			def connection = new UshahidiWebconnection(name:'name',
					url:"www.ushahidi.com/frontlinesms2",
					httpMethod:Webconnection.HttpMethod.GET)
					.addToKeywords(new Keyword(value:'keyword'))
			mockRequestParams(connection, [s:'${message_src_name}', m:'${message_body}', key:'1234567'])

			def headers = ['fmessage-id':message.id,'webconnection-id':connection.id]
			def exchange = mockExchange(null, headers)
		when:
			connection.preProcess(exchange)
		then:
			exchange.out.headers[Exchange.HTTP_QUERY].contains('m=test')
			exchange.out.headers[Exchange.HTTP_QUERY].contains('s=test')
			exchange.out.headers[Exchange.HTTP_QUERY].contains('key=test')
	}

	def mockRequestParams(connection, Map params) {
		params.each { key, value ->
			def rp = Mock(RequestParameter)
			rp.name >> key
			rp.value >> value
			rp.getProcessedValue(_) >> { a -> "test" }
			connection.addToRequestParameters(rp)
		}
	}

	Exchange mockExchange(body, Map headers) {
		Exchange x = Mock()
		def inMessage = Mock(Message)
		if(body) inMessage.body >> body
		inMessage.headers >> headers
		x.in >> inMessage
		def outMessage = Mock(Message)
		outMessage.headers >> [:]
		outMessage.body >> [:]
		x.out >> outMessage
		return x
	}
}
