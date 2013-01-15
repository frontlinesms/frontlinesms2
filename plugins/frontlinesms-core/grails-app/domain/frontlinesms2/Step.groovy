package frontlinesms2

import grails.converters.JSON

abstract class Step {
	static belongsTo = [activity: CustomActivity]
	static hasMany = [stepProperties: StepProperty]
	static def implementations = [JoinActionStep, LeaveActionStep, ReplyActionStep]

	static configFields = [:]

	static constraints = {
		stepProperties(nullable: true)
	}
	
	abstract def process(Fmessage message)

	String getPropertyValue(key) {
		stepProperties?.find { it.key == key }?.value
	}

	def setPropertyValue(key, value){
		stepProperties?.find { it.key == key }?.value = value
	}

	String getJsonConfig() {
		return getConfig() as JSON
	}
	// helper method to retrieve list of entities saved as StepProperties
	def getEntityList(entityType, propertyName) {
		println "ALL STEP PROPERTIES::: ${stepProperties.findAll { true }.collect { it.key + ' ' + it.value }}"
		println "entityType getall: ${entityType.getAll([1,2])}"
		entityType.getAll(stepProperties.findAll { it.key == propertyName }*.value) - null
	}
}
