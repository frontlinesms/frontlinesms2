package frontlinesms2.controllers

import frontlinesms2.*

import spock.lang.*

@TestFor(ApiController)
class ApiControllerSpec extends Specification {
	def setup() {
		def grailsApplication = [domainClasses:[[clazz:Webconnection]]]
		controller.grailsApplication = grailsApplication
		controller.apiService = [invokeApiProcess : { entity, controller -> return [ status: 200 ]}]

		Webconnection.metaClass.static.findById = { id -> Mock(GenericWebconnection) }
		Webconnection.metaClass.static.findAllByNameIlike = { name -> Mock(GenericWebconnection) }
	}

	def 'bad URL should return a 404 error status'() {
		when:
			controller.index()
		then:
			response.status == 404
	}

	def 'good request should return 200 status'() {
		given:
			params.entityClassApiUrl = 'webconnection'
			params.entityId = 1
		when:
			controller.index()
		then:
			response.status == 200
	}
}

