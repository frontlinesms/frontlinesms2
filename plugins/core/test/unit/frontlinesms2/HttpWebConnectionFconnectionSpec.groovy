package frontlinesms2

import grails.plugin.mixin.*
import spock.lang.*

@TestFor(HttpWebConnectionFconnection)
class HttpWebConnectionFconnectionSpec extends Specification {
	@Unroll
	def 'test constraints and url validation'() {
		when:
			def keyword = new Keyword(value:"TEST")
			def connection =  new HttpWebConnectionFconnection(name:name, url:url, httpMethod:method)
			def extComm = new WebConnection(name:name, keyword:keyword, connection: connection)

		then:
			connection.validate() == valid
		where:
			name 	|url 							|method |valid
			'test'	|'..'							|HttpWebConnectionFconnection.HttpMethod.GET 	|false
			'test'	|''								|HttpWebConnectionFconnection.HttpMethod.GET 	|false
			'test'	|'http://.com'					|HttpWebConnectionFconnection.HttpMethod.GET 	|false
			'test'	|'http://.com/sync'				|HttpWebConnectionFconnection.HttpMethod.GET 	|false
			'test'	|'http://www.frontlinesms.com'	|HttpWebConnectionFconnection.HttpMethod.GET 	|true
			'test'	|'http://frontlinesms.com'		|HttpWebConnectionFconnection.HttpMethod.GET 	|true
			'test'	|'www.frontlinesms.com/sync'	|HttpWebConnectionFconnection.HttpMethod.GET 	|true
			'test'	|'frontlinesms.com/sync'		|HttpWebConnectionFconnection.HttpMethod.GET 	|true
			'test'	|'frontlinesms.com:8080'		|HttpWebConnectionFconnection.HttpMethod.GET 	|true
			'test'	|'frontlinesms.com:8080/sync'	|HttpWebConnectionFconnection.HttpMethod.GET 	|true
			'test'	|'192.168.0.200:8080'			|HttpWebConnectionFconnection.HttpMethod.GET 	|true
			''		|'http://frontlinesms.com'		|HttpWebConnectionFconnection.HttpMethod.GET 	|false
			null	|'http://frontlinesms.com'		|HttpWebConnectionFconnection.HttpMethod.GET 	|false
			'test'	|'http://frontlinesms.com'		|HttpWebConnectionFconnection.HttpMethod.POST 	|true
	}
}
