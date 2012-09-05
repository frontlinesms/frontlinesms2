package frontlinesms2.controller

import spock.lang.*
import grails.plugin.spock.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import frontlinesms2.*
import grails.converters.JSON

class WebConnectionControllerControllerISpec extends grails.plugin.spock.IntegrationSpec {
	def controller
	def trashService

	def setup() {
		controller = new WebConnectionControllerController()
	}

	def 'save action should also edit a webconnection'(){}
	def ''(){}
}