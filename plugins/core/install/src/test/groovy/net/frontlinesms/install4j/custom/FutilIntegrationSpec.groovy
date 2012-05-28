package net.frontlinesms.install4j.custom

import spock.lang.*

class FutilIntegrationSpec extends Specification {
	def setup() {
		// make sure the registration file does not exist
		def regFile = getRegistrationFile()
		if(regFile.exists()) regFile.delete()
		assert !regFile.exists()
	}

	def 'createRegistrationPropertiesFile should create parent folder if it doesnt exist'() {
		given:
			def settings = getFrontlinesmsSettingsDirectory()
			assert settings.deleteDir()
		when:
			Futil.createRegistrationPropertiesFile('1234', true)
		then:
			settings.exists()
	}

	def 'registration file should be created at the approprtate location'() {
		when:
			Futil.createRegistrationPropertiesFile('1234', true)
		then:
			getRegistrationFile().exists()
	}

	def 'registration properties file should have some content'() {
		when:
			Futil.createRegistrationPropertiesFile('1234', true)
		then:
			getRegistrationFile().text
	}

	private def getFrontlinesmsSettingsDirectory() {
		new File(System.properties['user.home'], '.frontlinesms2')
	}

	private def getRegistrationFile() {
		new File(getFrontlinesmsSettingsDirectory(), 'registration.properties')
	}
}

