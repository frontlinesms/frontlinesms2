package frontlinesms2

import grails.converters.JSON

abstract class Step {

	def i18nUtilService
	
	static belongsTo = [activity: CustomActivity]
	static hasMany = [stepProperties: StepProperty]
	static def implementations = [JoinActionStep, LeaveActionStep, ReplyActionStep]

	static transients = ['i18nUtilService']
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
		def prop = stepProperties?.find { it.key == key }
		prop? (prop.value = value) : this.addToStepProperties(key:key, value:value)
	}

	// helper method to retrieve list of entities saved as StepProperties
	def getEntityList(entityType, propertyName) {
		entityType.getAll(StepProperty.findAllByStepAndKey(this, propertyName)*.value) - null
	}

	String getJsonConfig() {
		return getConfig() as JSON
	}

	def activate() {}
	def deactivate() {}
}
