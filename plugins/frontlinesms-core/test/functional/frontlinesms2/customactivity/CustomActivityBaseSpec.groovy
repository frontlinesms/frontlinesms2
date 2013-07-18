package frontlinesms2.customactivity

import frontlinesms2.*

class CustomActivityBaseSpec extends grails.plugin.geb.GebSpec {
	def createTestCustomActivities() {
		remote {
			def joinStep = new JoinActionStep().addToStepProperties(new StepProperty(key:"group", value:Group.list()[0].id))
			def leaveStep = new JoinActionStep().addToStepProperties(new StepProperty(key:"group", value:Group.list()[1].id))

			new CustomActivity(name:'Do it all')
				.addToSteps(joinStep)
				.addToSteps(leaveStep)
				.addToKeywords(value:"CUSTOM")
				.save(failOnError:true, flush:true)
			null
		}
	}

	def createTestGroups() {
		remote {
			new Group(name:"Camping").save(failOnError:true, flush:true)
			new Group(name:"Eating").save(failOnError:true, flush:true)
			null
		}
	}
}

