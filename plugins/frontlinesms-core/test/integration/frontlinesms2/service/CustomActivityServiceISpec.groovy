package frontlinesms2.service

import frontlinesms2.*
import spock.lang.*

class CustomActivityServiceISpec extends grails.plugin.spock.IntegrationSpec{
	def customActivityService, customActivity, join, leave, reply, group, contact, message

	def setup() {
		customActivityService = new CustomActivityService()
		contact = new Contact(name:"Wamaitha", mobile:"+23888")
		contact.save(failOnError:true)
		message = new TextMessage(src:contact.mobile, text:"CUSTOM TEST", inbound:true).save(failOnError:true)
		createCustomActivity()
	}

	def 'customActivityService.triggerSteps() should perform relevant action for join and reply'(){
		given:
			createTestGroup()
			addJoinActionStep()
			addReplyActionStep()
		when:
			customActivityService.triggerSteps(customActivity, message)
		then:
			contact.groups == [group]
			TextMessage.findAllByText("autoreply seems to work").size() == 1
	}

	def 'customActivityService.triggerSteps() should perform relevant action for leave and reply'(){
		given:
			createTestGroup(contact)
			addLeaveActionStep()
			addReplyActionStep()
			customActivity.steps.each {
				println "### steps ${it.shortName}"
			}
		when:
			customActivityService.triggerSteps(customActivity, message)
		then:
			contact.groups == []
			TextMessage.findAllByText("autoreply seems to work").size() == 1
	}

	private def createCustomActivity() {
		customActivity = new CustomActivity(name:"Custom")
		customActivity.addToKeywords(new Keyword(isTopLevel: true, value: "CUSTOM"))
		customActivity.addToMessages(message)
		customActivity.save(failOnError:true, flush:true)
	}

	private def createTestGroup(contact) {
		group = new Group(name:"testGroup")
		group.save(failOnError:true)
		if (contact) {
			contact.addToGroups(group)
			contact.save(failOnError:true)
		}
	}

	private def addJoinActionStep() {
		join = new JoinActionStep()
		join.addToStepProperties(new StepProperty(key:"group", value:group.id))
		customActivity.addToSteps(join)
		customActivity.save(failOnError:true)
		join.save(failOnError:true)
	}

	private def addLeaveActionStep() {
		leave = new LeaveActionStep()
		leave.addToStepProperties(new StepProperty(key:"group", value:group.id))
		customActivity.addToSteps(leave)
		customActivity.save(failOnError:true)
		leave.save(failOnError:true)
	}

	private def addReplyActionStep() {
		reply = new ReplyActionStep()
		reply.addToStepProperties(new StepProperty(key:"autoreplyText", value:"autoreply seems to work"))
		customActivity.addToSteps(reply)
		customActivity.save(failOnError:true)
		reply.save(failOnError:true)
	}
}