package frontlinesms2.controller

import frontlinesms2.*

import spock.lang.*

class AutoforwardControllerISpec extends grails.plugin.spock.IntegrationSpec {
	def controller
	def i18nUtilService

	def setup() {
		controller = new AutoforwardController()
	}

	def 'Saving a autoforward should persist it in the database'(){
		setup:
			controller.params.messageText = "Forward this message"
			controller.params.name = "Forward"
			controller.params.addresses = ['123123','6867896789']
			controller.params.keywords = 'try,again'
		when:
			controller.save()
		then:
			Autoforward.findByName('Forward')
			controller.flash.message == i18nUtilService.getMessage([code:"autoforward.save.success", args:[Autoforward.findByName('Forward').name]])
	}

	def 'Editing a autoforward should persist changes'(){
		setup:
			def contact = new Contact(name:"Contact", mobile:"+123321").save()
			def autoforward = new Autoforward(name: "test", sentMessageText: "Someone said something")
				.addToContacts(contact)
				.save(failOnError:true)
			controller.params.messageText = "Forward this message"
			controller.params.name = "Forward"
			controller.params.addresses = ['123123','6867896789']
			controller.params.keywords = 'try,again'
			controller.params.ownerId = autoforward.id
		when:
			controller.save()
		then:
			Autoforward.count() == 1
			Autoforward.findByName('Forward')
			controller.flash.message == i18nUtilService.getMessage([code:"autoforward.save.success", args:[Autoforward.findByName('Forward').name]])
	}
}