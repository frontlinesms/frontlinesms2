package frontlinesms2.domain

import frontlinesms2.*
import spock.lang.*
import org.apache.camel.*

class WebconnectionISpec extends grails.plugin.spock.IntegrationSpec {
	def webCService = Mock(WebconnectionService)

	def 'incoming message matching keyword should trigger http message sending'() {
		given:
			def k = new Keyword(value:'FORWARD')
			def webconnection = new GenericWebconnection(name:"Sync", url:"www.frontlinesms.com/sync",httpMethod:Webconnection.HttpMethod.GET).addToKeywords(k).save(failOnError:true)
			webconnection.webconnectionService = webCService
			webconnection.save(failOnError:true)
			def incomingMessage = Fmessage.build(text:"FORWARD ME", src:'123123')
		when:
			webconnection.processKeyword(incomingMessage, k)
		then:
			1 * webCService.send(incomingMessage)
	}

	def 'incoming message should match if keyword is blank and exactmatch == false'() {
		given:
			def k = new Keyword(value:'')
			def webconnection = new GenericWebconnection(name:"Sync", url:"www.frontlinesms.com/sync",httpMethod:Webconnection.HttpMethod.GET).addToKeywords(k).save(failOnError:true)
			webconnection.webconnectionService = webCService
			webconnection.save(failOnError:true)
			def incomingMessage = Fmessage.build(text:"FORWARD ME", src:'123123')
			webconnection.addToMessages(incomingMessage).save(failOnError:true)
		when:
			webconnection.processKeyword(incomingMessage, k)
		then:
			1 * webCService.send(incomingMessage)
	}


	def 'testRoute should set message ownerDetail to failed when it fails'() {
		given:
			def camelContext = Mock(CamelContext)
			def webconnection = new GenericWebconnection(name:"Sync", url:"www.frontlinesms.com/sync",httpMethod:Webconnection.HttpMethod.GET).save(failOnError:true)
			webconnection.camelContext = camelContext
			webconnection.save(failOnError:true)
		when:
			webconnection.testRoute()
		then:
			Fmessage.findByMessageOwnerAndText(webconnection, WebconnectionService.TEST_MESSAGE_TEXT).ownerDetail == "failed"
	}

	// @IgnoreRest
	// def "test route should work"() {
	// 	when:
	// 		def conn = Webconnection.list()[0] ?: new GenericWebconnection(name:"Sync", url:"http://localhost:8080/webservice-debugger",httpMethod:Webconnection.HttpMethod.GET).save(failOnError:true)
	// 		conn.testRoute()
	// 	then:
	// 		sleep 10000
	// 		Fmessage.findByMessageOwnerAndText(conn, WebconnectionService.TEST_MESSAGE_TEXT).ownerDetail == "failed"
	// }
}
