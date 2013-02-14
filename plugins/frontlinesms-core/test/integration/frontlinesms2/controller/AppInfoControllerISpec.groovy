package frontlinesms2.controller

import frontlinesms2.*
import grails.converters.JSON
import org.codehaus.groovy.grails.commons.DefaultGrailsApplication
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
		when:
			def response = controller.index()
		then:
			0 * _
			JSON.parse(controller.response.contentAsString).keySet().sort() == requestData
	}

	def 'appInfoService should be called with request for relevant data (1 requested item)'() {
		given:
			def requestData = ['devices']
			controller.params['interest[]'] = 'devices'
		when:
			def response = controller.index()
		then:
			1 * service.provide('devices', controller)
			0 * _
			JSON.parse(controller.response.contentAsString).keySet().sort() == requestData
	}

	def 'appInfoService should be called with request for relevant data (2 requested items)'() {
		given:
			def requestData = ['devices', 'gribbles']
			controller.params['interest[]'] = requestData
		when:
			def response = controller.index()
		then:
			1 * service.provide('devices', controller)
			1 * service.provide('gribbles', controller)
			0 * _
			JSON.parse(controller.response.contentAsString).keySet().sort() == requestData
	}
}

