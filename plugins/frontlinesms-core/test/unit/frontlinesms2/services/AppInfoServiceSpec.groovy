package frontlinesms2.services

import frontlinesms2.*
import spock.lang.*

@TestFor(AppInfoService)
class AppInfoServiceSpec extends Specification {
	def mockController

	def setup() {
		mockController = Mock(AppInfoController)
	}

	def 'provide should throw exception if no provider registered'() {
		when:
			service.provide mockController, 'some key', null
		then:
			thrown(RuntimeException)
	}

	def 'registerProvider should throw exception if provider already registered'() {
		given:
			service.registerProvider 'a', newPointlessProvider()
		when:
			service.registerProvider 'a', newPointlessProvider()
		then:
			thrown(RuntimeException)
	}

	def 'provide should call previously-registered provider'() {
		given:
			def a = Mock(Closure)
			def b = Mock(Closure)
			service.registerProvider 'a', a
			service.registerProvider 'b', b
		when:
			service.provide mockController, 'a', null
		then:
			1 * a.call(service.grailsApplication, mockController, null)
			0 * _
		when:
			service.provide mockController, 'b', null
		then:
			1 * b.call(service.grailsApplication, mockController, null)
			0 * _
	}

	private def newPointlessProvider() {
		{ controller -> }
	}
}

