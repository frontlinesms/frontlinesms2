package frontlinesms2

abstract class Step {
	String type
	static hasMany = [stepProperties: StepProperty]	

	static constraints = {
		stepProperties nullable: true
	}
	
	def process(Fmessage message) {

	}
}
