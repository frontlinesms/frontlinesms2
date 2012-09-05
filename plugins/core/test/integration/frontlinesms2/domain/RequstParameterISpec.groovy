package frontlinesms2.domain

import frontlinesms2.*

class RequestParameterISpec extends grails.plugin.spock.IntegrationSpec {
	def "parameter in a message should be replaced with actual values"(){
		setup:
			def message = Fmessage.build(text:"Sample text to send out")
			def requestParameter = new RequestParameter(name:'message', value:'${message_content}')
			def syncKeyword = new Keyword(value:"SYNC")
			def webconnection = new WebConnection(name:"Sync", keyword:syncKeyword, url:"http://www.frontlinesms.com/sync", httpMethod:WebConnection.HttpMethod.GET)
			webconnection.addToRequestParameters(requestParameter)
			webconnection.save(failOnError:true)
		when:
			def returnval = requestParameter.getProcessedValue(message)
		then:
			returnval == "Sample text to send out"
	}
}