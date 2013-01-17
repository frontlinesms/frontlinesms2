package frontlinesms2

abstract class Step {
	
	static hasMany = [stepProperties: StepProperty]
	static def implementations = [JoinActionStep, LeaveActionStep, ReplyActionStep]
	static String getShortName() { 'base' }

	static configFields = [:]

	static constraints = {
		// the following assumes all configFields are mandatory
		stepProperties(nullable: true, validator: { val, obj ->
			if (!val) return false
			val*.key?.containsAll(obj.configFields?.collect { name, type -> name })
		})
	}
	
	def process(Fmessage message) {

	}

	String getPropertyValue(key) {
		stepProperties?.find { it.key == key }?.value
	}

	// helper method to retrieve list of entities saved as StepProperties
	def getEntityList(entityType, propertyName) {
		entityType.getAll(StepProperty.findAllByStepAndKey(this, propertyName)*.value) - null
	}
}
