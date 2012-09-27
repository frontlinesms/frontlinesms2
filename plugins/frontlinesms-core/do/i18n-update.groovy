/* Script to compare grails internationalisation property files (or any property files in general)
 * and automatically update an incomplete file with missing keys from the master file (with TODOs).
 *
*/
if(args.size() < 2)
{
	println("updates a translation file with missing entries present in message.properties (or other 'master' file)")
	println("usage: groovy i18n-update.groovy MASTER OTHER [NEW-FILE]")
	println("   MASTER: The main property file, assumed to be complete")
	println("   OTHER : The file to be updated")
	println("   NEW-FILE (optional): filename to write output to. (if not supplied, changes will be applied to OTHER)")
	return
}

File master = new File(args[0])
File slave = new File(args[1])
def targetFile = args.size() >= 3 ? args[2] : args[1]
println "Applying changes to $targetFile"

if (targetFile == args[1]) {
	def s = "*  WARNING: This will overwrite the existing file $targetFile. Press ENTER to continue, or Ctrl+C to terminate  *"
	(1..s.length()).each { print('*') }
	println "\n$s"
	(1..s.length()).each { print('*') }
	println ""
	def input = System.in.withReader{ it.readLine() }
}

String currentLine

def existingSlaveLines = []
def newSlaveLines = []
slave.eachLine { line -> existingSlaveLines << line }

master.eachLine { masterLine ->
	if(masterLine.isAllWhitespace() || masterLine.trim().startsWith("#")) {
		// This is a comment or whitespace, preserve it
		newSlaveLines << masterLine
	}
	else if (masterLine.contains('=')) {
		// This is a property. Check if the other translation has it, and if not, copy it with a TODO
		def key = masterLine.split('=')[0]
		def masterValue = masterLine.split('=')[1]

		newSlaveLines << (existingSlaveLines.find { it.startsWith(key+"=") } ?: masterLine.replaceFirst("=", "=TODO:"))
	}
	else {
		// Something strange is going on, we should only have props, comments or whitespace!
	}
}

new File(targetFile).withWriter { out -> 
	newSlaveLines.each { line ->
		out.writeLine(line)
	}
}
