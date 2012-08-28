package frontlinesms2

import grails.plugin.mixin.*
import spock.lang.*

@TestFor(HttpWebConnectionFconnection)
class HttpWebConnectionFconnectionSpec extends Specification {
	def 'test constraints'() {
		when:
			def keyword = addKeyword? new Keyword(value:'TEST'): null
			def connection =  addConnection? new HttpWebConnectionFconnection(name:'Testing', url:"www.frontlinesms.com/sync",httpMethod:HttpWebConnectionFconnection.HttpMethod.GET): null
			def extComm = new WebConnection(name:name, keyword:keyword, connection: connection)
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
