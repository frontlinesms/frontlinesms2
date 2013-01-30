/* Script to compare grails internationalisation property files (or any property files in general) */
if(args.size() < 2) {
	println("# compares i18n file to master message.properties")
	println("# usage: i18n-compare MASTER OTHER [APPLY-CHANGES] [NEW-FILE]")
	println("#    MASTER: The main property file, assumed to be correct")
	println("#    OTHER : The file to be compared to MASTER")
	return
}

def compare(fileA, fileB) {
	println "# Reporting on differences betweer $fileA and $fileB..."
	// read files into props
	master = new Properties()
	new File(fileA).withInputStream { stream -> master.load(stream); }
	other = new Properties()
	new File(fileB).withInputStream { stream -> other.load(stream); }

	// init lists
	def missingKeys = []
	def redundantKeys = []

	master.keySet().each {
		if((!other.get(it) || other.get(it).length() == 0) && !(master.get(it) ==~ /\s*\{[0-9]\}\s*$/ ))
		{
			missingKeys << it
		}
	}
	redundantKeys = other.keySet() - master.keySet()

	redundantKeys.each {
		other.remove(it)
	}
	missingKeys.each {
		other.put(it, "TODO:"+master.get(it))
	}

	langName = other.get("language.name")

	double perc = ((master.size() - missingKeys.size()) / master.size()) * 100
	println "# Redundant entries: ${redundantKeys.size()}, MissingEntries: ${missingKeys.size()}"
	println "# ${langName? langName + ' translation' : args[1]} is ${perc.round(2)}% complete"

	return perc == 100
}

def perc = compare(args[0], args[1])
System.exit perc == 100? 0: 1

