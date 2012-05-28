package net.frontlinesms.install4j.custom

import spock.lang.*

class FutilIntegrationSpec extends Specification {
	def setup() {
		// make sure the registration file does not exist
		def regFile = getRegistrationFile()
		if(regFile.exists()) regFile.delete()
		assert !regFile.exists()
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

	private def getRegistrationFile() {
		new File(new File(System.properties['user.home'], '.frontlinesms2'), 'registration.properties')
	}
}

