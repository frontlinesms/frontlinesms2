package frontlinesms2

class StepProperty {
	static belongsTo = [step: Step]
	static constraints = {

	}
	String key
	String value
}
