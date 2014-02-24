import grails.util.BuildScope

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

def getValueAsBoolean(String var, boolean defaultValue) {
	if(isSet(var)) return Boolean.parseBoolean(getValue(var))
	else return defaultValue
}

def isWindows() {
	System.properties.'os.name'.toLowerCase().contains('windows')
}

def mvn() {
	return isWindows()? 'mvn.bat': 'mvn'
}

def doScript(name) {
	ant.exec executable:"../frontlinesms-core/do/$name"
}

def isReleaseBuild() {
	def appVersion = metadata['app.version'].toUpperCase()
	return !(appVersion.contains('SNAPSHOT') || appVersion.contains('RC'))
}

target(clearPluginXmls: 'Delete plugin.xml from all in-place plugins') {
	delete {
		fileset dir:'..', includes:'*/plugin.xml'
	}
}

target(main: 'Build installers for various platforms.') {
	buildScope = BuildScope.WAR
	clearPluginXmls()
	envCheck()
	if(!getValueAsBoolean('confirmNotProd', grailsSettings.grailsEnv == 'production')) {
		input('Press Return to continue building...')
	}
	// begin horrible manual cleaning
	new File('FrontlinesmsCoreGrailsPlugin.groovy').delete()
	new File('.', 'target').deleteDir()
	// end horrible manual cleaning
	if(getValueAsBoolean('skipWar', false)) {
		if(grailsSettings.grailsEnv == 'production') {
			println "CANNOT SKIP WAR BUILD FOR PRODUCTION"
			depends(clean, war)
		} else {
			println "Skipping WAR build..."
			depends(clean)
		}
	} else {
		depends(clean, war)
	}
	if(!isWindows()) if(getValueAsBoolean('compress', grailsSettings.grailsEnv == 'production')) {
		println 'Forcing compression of installers...'
		doScript 'enable_installer_compression'
	} else {
		println 'Disabling compression of installers...'
		doScript 'disable_installer_compression'
	}
	if(getValueAsBoolean('resources.asRelease', isReleaseBuild())) {
		println 'Changing resource paths for installed app to RELEASE options...'
		doScript 'remove_snapshot_from_install_resource_directories'
	} else {
		println 'Changing resource paths for installed app to SNAPSHOT options...'
		doScript 'add_snapshot_to_install_resource_directories'
	}
	if(isReleaseBuild() || getValueAsBoolean('db.migrations', true)) {
		println 'Enabling database migrations...'
		doScript 'installer_dbmigration_enable'
	} else {
		println 'Disabling database migrations...'
		doScript 'installer_dbmigration_disable'
	}
	def appName = metadata.'app.name'
	def appVersion = metadata.'app.version'
	println "Building $appName, v$appVersion"
	def webappTempDir = '../frontlinesms-core/install/src/web-app'
	delete(dir:webappTempDir)
	unzip(src:"target/${appName}-${appVersion}.war", dest:webappTempDir)

	// Build instal4j custom code JAR
	exec(dir:'../frontlinesms-core/install', output:'install4j-custom-classes.maven.log', executable:mvn(), args) {
		arg value:'-f'
		arg value:'install4j-custom-classes.pom.xml'
		arg value:'clean'
		arg value:'package'
	}
	
	// Build installers
	exec(dir:'../frontlinesms-core/install', output:'install4j.maven.log', executable:mvn(), args) {
		arg value:"-Dbuild.version=$appVersion"
		arg value:"-Dfrontlinesms.flavour=$appName"
		arg value:'clean'
		arg value:'package'
	}

	// Make sure that linux installer is executable
	chmod dir:'../frontlinesms-core/install/target/install4j', includes:'*.sh', type:'file', perm:'a+x'

	exec(executable:'/usr/bin/env') {
		arg value:'git'
		arg value:'checkout'
		arg value:'--'
		arg value:'FrontlinesmsCoreGrailsPlugin.groovy'
	}

	envCheck()
}

setDefaultTarget(main)
