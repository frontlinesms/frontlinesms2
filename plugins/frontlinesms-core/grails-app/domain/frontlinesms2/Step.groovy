package frontlinesms2

abstract class Step {
	
	static hasMany = [stepProperties: StepProperty]
	static def implementations = [JoinActionStep, LeaveActionStep, ReplyActionStep]
	static String getShortName() { 'base' }

	static configFields = [:]

	static constraints = {
		// the following assumes all configFields are mandatory
		stepProperties(nullable: true)
	}
	
	def process(Fmessage message) {

	}

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
}
