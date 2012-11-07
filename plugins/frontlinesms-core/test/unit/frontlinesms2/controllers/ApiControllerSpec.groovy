package frontlinesms2.controllers

import frontlinesms2.*

import spock.lang.*
import grails.buildtestdata.mixin.Build

@TestFor(ApiController)
@Build([Webconnection])
class ApiControllerSpec extends Specification {
	def 'bad URL should return a 404 error status'() {
		given:
			params = [:]
		when:
			controller.index()
		then:
			response.status == 404
	}

	def 'bad secret should return a 401 error status'() {
		given:
			def id = Webconnection.build(secret:'skyfall').id
			params = [entityClassApiUrl:'webconnection', secret:'toffee', entityId:id]
		when:
			controller.index()
		then:
			response.status == 401
	}

	def 'good request should return 200 status'() {
		given:
			def id = Webconnection.build(secret:'skyfall').id
			params = [entityClassApiUrl:'webconnection', secret:'skyfall', entityId:id]
		when:
			controller.index()
		then:
			response.status == 200
	}
}

