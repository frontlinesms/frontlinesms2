package frontlinesms2.domain

import frontlinesms2.*
import spock.lang.*

class WebConnectionISpec extends grails.plugin.spock.IntegrationSpec {
	def webCService = Mock(WebConnectionService)

	def 'incoming message matching keyword should trigger http message sending'() {
		given:
			def k = new Keyword(value:'FORWARD')
			def webConnection = new GenericWebConnection(name:"Sync", keyword:k, url:"www.frontlinesms.com/sync",httpMethod:WebConnection.HttpMethod.GET).save(failOnError:true)
			webConnection.webConnectionService = webCService
			webConnection.save(failOnError:true)
			def incomingMessage = Fmessage.build(text:"FORWARD ME", src:'123123')
		when:
			webConnection.processKeyword(incomingMessage, true)
		then:
			1 * webCService.send(incomingMessage)
	}

	def 'incoming message should match if keyword is blank and exactmatch == false'() {
		given:
			def k = new Keyword(value:'')
			def webConnection = new GenericWebConnection(name:"Sync", keyword:k, url:"www.frontlinesms.com/sync",httpMethod:WebConnection.HttpMethod.GET).save(failOnError:true)
			webConnection.webConnectionService = webCService
			webConnection.save(failOnError:true)
			def incomingMessage = Fmessage.build(text:"FORWARD ME", src:'123123')
			webConnection.addToMessages(incomingMessage).save(failOnError:true)
		when:
			webConnection.processKeyword(incomingMessage, false)
		then:
			1 * webCService.send(incomingMessage)
	}
}
