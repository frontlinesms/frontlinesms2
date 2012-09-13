def totalViolations = 0
final def I18N_VIOLATION_REGEX = /[>]\s*[[\s*.:!+@;'?()$,{}]*\s*0-9a-zA-Z\s]*+\s*[<]*/
final def DIR_ROOT = "grails-app/views"
new File(DIR_ROOT).eachFileRecurse { file ->
	if (file.isFile()) {
		def lineNumber = 0
		file.eachLine { line ->
			lineNumber++
			def result = line.findAll(I18N_VIOLATION_REGEX)
			if(result.any { it && it != '>' && it != '><' }) {
				println "$DIR_ROOT/$file.name:$lineNumber:$line"
				++totalViolations
			}
		}
	}   
} 
println "Total violations: $totalViolations"

