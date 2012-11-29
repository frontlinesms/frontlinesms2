package frontlinesms2

abstract class Step {
	String type
	static hasMany = [stepProperties: StepProperty]
	static configFields = [:]

	static constraints = {
		// the following assumes all configFields are mandatory
		stepProperties(nullable: true, validator: { val, obj ->
			val*.key?.containsAll(obj.configFields?.collect { name, type -> name })
		})
	}
	
	def process(Fmessage message) {

	}
}
