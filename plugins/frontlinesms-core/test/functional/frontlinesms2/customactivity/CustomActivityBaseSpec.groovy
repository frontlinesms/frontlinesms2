package frontlinesms2.customactivity

import frontlinesms2.*

class CustomActivityBaseSpec extends grails.plugin.geb.GebSpec {
	def createTestCustomActivities() {
		def joinStep = new JoinActionStep().addToStepProperties(new StepProperty(key:"group", value:"1")).save(failOnError:true,flush:true)
		def leaveStep = new JoinActionStep().addToStepProperties(new StepProperty(key:"group", value:"2")).save(failOnError:true,flush:true)

		new CustomActivity(name:'Do it all')
			.addToSteps(joinStep)
			.addToSteps(leaveStep)
			.addToKeywords(value:"CUSTOM")
			.save(failOnError:true, flush:true)
	}

	def createTestGroups() {
		new Group(name:"Camping").save(failOnError:true)
		new Group(name:"Eating").save(failOnError:true)
	}
}