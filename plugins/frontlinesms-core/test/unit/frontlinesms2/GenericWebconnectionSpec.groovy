package frontlinesms2

import grails.test.mixin.*
import spock.lang.*
import frontlinesms2.*
import frontlinesms2.camel.*
import org.apache.camel.Exchange
import org.apache.camel.Message

@TestFor(GenericWebconnection)
@Mock([Keyword])
class GenericWebconnectionSpec extends CamelUnitSpecification {
	private static final String TEST_NUMBER = "+2345678"
	def setup() {
		Webconnection.metaClass.static.findAllByNameIlike = { name -> GenericWebconnection.findAll().findAll { it.name == name } }
	}

	@Unroll
	def "Test constraints"() {
		when:
			def keyword = new Keyword(value:'TEST')
			def extComm = new WebConnection(name:name, url:"www.frontlinesms.com/sync",httpMethod:WebConnection.HttpMethod.GET)
			if (addKeyword)
				extComm.addToKeywords(keyword)
		then:
			extComm.validate() == valid
		where:
			name  | addKeyword |valid
			'test'| true       |true
			'test'| false      |true
			''    | true       |false
			null  | true       |false
	}
}

