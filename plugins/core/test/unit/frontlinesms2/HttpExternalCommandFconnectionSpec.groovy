package frontlinesms2

import grails.plugin.mixin.*
import spock.lang.*

@TestFor(HttpExternalCommandFconnection)
class HttpExternalCommandFconnectionSpec extends Specification {
	def 'test constraints'() {
		when:
			def keyword = addKeyword? new Keyword(value:'TEST'): null
			def connection =  addConnection? new HttpExternalCommandFconnection(name:'Testing', url:"www.frontlinesms.com/sync",httpMethod:HttpExternalCommandFconnection.HttpMethod.GET): null
			def extComm = new ExternalCommand(name:name, keyword:keyword, connection: connection)
		then:
			extComm.validate() == valid
		where:
			name|addKeyword|addConnection|valid
			'test'|true|true|true
			'test'|false|true|true
			''|true|true|false
			null|true|true|false
			'test'|true|false|true
	}
}
