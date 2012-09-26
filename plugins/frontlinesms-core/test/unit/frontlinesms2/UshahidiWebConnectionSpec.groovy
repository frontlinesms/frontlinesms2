package frontlinesms2

import grails.test.mixin.*
import spock.lang.*
import frontlinesms2.*
import frontlinesms2.camel.*
import org.apache.camel.Exchange
import org.apache.camel.Message

@TestFor(UshahidiWebConnection)
@Mock([Keyword])
class UshahidiWebConnectionSpec extends CamelUnitSpecification {
	private static final String TEST_NUMBER = "+2345678"
	def setup() {
		WebConnection.metaClass.static.findAllByNameIlike = { name -> UshahidiWebConnection.findAll().findAll { it.name == name } }
	}

	@Unroll
	def "Test constraints"() {
		when:
			def keyword = addKeyword? new Keyword(value:'TEST'): null
			def connection = new UshahidiWebConnection(name:name, keyword:keyword, url:"www.ushahidi.com/frontlinesms2", httpMethod:method)
		then:
			println connection.errors
			connection.validate() == valid
		where:
			name	|addKeyword	|valid | method
			'test'	|true		|true  | WebConnection.HttpMethod.POST
			'test'	|false		|false | WebConnection.HttpMethod.POST
			''		|true		|false | WebConnection.HttpMethod.POST
			null	|true		|false | WebConnection.HttpMethod.POST
	}
	
}