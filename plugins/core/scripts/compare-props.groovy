/* Script to compare grails internationalisation property files (or any property files in general)
 * and automatically update an incomplete file with missing keys from the master file (with TODOs).
 *
 * Future enhancements:
 * - preserve order of entries from master file when copying to slave file (and rearrange existing slave props)
 * - copy comments from master to slave, preserving existing comments in slave
 * - TODOs break fields that take args, may want to reconsider placing them at beginning of line
 * - use some mechanism (grep?) to identify redundant entries in master file (potentially in another script)
*/
if(args.size() < 2)
{
	println("compares i18n file to master message.properties")
	println("usage: compare-props MASTER OTHER [APPLY-CHANGES] [NEW-FILE]")
	println("   MASTER: The main property file, assumed to be correct")
	println("   OTHER : The file to be compared to MASTER")
	println("   APPLY-CHANGES (optional): if true/y/1, redundant entries will be deleted from OTHER, and missing ones entered, with values copied from MASTER as TODOs")
	println("   NEW-FILE (optional): filename to write output to. (if not supplied, changes will be applied to OTHER)")
	return
}
def enactChanges = args.size() >= 3 ? args[2].toBoolean() : false
def targetFile = args.size() >= 4 ? args[3] : args[1]
println (enactChanges? "Applying changes to $targetFile" : "Reporting on differences..")

// read files into props
master = new Properties()
new File(args[0]).withInputStream { stream -> master.load(stream); }
other = new Properties()
new File(args[1]).withInputStream { stream -> other.load(stream); }

// init lists
def missingKeys = []
def redundantKeys = []

missingKeys = master.keySet() - other.keySet()
redundantKeys = other.keySet() - master.keySet()

redundantKeys.each {
	other.remove(it)
}
missingKeys.each {
	other.put(it, "TODO:"+master.get(it))
}

if(enactChanges)
	new File(targetFile).withOutputStream { stream -> other.save(stream, "Processed by FSMS i18n script"); }
else
	println("Redundant entries: ${redundantKeys.size()}, MissingEntries: ${missingKeys.size()}")