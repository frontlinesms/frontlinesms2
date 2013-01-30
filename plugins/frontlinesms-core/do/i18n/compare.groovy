/* Script to compare grails internationalisation property files (or any property files in general) */
if(args.size() < 2) {
	println("# compares i18n file to master message.properties")
	println("# usage: i18n-compare MASTER OTHER [APPLY-CHANGES] [NEW-FILE]")
	println("#    MASTER: The main property file, assumed to be correct")
	println("#    OTHER : The file to be compared to MASTER")
	return
}

def loadProps(filename) {
	def props = new Properties()
	new File(filename).withInputStream { stream -> props.load(stream); }
	return props
}

def compare(fileA, fileB) {
	println "# Reporting on differences betweer $fileA and $fileB..."
	// read files into props
	master = loadProps(fileA)
	other = loadProps(fileB)

	// init lists
	def missingKeys = (master.keySet().findAll {
		(!other[it] || other[it].length() == 0) &&
				!(master[it] ==~ /\s*\{[0-9]\}\s*$/ )
	}).size()
	def redundantKeys = (other.keySet() - master.keySet()).size()
	def langName = other["language.name"]

	double perc = ((master.size() - missingKeys) / master.size()) * 100
	println "# Redundant entries: $redundantKeys, MissingEntries: $missingKeys"
	println "# ${langName? langName + ' translation' : fileB} is ${perc.round(2)}% complete"

	return perc == 100
}

def perc = compare(args[0], args[1])
System.exit perc == 100? 0: 1

