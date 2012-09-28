package frontlinesms2.domain

import frontlinesms2.*

class RequestParameterISpec extends grails.plugin.spock.IntegrationSpec {
	@spock.lang.Unroll
	def "parameter in a message should be replaced with actual values"(){
		when:
			def message = Fmessage.build(text:"Sample text to send out", src:'12345')
			def syncKeyword = new Keyword(value:"SYNC")
			def parameter = new RequestParameter(name:'message', value:"\${}")
			def webconnection = new GenericWebconnection(name:"Sync", keyword:syncKeyword, url:"http://www.frontlinesms.com/sync", httpMethod:Webconnection.HttpMethod.GET)
			webconnection.addToRequestParameters(parameter)
			webconnection.save(failOnError:true)
			
			def p = RequestParameter.findByName("message")
			p.value = "\${"+paramText+"}"
			p.save(failOnError:true)
		then:
			returnval == parameter.getProcessedValue(message)
		where:
			returnval					|paramText
			'Sample text to send out'	|"message_body"
			'12345'						|"message_src_number"
			'12345'						|"message_src_name"
	}
}