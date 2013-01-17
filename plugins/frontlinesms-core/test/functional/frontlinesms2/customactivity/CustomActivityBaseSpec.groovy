package frontlinesms2.customactivity

import frontlinesms2.*

class CustomActivityBaseSpec extends grails.plugin.geb.GebSpec {
	def createTestCustomActivities() {

	}

	def createTestGroups() {
		new Group(name:"Camping").save(failOnError:true)
		new Group(name:"Eating").save(failOnError:true)
	}
}