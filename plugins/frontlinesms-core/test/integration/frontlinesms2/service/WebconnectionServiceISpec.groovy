package frontlinesms2.service

import frontlinesms2.*
import spock.lang.*
import org.apache.camel.*
import org.apache.camel.Message
import org.apache.camel.spi.UnitOfWork
import org.apache.camel.spi.RouteContext
import org.apache.camel.model.RouteDefinition

class WebconnectionServiceISpec extends grails.plugin.spock.IntegrationSpec{
	def webconnectionService
	def setup() {
		def k = new Keyword(value:'FOREWARD')
		def webconnection = new GenericWebconnection(name:"Sync", url:"http://www.frontlinesms.com/sync",httpMethod:Webconnection.HttpMethod.GET).addToKeywords(k).save(failOnError:true)
	}
	//Pre-Processor Tests
	def 'out_header url should contain the RequestParameters for GET request'() {
 		given:
			def x = mockExchange("simple","get", false)
		when:
			webconnectionService.preProcess(x)
			println "**** " + x.out.headers.url
		then:
			x.in.headers[Exchange.HTTP_QUERY].contains("username=bob")
			x.in.headers[Exchange.HTTP_QUERY].contains("password=secret")
	}

	def 'out_body should contains the substituted RequestParameters for POST request'(){
		given:
			def x = mockExchange("test message","post", true)
		when:
			webconnectionService.preProcess(x)
		then:
			1 * x.in.setBody({ bodyContent ->
				bodyContent.contains("message=test+message")
			})
	}

	def 'URL is not modified when request is POST'(){
		given:
			def x = mockExchange("test message","post", true)
		when:
			webconnectionService.preProcess(x)
		then:
		!x.out.headers[Exchange.HTTP_QUERY]
	}

	def 'exchange body should contains all values substituted values'() {
		given:
			def x = mockExchange("simple","post", false)
		when:
			webconnectionService.preProcess(x)
		then:
			1* x.in.setBody({ bodyContent ->
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
			webconnectionService.postProcess(x)
		then:
			notThrown(RuntimeException)
	}

	def 'webconnectionservice.postProcess() should change ownerDetail to onwerdetail-completed'() {
		given:
			def x = mockExchange("simple","post", false)
		when:
			webconnectionService.postProcess(x)
		then:
			Fmessage.findByText("simple").ownerDetail == "success"
	}

	def 'webconnectionservice.send() should change ownerDetail to onwerdetail-pending'() {
		given:
			def webconnection =  Webconnection.findByName("Sync")
			webconnection.addToMessages(Fmessage.build(text:"simple"))
			webconnection.save(flush:true)
		when:
			webconnectionService.doUpload(Fmessage.findByText("simple"))
		then:
			Fmessage.findByText("simple").ownerDetail == "pending"
	}

	def 'webconnectionService.handleException() should change ownerDetail to onwerdetail-failed'(){
		given:
			def x = mockExchange("simple","post", false)
		when:
			webconnectionService.handleException(x)
		then:
			Fmessage.findByText("simple").ownerDetail == "failed"
	}

	def 'webconnectionservice.getWebconnectionStatus should return NOT_CONNECTED when the webconnection is not active'() {
		given:
			def webconnection =  Webconnection.findByName("Sync")
		expect:
			webconnectionService.getStatusOf(webconnection) == ConnectionStatus.NOT_CONNECTED
	}

	def 'webconnectionservice.getWebconnectionStatus should return CONNECTED when the webconnection is active'() {
		setup:
			def camelContext = Mock(CamelContext)
			def webconnection =  Webconnection.findByName("Sync")
			webconnection.camelContext = camelContext
			webconnectionService.metaClass.getStatusOf = {Webconnection w -> ConnectionStatus.CONNECTED}
			webconnection.save(failOnError:true)
		when:
			webconnection.activate()
		then:
			webconnectionService.getStatusOf(webconnection) == ConnectionStatus.CONNECTED
	}

	def 'testRoute should set message ownerDetail to failed when it fails'() {
		setup:
			def camelContext = Mock(CamelContext)
			def webconnection =  Webconnection.findByName("Sync")
			webconnection.camelContext = camelContext
			webconnection.save(failOnError:true)
		when:
			webconnectionService.testRoute(webconnection)
		then:
			Fmessage.findByMessageOwnerAndText(webconnection, Fmessage.TEST_MESSAGE_TEXT).ownerDetail
	}

	Exchange mockExchange(messageText,method,messageOnly){
		def webconnection =  Webconnection.findByName("Sync")
		if(method ==  'get'){
			webconnection.httpMethod = Webconnection.HttpMethod.GET
		} else {
			webconnection.httpMethod = Webconnection.HttpMethod.POST
		}
		def p1 = new RequestParameter(name:"message",value:"\${message_body}")
		webconnection.addToRequestParameters(p1)
		if(!messageOnly){
			def p2 = new RequestParameter(name:"password",value:"secret")
			def p3 = new RequestParameter(name:"username",value:"bob")
			webconnection.addToRequestParameters(p2)
			webconnection.addToRequestParameters(p3)
		}
		def message = Fmessage.build(text:messageText)
		webconnection.addToMessages(message)
		webconnection.save(failOnError:true, flush:true)
		Exchange exchange = Mock(Exchange)
		exchange.in >> mockExchangeMessage(['fmessage-id':message.id,'webconnection-id':webconnection.id], null)
		exchange.out >> mockExchangeMessage([:], null)
		exchange.unitOfWork >> Mock(UnitOfWork)
		return exchange
		/*
		def inMessage = mockIncomingMessage(['frontlinesms.fmessageId':message.id,'frontlinesms.webconnectionId':webconnection.id], null)
		def xIn = [headers:['frontlinesms.fmessageId':message.id,'frontlinesms.webconnectionId':webconnection.id], body:null] as Message
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
