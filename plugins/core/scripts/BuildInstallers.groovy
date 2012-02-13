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

target(main: 'Build installers for various platforms.') {
	envCheck()
	println "args: $args"
	if(isSet('skipWar')) {
		println "Skipping WAR build..."
		depends(clean)
	} else depends(clean, war)
	def appName = metadata.'app.name'
	def appVersion = metadata.'app.version'
	println "Building $appName, v$appVersion"
	delete(dir:'install/webapp')
	unzip(src:"target/${appName}-${appVersion}.war", dest:'install/webapp')
	
	def compress = !isSet('skipCompression') && grailsSettings.grailsEnv=='production'
	println "Compressing installer? $compress"
	exec(dir:'install', executable:'mvn', args) {
		arg value:"-Dbuild.version=$appVersion"
		arg value:"-Dbuild.compress=$compress"
		arg value:'clean'
		arg value:'package'
	}
	envCheck()
}

setDefaultTarget(main)
