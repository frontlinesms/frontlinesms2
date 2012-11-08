import spock.lang.*
import frontlinesms2.*
import frontlinesms2.api.*

@Mock(frontlinesms2.GenericWebconnection)

class FrontlineApi1UtilsSpec extends Specification {
	
	def "Generate url for generic web connection with enabled api should produce url without secret"() {
		setup:
			def webCon = new GenericWebconnection(apiEnabled:true)
			webCon.id = 123
			webCon.apiEnabled = true
		when:
			def url = FrontlineApi1Utils.generateUrl(webCon)
		then:
			url == "/api/1/webconnection/123/"
	}

	def "Generate url for generic web connection with disabled api should produce empty string"() {
		setup:
			def webCon = new GenericWebconnection()
			webCon.id = 123
			webCon.apiEnabled = false
		when:
			def url = FrontlineApi1Utils.generateUrl(webCon)
		then:
			url == ""
	}
}

