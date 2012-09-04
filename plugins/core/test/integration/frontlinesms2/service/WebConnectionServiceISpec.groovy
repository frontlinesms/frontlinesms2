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
	def 'out_body should not contain the RequestParameters for GET request'() {
 		given:
			def x = mockExchange("simple","get")
		when:
			webConnectionService.preProcess(x)
		then:
			!x.out.body.contains("username=bob")
			!x.out.body.contains("password=secret")
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
		def p3 = new RequestParameter(name:"message",value:"\${MESSAGE_BODY}")
		webconnection.addToRequestParameters(p1)
		webconnection.addToRequestParameters(p2)
		webconnection.addToRequestParameters(p3)
		webconnection.save(failOnError:true)

		def inMessage = mockIncomingMessage(messageText)
		def x = Mock(Exchange)
		def unitOfWork = Mock(UnitOfWork)
		x.unitOfWork >> unitOfWork
		x.in >> inMessage
		x.in.headers >> ['frontlinesms.fmessageId':inMessage.body.id]
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

	def mockIncomingMessage(String messageText) {
		def m = Mock(Message)
		m.body >> [
			id:'45678',
			message:[
				text:messageText,
				toString:{"mock Fmessage"}
			],
			text:messageText,
			src:'+1234567890',
			toString:{"mock body (inboundMessage)"}
		]
		return m
	}
}