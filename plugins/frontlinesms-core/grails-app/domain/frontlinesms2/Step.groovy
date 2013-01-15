package frontlinesms2

abstract class Step {
	String type
	static hasMany = [stepProperties: StepProperty]
	static configFields = [:]

	static constraints = {
		
	}
	
	def process(Fmessage message) {

	}

	String getPropertyValue(key) {
		stepProperties?.find { it.key == key }?.value
	}

	// helper method to retrieve list of entities saved as StepProperties
	def getEntityList(entityType, propertyName) {
		println "ALL STEP PROPERTIES::: ${stepProperties.findAll { true }.collect { it.key + ' ' + it.value }}"
		println "entityType getall: ${entityType.getAll([1,2])}"
		entityType.getAll(stepProperties.findAll { it.key == propertyName }*.value) - null
	}
}
