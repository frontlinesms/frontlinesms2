package frontlinesms2.controller

import spock.lang.*
import frontlinesms2.*
import grails.plugin.spock.*

class QuickMessageControllerISpec extends IntegrationSpec {
	def controller
	
	def setup() {
		controller = new QuickMessageController()
	}
	
	//TODO Add test for prepopulating the wizard with recipients and groups
}
