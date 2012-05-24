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

def mvn() {
	return isWindows()? 'mvn.bat': 'mvn'
}

target(main: 'Build installers for various platforms.') {
	envCheck()
	if(grailsSettings.grailsEnv != 'production')
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
	def webappTempDir = 'install/src/web-app'
	delete(dir:webappTempDir)
	unzip(src:"target/${appName}-${appVersion}.war", dest:webappTempDir)

	// Build instal4j custom code JAR
	exec(dir:'install', output:'install4j-custom-classes.maven.log', executable:mvn(), args) {
		arg value:'-f'
		arg value:'install4j-custom-classes.pom.xml'
		arg value:'clean'
		arg value:'package'
	}
	
	// Build installers
	exec(dir:'install', output:'install4j.maven.log', executable:mvn(), args) {
		arg value:"-Dbuild.version=$appVersion"
		arg value:'clean'
		arg value:'package'
	}
	envCheck()
}

setDefaultTarget(main)
