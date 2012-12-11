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
			def extComm = new GenericWebconnection(name:name, url:"http://www.frontlinesms.com/sync",httpMethod:Webconnection.HttpMethod.GET)
		then:
			extComm.validate() == valid
		where:
			name   | valid
			'test' | true
			'test' | true
			''     | false
			null   | false
	}

	def "Test URL constraints"() {
		when:
			def extComm = new GenericWebconnection(name:"URL",url:url,httpMethod:Webconnection.HttpMethod.GET)
		then:
			extComm.validate() == valid
		where:
			url                                                   | valid
			'http://www.cuug.com/branderr/csce.html'              | true
			'ftp://www.sagana.com/home/smith/budget.wk1'          | true
			'https://www.apple.com/index.html'                    | true
			'http://127.0.0.1:8080/frontlinesms-core'             | true
			'http://127.0.0.1'                                    | true
			'www.apple.com/index.html'                    	      | false
			'http://localhost:8080/frontlinesms-core'             | false
			'http//www.apple.com/index.php'                       | false
			'https://http://home/frontlinesms'                    | false
			'http://....home.com'                                 | false
			'http:/www.apple.com/index.html'                      | false
			'http:/wwww.apple.com/index.html'                     | false
			'htpp:/www.apple.com/index.html'                      | false
			'htttp:/www.apple.com/index.html'                     | false
			'htttp:/www..apple.com/index.html'                    | false
	}

	def 'apiProcess should pass call to service'() {
		given:
			WebconnectionService s = Mock()
			def c = new GenericWebconnection()
			c.webconnectionService = s
			def controller = [:]
		when:
			c.apiProcess(controller)
		then:
			1 * s.apiProcess(c, controller)
	}
}

