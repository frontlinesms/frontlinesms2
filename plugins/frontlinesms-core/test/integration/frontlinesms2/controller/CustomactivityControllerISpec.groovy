package frontlinesms2.controller

import frontlinesms2.*

class CustomactivityControllerISpec extends grails.plugin.spock.IntegrationSpec {
	def controller = new CustomactivityController()

	def "can create a new custom activity"(){
		given:
			controller.params.jsonToSubmit = """[{"stepId":"","stepType":"join", "group":"5"}, {"stepId":"","stepType":"leave", "group":"5" }]"""
			controller.params.name = "test save"
			controller.params.keywords = "test, custom"
			controller.params.sorting = "enabled"
		when:
			controller.save()
		then:
			def activity = CustomActivity.findByName("test save")
			activity.keywords*.value.containsAll(["TEST", "CUSTOM"])
			activity.steps.size() == 2
	}

	def "autoforward steps are saved correctly"(){
		given:
			def contact1 = Contact.build(mobile:"123123")
			def contact2 = Contact.build(mobile:"123124")
			def group = Group.build()
			def smartGroup = SmartGroup.build(name:'English numbers', mobile:'+44')
			controller.params.jsonToSubmit = """[{"stepId":"","stepType":"forward","sentMessageText":"testmessage","recipients":["contact-${contact1.id}","contact-${contact2.id}","address-12343123","group-${group.id}","smartgroup-${smartGroup.id}"]}]"""
			controller.params.name = "test save"
			controller.params.keywords = "test, custom"
			controller.params.sorting = "enabled"
		when:
			controller.save()
		then:
			def activity = CustomActivity.findByName("test save")
			def expectedResults = ["testmessage", "Contact-${contact1.id}", "Contact-${contact2.id}", "Address-12343123", "Group-${group.id}", "SmartGroup-${smartGroup.id}"].collect { it.toString() }
			activity.keywords*.value.containsAll(["TEST", "CUSTOM"])
			activity.steps.size() == 1
			activity.steps*.stepProperties*.flatten().value.flatten().containsAll(expectedResults)
	}
	
	def "can edit an existing custom activity"() {
		given:
			def joinStep = new JoinActionStep().addToStepProperties(new StepProperty(key:"group", value:"1"))
			def leaveStep = new LeaveActionStep().addToStepProperties(new StepProperty(key:"group", value:"2"))

			def a = new CustomActivity(name:'Do it all')
				.addToSteps(joinStep)
				.addToSteps(leaveStep)
				.addToKeywords(value:"CUSTOM")
				.save(failOnError:true, flush:true)

			controller.params.name = "just edited"
			controller.params.keywords = "new, just, yeah"
			controller.params.sorting = "enabled"
			controller.params.ownerId = a.id
			controller.params.jsonToSubmit = """[{"stepId":"","stepType":"join", "group":"5"}]""" 
		when:
			controller.save()
		then:
			def activity = CustomActivity.findByName("just edited")
			activity.keywords*.value.containsAll(["NEW", "JUST", "YEAH"])
			activity.steps.size() == 1
	}

	def "only messages that have been triggered by the step should be displayed when viewing a step"() {
		given:
			controller = new MessageController()
			def group = Group.build()
			def joinStep = new JoinActionStep().addToStepProperties(new StepProperty(key:"group", value:"${group.id}"))
			def leaveStep = new LeaveActionStep().addToStepProperties(new StepProperty(key:"group", value:"${group.id}"))
			def replyStep = new ReplyActionStep().addToStepProperties(new StepProperty(key:"autoreplyText", value:"sending this message"))

			def a = new CustomActivity(name:'Do it all')
				.addToSteps(joinStep)
				.addToSteps(leaveStep)
				.addToSteps(replyStep)
				.addToKeywords(value:"CUSTOM")
				.save(failOnError:true, flush:true)
			def message = TextMessage.build(text:'steppilize this')
			a.processKeyword(message, null)
			controller.params.ownerId = a.id
			controller.params.starred = false
			controller.params.stepId = joinStep.id
		when:
			controller.activity()
		then:
			controller.modelAndView.model.interactionInstanceList.size() == 1
		when:
			controller.params.stepId = replyStep.id
			controller.params.ownerId = a.id
			controller.activity()
		then:
			controller.modelAndView.model.interactionInstanceList.size() == 2
	}

	def "steps should be edited when editing a custom activity not removed"() {
		given:
			def joinStep = new JoinActionStep().addToStepProperties(new StepProperty(key:"group", value:"1"))
			def leaveStep = new LeaveActionStep().addToStepProperties(new StepProperty(key:"group", value:"2"))

			def a = new CustomActivity(name:'Do it all')
				.addToSteps(joinStep)
				.addToSteps(leaveStep)
				.addToKeywords(value:"CUSTOM")
				.save(failOnError:true, flush:true)

			controller.params.name = "just edited"
			controller.params.keywords = "new, just, yeah"
			controller.params.sorting = "enabled"
			controller.params.ownerId = a.id
			controller.params.jsonToSubmit = """[{"stepId":"${joinStep.id}","stepType":"join", "group":"2"}, {"stepId":"","stepType":"reply", "autoreplyText":"where is the help controller" }]"""
		when:
			controller.save()
		then:
			def activity = CustomActivity.findByName("just edited")
			activity.keywords*.value.containsAll(["NEW", "JUST", "YEAH"])
			activity.steps*.id.contains(joinStep.id)
			activity.steps.size() == 2
			activity.steps*.stepProperties*.flatten().value.containsAll([["2"], ["where is the help controller"]])
	}
}
