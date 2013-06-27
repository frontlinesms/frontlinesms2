import grails.util.Environment

def getApplicationProperty(key) {
	def MATCHER = key.replace('.', /\./) + /=(.*)/
	def match
	new File('application.properties').eachLine {
		if(it ==~ MATCHER) match = (it =~ MATCHER)[0][1]
	}
	return match
}

eventCompileStart = { kind ->
	ant.exec executable:'do/clean_naughty_camel'
	if(Environment.current == Environment.PRODUCTION) {
		// Check we have no snapshot dependencies
		if(new File('grails-app/conf/BuildConfig.groovy').text.contains('SNAPSHOT')) {
			println '##################################'
			println '# YOU HAVE SNAPSHOT DEPENDENCIES #'
			println '##################################'

			def appVersion = getApplicationProperty('app.version')
			def allowSnapshots = Boolean.parseBoolean(System.properties.'frontlinesms2.build.allowSnapshots')
			println "allowSnapshots=$allowSnapshots"
			if(appVersion.contains('SNAPSHOT')) {
				if(System.getenv('frontlinesms.build.env') == 'online-dev' ||
						allowSnapshots) {
					// let it slide
				} else {
					println '# Press ENTER to continue...'
					System.in.withReader { it.readLine() }
				}
			} else {
				println '# You cannot include SNAPSHOT dependencies in a release.'
				println '# Build terminating.'
				System.exit(1);
			}
		}
	}
}

eventCompileEnd = {
	def folderMap = [
		"grails-app/i18n":"web-app/WEB-INF/grails-app/i18n"
	]
	folderMap.each { oldLocation, newLocation ->
		ant.copy(toDir: newLocation) {
			fileset(dir:oldLocation)
		}	
	}

	// Rewrite the message properties files as JSON so they are available in Javascript in the app
	def appName = getApplicationProperty('app.name')
println "appName: $appName"
	new File('web-app/i18n').mkdir()
	new File('grails-app/i18n/').listFiles().each { f ->
		def builder = new groovy.json.JsonBuilder()
		def props = new Properties()
		f.withReader 'UTF-8', { reader -> props.load(reader) }

		def m = [:]
		props.each { k, v -> m[k] = v }
		builder(m)

		jsFilename = f.name - '.properties' + '.js'
		new File("web-app/i18n/$jsFilename").setText(
				"var i18nStrings = i18nStrings || {}; i18nStrings[\"$appName\"] = $builder;",
				'UTF-8')
	}
}

