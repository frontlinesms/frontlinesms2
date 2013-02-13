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
			service.provide 'some key'
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
			service.provide('a', mockController)
		then:
			1 * a.call(mockController)
			0 * _
		when:
			service.provide('b', mockController)
		then:
			1 * b.call(mockController)
			0 * _
	}

	private def newPointlessProvider() {
		{ controller -> }
	}
}

