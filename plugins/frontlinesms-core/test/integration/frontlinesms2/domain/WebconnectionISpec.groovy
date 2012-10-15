package frontlinesms2.domain

import frontlinesms2.*
import spock.lang.*

class WebconnectionISpec extends grails.plugin.spock.IntegrationSpec {
	def webCService = Mock(WebconnectionService)

	def 'incoming message matching keyword should trigger http message sending'() {
		given:
			def k = new Keyword(value:'FORWARD')
			def webconnection = new GenericWebconnection(name:"Sync", keyword:k, url:"www.frontlinesms.com/sync",httpMethod:Webconnection.HttpMethod.GET).save(failOnError:true)
			webconnection.webconnectionService = webCService
			webconnection.save(failOnError:true)
			def incomingMessage = Fmessage.build(text:"FORWARD ME", src:'123123')
		when:
			webconnection.processKeyword(incomingMessage, true)
		then:
			1 * webCService.send(incomingMessage)
	}

	def 'incoming message should match if keyword is blank and exactmatch == false'() {
		given:
			def k = new Keyword(value:'')
			def webconnection = new GenericWebconnection(name:"Sync", keyword:k, url:"www.frontlinesms.com/sync",httpMethod:Webconnection.HttpMethod.GET).save(failOnError:true)
			webconnection.webconnectionService = webCService
			webconnection.save(failOnError:true)
			def incomingMessage = Fmessage.build(text:"FORWARD ME", src:'123123')
			webconnection.addToMessages(incomingMessage).save(failOnError:true)
		when:
			webconnection.processKeyword(incomingMessage, false)
		then:
			1 * webCService.send(incomingMessage)
	}
}
