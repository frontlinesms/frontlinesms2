includeTargets << grailsScript("Init") << grailsScript("War")

def envCheck = {
	if(grailsSettings.grailsEnv != 'production') {
		def middleLine = "# !! WARNING !! You are building for $grailsSettings.grailsEnv !! WARNING !! #"
		println '#' * middleLine.size()
		println middleLine
		println '#' * middleLine.size()
	}
}

def isSet(String var) {
	return System.properties."frontlinesms2.build.$var"? true: false
}

def isWindows() {
	System.properties.'os.name'.toLowerCase().contains('windows')
}

target(main: 'Build installers for various platforms.') {
	envCheck()
	if(isSet('skipWar')) {
		if(grailsSettings.grailsEnv == 'production') {
			println "CANNOT SKIP WAR BUILD FOR PRODUCTION"
			depends(clean, war)
		} else {
			println "Skipping WAR build..."
			depends(clean)
		}
	} else depends(clean, war)
	def appName = metadata.'app.name'
	def appVersion = metadata.'app.version'
	println "Building $appName, v$appVersion"
	delete(dir:'install/webapp')
	unzip(src:"target/${appName}-${appVersion}.war", dest:'install/webapp')
	
	exec(dir:'install', executable:isWindows()? 'mvn.bat': 'mvn', args) {
		arg value:"-Dbuild.version=$appVersion"
		arg value:'clean'
		arg value:'package'
	}
	envCheck()
}

setDefaultTarget(main)
