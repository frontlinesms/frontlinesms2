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
	return getValue(var)? true: false
}

def getValue(String var) {
	return System.properties."frontlinesms2.build.$var"
}

def isWindows() {
	System.properties.'os.name'.toLowerCase().contains('windows')
}

def mvn() {
	return isWindows()? 'mvn.bat': 'mvn'
}

target(main: 'Build installers for various platforms.') {
	envCheck()
	if(grailsSettings.grailsEnv != 'production') {
		input('Press Return to continue building...')
	}
	if(isSet('skipWar')) {
		if(grailsSettings.grailsEnv == 'production') {
			println "CANNOT SKIP WAR BUILD FOR PRODUCTION"
			depends(clean, war)
		} else {
			println "Skipping WAR build..."
			depends(clean)
		}
	} else depends(clean, war)
	if(!isWindows()) if(isSet('compress')? getValue('compress'): grailsSettings.grailsEnv == 'production') {
		println 'Forcing compression of installers...'
		exec executable:'do/enable_installer_compression'
	} else {
		println 'Disabling compression of installers...'
		exec executable:'do/disable_installer_compression'
	}
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
