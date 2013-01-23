package frontlinesms2

import grails.converters.JSON

abstract class Step {
	static hasMany = [stepProperties: StepProperty]
	static def implementations = [JoinActionStep, LeaveActionStep, ReplyActionStep]

	static configFields = [:]

	static constraints = {
		// the following assumes all configFields are mandatory
		stepProperties(nullable: true)
	}
	
	abstract def process(Fmessage message)

	String getPropertyValue(key) {
		stepProperties?.find { it.key == key }?.value
	}

	def setPropertyValue(key, value){
		stepProperties?.find { it.key == key }?.value = value
	}

	// helper method to retrieve list of entities saved as StepProperties
	def getEntityList(entityType, propertyName) {
		entityType.getAll(StepProperty.findAllByStepAndKey(this, propertyName)*.value) - null
	}

	String getJsonConfig() {
		return getConfig() as JSON
	}
}
