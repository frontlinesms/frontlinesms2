/* Script to compare grails internationalisation property files (or any property files in general)
 *
*/
if(args.size() < 2)
{
	println("compares i18n file to master message.properties")
	println("usage: i18n-compare MASTER OTHER [APPLY-CHANGES] [NEW-FILE]")
	println("   MASTER: The main property file, assumed to be correct")
	println("   OTHER : The file to be compared to MASTER")
	return
}
println "Reporting on differences betweer ${args[0]} and ${args[1]}.."

// read files into props
master = new Properties()
new File(args[0]).withInputStream { stream -> master.load(stream); }
other = new Properties()
new File(args[1]).withInputStream { stream -> other.load(stream); }

// init lists
def missingKeys = []
def redundantKeys = []

master.keySet().each {
	if(!other.get(it) || other.get(it).length() == 0)
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
println("Redundant entries: ${redundantKeys.size()}, MissingEntries: ${missingKeys.size()}")
println "${langName? langName + ' translation' : args[1]} is ${perc.round(2)}% complete"