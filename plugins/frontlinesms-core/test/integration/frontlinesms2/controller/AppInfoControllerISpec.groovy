package frontlinesms2.controller

import frontlinesms2.*
import spock.lang.*

import grails.converters.JSON

class AppInfoControllerISpec extends grails.plugin.spock.IntegrationSpec {
	private static final String JSON_MIME_TYPE = 'application/json'

	def controller
	def service

	def setup() {
		controller = new AppInfoController()
		service = Mock(AppInfoService)
		controller.appInfoService = service
	}

	def 'appInfoService should be called with request for relevant data (0 requested items)'() {
		given:
			def requestData = []
			controller.request.format = JSON_MIME_TYPE
			controller.request.JSON = (requestData as JSON).toString()
		when:
			def response = controller.index()
			println response
		then:
			0 * _
			JSON.parse(controller.response.contentAsString).keySet().sort() == requestData
	}

	def 'appInfoService should be called with request for relevant data (1 requested item)'() {
		given:
			def requestData = ['devices']
			controller.request.format = JSON_MIME_TYPE
			controller.request.JSON = (requestData as JSON).toString()
		when:
			def response = controller.index()
			println response
		then:
			1 * service.provide('devices', controller)
			0 * _
			JSON.parse(controller.response.contentAsString).keySet().sort() == requestData
	}

	def 'appInfoService should be called with request for relevant data (2 requested items)'() {
		given:
			def requestData = ['devices', 'gribbles']
			controller.request.format = JSON_MIME_TYPE
			controller.request.JSON = (requestData as JSON).toString()
		when:
			def response = controller.index()
			println response
		then:
			1 * service.provide('devices', controller)
			1 * service.provide('gribbles', controller)
			0 * _
			JSON.parse(controller.response.contentAsString).keySet().sort() == requestData
	}
}

