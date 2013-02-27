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
			def connection = new UshahidiWebconnection(name:name, url:"http://www.ushahidi.com/frontlinesms2", httpMethod:method)
		then:
			println connection.errors
			connection.validate() == valid
		where:
			name	|valid | method
			'test'	|true  | Webconnection.HttpMethod.POST
			'test'	|true  | Webconnection.HttpMethod.POST
			''		|false | Webconnection.HttpMethod.POST
			null	|false | Webconnection.HttpMethod.POST
	}

	def "Test Ushahidi pre-processor"() {
		given:
			def message = Fmessage.build(text:"testing", src:"23423")
			def connection = new UshahidiWebconnection(name:'name',
					url:"www.ushahidi.com/frontlinesms2",
					httpMethod:Webconnection.HttpMethod.GET)

			def mockService = Mock(WebconnectionService)
			mockService.getProcessedValue(_,_) >> { a -> "test" }
			connection.webconnectionService = mockService
			mockRequestParams(connection, [s:'${message_src_name}', m:'${message_body}', key:'1234567'])

			def headers = ['fmessage-id':message.id,'webconnection-id':connection.id]
			def exchange = mockExchange(null, headers)
		when:
			connection.preProcess(exchange)
		then:
			exchange.in.headers[Exchange.HTTP_QUERY].contains('m=test')
			exchange.in.headers[Exchange.HTTP_QUERY].contains('s=test')
			exchange.in.headers[Exchange.HTTP_QUERY].contains('key=test')
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
		def outMessage = Mock(Message)
		outMessage.headers >> [:]
		outMessage.body >> [:]
		x.out >> outMessage
		return x
	}
}
