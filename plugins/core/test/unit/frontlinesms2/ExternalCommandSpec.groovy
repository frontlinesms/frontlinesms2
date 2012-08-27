package frontlinesms2

import grails.test.mixin.*
import spock.lang.*

@TestFor(ExternalCommand)
@Mock([Keyword])
class ExternalCommandSpec extends Specification {

	def setup() {
	}

	@Unroll
	def "Test constraints"() {
		when:
			def keyword = addKeyword? new Keyword(): null
			def extComm = new ExternalCommand(name:name, url:url, keyword:keyword, type:type)
		then:
			extComm.validate() == valid
		where:
			name     | url                                       | addKeyword | type   	             | valid
			'test'   | 'http://192.168.0.200:8080/test'          | true       | ExternalCommand.GET  | true
			'test'   | 'http://192.168.0.200:8080/test'          | true       | ExternalCommand.POST | true
			null     | 'http://192.168.0.200:8080/test'          | true       | ExternalCommand.POST | false
			'test'   | null                                      | true       | ExternalCommand.POST | false
			'test'   | 'http://192.168.0.200:8080/test'          | false      | ExternalCommand.POST | false
			'test'   | 'http://192.168.0.200:8080/test'          | true       | "not a valid value!" | false
	}

}