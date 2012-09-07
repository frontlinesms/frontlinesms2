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
			def x = mockExchange("simple","get", false)
		when:
			webConnectionService.preProcess(x)
			println "**** " + x.out.headers.url
		then:
			x.out.headers.url.contains("username=bob")
			x.out.headers.url.contains("password=secret")
	}

	def 'out_body should contains the substituted RequestParameters for POST request'(){
		given:
			def x = mockExchange("test message","post", true)
		when:
			webConnectionService.preProcess(x)
		then:
			1* x.out.setBody({ bodyContent ->
				bodyContent.contains("message=test+message")
			})
	}

	def 'URL is not modified when request is POST'(){
		given:
			def x = mockExchange("test message","post", true)
		when:
			webConnectionService.preProcess(x)
		then:
			x.out.headers.url == "www.frontlinesms.com/sync"
	}

	def 'exchange body should contains all values substituted values'() {
		given:
			def x = mockExchange("simple","post", false)
		when:
			webConnectionService.preProcess(x)
		then:
			1* x.out.setBody({ bodyContent ->
				bodyContent.contains("message=simple") && 
				bodyContent.contains("username=bob") && 
				bodyContent.contains("password=secret")
			})
	}
	//Post-Processor Tests
	def 'Successful responses should do nothing exciting'() {
		given:
			def x = mockExchange("simple","post", false)
		when:
			webConnectionService.postProcess(x)
		then:
			notThrown(RuntimeException)
	}

	Exchange mockExchange(messageText,method,messageOnly){
		def webconnection =  WebConnection.findByName("Sync")
		if(method ==  'get'){
			webconnection.httpMethod = WebConnection.HttpMethod.GET
		} else {
			webconnection.httpMethod = WebConnection.HttpMethod.POST
		}
		def p1 = new RequestParameter(name:"message",value:"\${message_body}")
		webconnection.addToRequestParameters(p1)
		if(!messageOnly){
			def p2 = new RequestParameter(name:"password",value:"secret")
			def p3 = new RequestParameter(name:"username",value:"bob")
			webconnection.addToRequestParameters(p2)
			webconnection.addToRequestParameters(p3)
		}
		webconnection.save(failOnError:true, flush:true)
		def message = Fmessage.build(text:messageText)
		Exchange exchange = Mock(Exchange)
		exchange.in >> mockExchangeMessage(['frontlinesms.fmessageId':message.id,'frontlinesms.webConnectionId':webconnection.id], null)
		exchange.out >> mockExchangeMessage([:], null)
		exchange.unitOfWork >> Mock(UnitOfWork)
		return exchange
		/*
		def inMessage = mockIncomingMessage(['frontlinesms.fmessageId':message.id,'frontlinesms.webConnectionId':webconnection.id], null)
		def xIn = [headers:['frontlinesms.fmessageId':message.id,'frontlinesms.webConnectionId':webconnection.id], body:null] as Message
		def xOut = [headers:[:], body:null] as Message
		def x = [getIn:{xIn}, getOut:{xOut}, unitOfWork:Mock(UnitOfWork)]
		x = x as Exchange
		//def unitOfWork = Mock(UnitOfWork)
		//x.unitOfWork >> unitOfWork
		println "mockExchange() : x = $x"
		return x
		*/
	}

	private String urlEncode(String s) throws UnsupportedEncodingException {
		println "PreProcessor.urlEncode : s=$s -> ${URLEncoder.encode(s, "UTF-8")}"
		return URLEncoder.encode(s, "UTF-8");
	}

	def mockExchangeMessage(headers, body) {
		def m = Mock(Message)
		m.body >> body
		m.headers >> headers
		return m
	}
}