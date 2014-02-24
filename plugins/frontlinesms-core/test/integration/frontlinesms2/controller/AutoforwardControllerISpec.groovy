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

	def 'moving a message into an autoforward that does not have contacts or groups should just fail the outgoing messages'() {
		setup:
			def autoforward = new Autoforward(name: "test", sentMessageText: "Someone said something").save(failOnError:true)
			autoforward.addToContacts(Contact.build(mobile:'123123'))
			autoforward.save(failOnError:true)

			def message = TextMessage.build(text:'This should be moved to Autoforward')
			def controller = new MessageController()
			controller.params.interactionId = message.id
			controller.params.ownerId = autoforward.id
			controller.params.messageSection = 'activity'
		when:
			controller.move()
		then:
			TextMessage.findByText("This should be moved to Autoforward").messageOwner.id == autoforward.id
	}
}