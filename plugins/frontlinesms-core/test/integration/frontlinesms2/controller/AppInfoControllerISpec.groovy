package frontlinesms2.controller

import frontlinesms2.*
import grails.converters.JSON
import spock.lang.*

class AppInfoControllerISpec extends grails.plugin.spock.IntegrationSpec {
	def controller
	def mockApp
	def service

	def setup() {
		controller = new AppInfoController()
		service = Mock(AppInfoService)
		controller.appInfoService = service
	}

	def 'appInfoService should be called with request for relevant data (0 requested items)'() {
		given:
			def requestData = []
			controller.request.JSON = requestData
		when:
			controller.index()
		then:
			0 * _
			JSON.parse(controller.response.contentAsString).keySet().sort() == requestData
	}

	def 'appInfoService should be called with request for relevant data (1 requested item)'() {
		given:
			def requestData = [devices:null]
			controller.request.JSON = requestData
		when:
			controller.index()
		then:
			1 * service.provide(controller, 'devices', _)
			0 * _
			JSON.parse(controller.response.contentAsString).keySet().sort() == requestData.keySet().sort()
	}

	def 'appInfoService should be called with request for relevant data (2 requested items)'() {
		given:
			def requestData = [devices:null, gribbles:null]
			controller.request.JSON = requestData
		when:
			controller.index()
		then:
			1 * service.provide(controller, 'devices', _)
			1 * service.provide(controller, 'gribbles', _)
			0 * _
			JSON.parse(controller.response.contentAsString).keySet().sort() == requestData.keySet().sort()
	}
}

