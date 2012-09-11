package frontlinesms2

import grails.test.mixin.*
import spock.lang.*
import frontlinesms2.*
import frontlinesms2.camel.*
import org.apache.camel.Exchange
import org.apache.camel.Message

@TestFor(WebConnection)
@Mock([Keyword])
class WebConnectionSpec extends CamelUnitSpecification {
	private static final String TEST_NUMBER = "+2345678"
	def setup() {
	}

	@Unroll
	def "Test constraints"() {
		when:
			def keyword = addKeyword? new Keyword(value:'TEST'): null
			def extComm = new WebConnection(name:name, keyword:keyword, url:"www.frontlinesms.com/sync",httpMethod:WebConnection.HttpMethod.GET)
		then:
			extComm.validate() == valid
		where:
			name	|addKeyword	|valid
			'test'	|true		|true
			'test'	|false		|true
			''		|true		|false
			null	|true		|false
	}
}