package frontlinesms2.domain

import frontlinesms2.*

class RequestParameterISpec extends grails.plugin.spock.IntegrationSpec {
	@spock.lang.Unroll
	def "parameter in a message should be replaced with actual values"(){
		when:
			def message = Fmessage.build(text:"Sample text to send out", src:'12345')
			def message_body = new RequestParameter(name:'message', value:'${message_body}')
			def src_number = new RequestParameter(name:'message', value:'${message_src_number}')
			def src_name = new RequestParameter(name:'message', value:'${message_src_name}')
			def syncKeyword = new Keyword(value:"SYNC")
			def webconnection = new WebConnection(name:"Sync", keyword:syncKeyword, url:"http://www.frontlinesms.com/sync", httpMethod:WebConnection.HttpMethod.GET)
			webconnection.requestParameters << message_body << src_number << src_name
			webconnection.save(failOnError:true)
		then:
			returnval == paramter.getProcessedValue(message)
		where:
			returnval					|parameter
			'Sample text to send out'	|message_body
			'12345'						|src_number
			'12345'						|src_name
	}
}