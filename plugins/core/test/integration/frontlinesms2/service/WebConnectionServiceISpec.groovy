package frontlinesms2.service

import frontlinesms2.*
import spock.lang.*
import org.apache.camel.Exchange
import org.apache.camel.Message
import org.apache.camel.spi.UnitOfWork
import org.apache.camel.spi.RouteContext
import org.apache.camel.model.RouteDefinition

class WebConnectionServiceISpec extends grails.plugin.spock.IntegrationSpec{
	def webConnectionService
	def setup() {
		def k = new Keyword(value:'FOREWARD')
		def webconnection = new WebConnection(name:"Sync", keyword:k, url:"www.frontlinesms.com/sync",httpMethod:WebConnection.HttpMethod.GET).save(failOnError:true)
	}
	//Pre-Processor Tests
	def 'out_header url should contain the RequestParameters for GET request'() {
 		given:
			def x = mockExchange("simple","get")
		when:
			webConnectionService.preProcess(x)
			println "**** " + x.out.headers.url
		then:
			x.out.headers.url.contains(urlEncode("username=bob"))
			x.out.headers.url.contains(urlEncode("password=secret"))
	}

	def 'out_body should contains the RequestParameters for POST request'(){
		given:
			def x = mockExchange("simple","post")
		when:
			webConnectionService.preProcess(x)
		then:
			x.out.body.contains("username=bob")
			x.out.body.contains("password=secret")
	}

	def 'exchange header should have all the necessary extra information in the header'() {
		given:
			def x = mockExchange("simple","post")
		when:
			webConnectionService.preProcess(x)
		then:
			x.out.headers.'ownerid' != ''
			x.out.headers.'sender' == '45678'
	}

	def 'exchange body should contains the substituted values'() {
		given:
			def x = mockExchange("simple","post")
		when:
			webConnectionService.preProcess(x)
		then:
			x.out.body.contains("message=simple")
	}
	//Post-Processor Tests
	def 'Successful responses should do nothing exciting'() {
		given:
			def x = mockExchange("simple","post")
		when:
			webConnectionService.postProcess(x)
		then:
			notThrown(RuntimeException)
	}

	Exchange mockExchange(messageText,method){
		def webconnection =  WebConnection.findByName("Sync")
		if(method ==  'get'){
			webconnection.httpMethod = WebConnection.HttpMethod.GET
		} else {
			webconnection.httpMethod = WebConnection.HttpMethod.POST
		}
		def p1 = new RequestParameter(name:"username",value:"bob")
		def p2 = new RequestParameter(name:"password",value:"secret")
		def p3 = new RequestParameter(name:"message",value:"\${message_body}")
		println "*** ${method} *** ${webconnection.httpMethod}"
		webconnection.addToRequestParameters(p1)
		webconnection.addToRequestParameters(p2)
		webconnection.addToRequestParameters(p3)
		webconnection.save(failOnError:true, flush:true)
		def message = Fmessage.build(text:messageText)
		def inMessage = mockIncomingMessage(['frontlinesms.fmessageId':message.id,'frontlinesms.webConnectionId':webconnection.id], null)
		def x = Mock(Exchange)
		def unitOfWork = Mock(UnitOfWork)
		x.unitOfWork >> unitOfWork
		x.in >> inMessage
		def out = Mock(Message)
		out.headers >> [:]
		x.out >> out
		println "mockExchange() : x = $x"
		return x
	}

	private String urlEncode(String s) throws UnsupportedEncodingException {
		println "PreProcessor.urlEncode : s=$s -> ${URLEncoder.encode(s, "UTF-8")}"
		return URLEncoder.encode(s, "UTF-8");
	}

	def mockIncomingMessage(headers, body) {
		def m = Mock(Message)
		m.body >> body
		m.headers >> headers
		return m
	}
}